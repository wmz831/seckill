package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Wang Mingzhen om 2018/4/25
 *
 * @Component 代表所有的组建, 统称组建实例
 * @pository
 * @Service
 * @Controller
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    //统一的日志api
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /* 在spring容器中获取实例，然后注入到service中
     * @Autowired   属于spring的注解，默认按配型装配，多个同类型bean时可结合@Qualifier("xxx")消除歧义
     * @Resource    JSR250规范，默认按名称装配
     * @Inject      JSR330规范的注解，与@Autowired基本一致
     */
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //混淆效果的加密盐,md5盐值，用于混淆MD5。最好用一长串无意义的混乱字符串
    private final String salt = "qwer";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        /**
         * Service层不关心对象从哪里获取的，不论从redis获取，还是从数据库获取，只要拿到对象数据就行
         * 优化点：缓存优化，一致性维护：建立在超时的基础上，一旦建立不能更改，可以废弃重建
         * 注意：真是生产环境中，Redis不是singleton环境（一台机器），而是集群；且访问逻辑、一致性维护不会这么简单
         */
        //1:访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2:访问数据库
            //byId主键访问，速度够快，但为了减轻DB负载，还是采用Redis缓存优化一下
            seckill = seckillDao.queryById(seckillId);
            //id不存在
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3:放入redis
                redisDao.putSeckill(seckill);
            }
        }

        //存在但不满足条件
        long nowTime = new Date().getTime();
        long startTime = seckill.getStartTime().getTime();
        long entTime = seckill.getEndTime().getTime();
        if (nowTime < startTime
                || entTime < nowTime) {//或者不转long用Date的.before()和.after()方法
            return new Exposer(false, nowTime, startTime, entTime);
        }

        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        //存在且满足条件
        return new Exposer(true, md5, seckillId);
    }

    //使用spring自带的md5
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 秒杀执行方法
     * 注意：这里try-catch块中线捕捉异常可以写入日志，再抛出异常时为了触发spring声明式事务回滚
     * <p>
     * 使用注解控制事务方法的优点：
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格；事务用不好会造成数据库链接、提交的延迟和阻塞
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，如RPC/HTTP请求 或者剥离到事务方法外部，（RPC:缓存/redis/...）
     * 因为在写入操作时会锁住数据，类似秒杀这类业务，且基于数据库实现的，最好保证dao操作简洁
     * 3：不是所有的方法需要事务，如单条修改，只读操作...
     * <p>
     * 2018-05-23 高并发优化
     * 对原有的逻辑进行调整：因为rowLock会在减库存时发生，所以将insert操作提前
     * 减库存->insert明细->commit 改为 insert明细->减库存->commit
     *
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        //调用getMD5()方法以确保id与md5匹配
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill date rewrite");//秒杀数据被重写,md5异常
        }

        //执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();

        //再次try-catche，是因为在操作过程中还会存在一些未知的异常，所以用整体Exception括起来
        try {

            //操作成功，记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一：seckillId,userPhone
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeat!");
            } else {
                //减库存，热点商品的竞争,拿到Mysql的行级锁rowLock
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录，秒杀结束,rollback；因为并发量很高，所以不必关心具体原因
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //插入成功 commit
                    SuccessKilled successKilled = successKilledDao.queryWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            logger.error(e1.getMessage(), e1);
            throw e1;
        } catch (RepeatKillException e2) {
            logger.error(e2.getMessage(), e2);
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常，转化为运行期异常;
            // 因为SeckillException继承自RuntimeException，所以可以用spring声明式事务来rollback回滚
            throw new SeckillException("Seckill inner error:" + e.getMessage());
        }

    }

    //todo 通过存储过程代替事务操操作
    public SeckillExecution executeSeckillProc(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        //执行存储过程，result会被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
        }
    }

}
