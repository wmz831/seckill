package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Create by Wang Mingzhen om 2018/4/19
 * 配置spring和junit整合，为了junit启动时加载spring IOC容器
 * spring-test,junit
 *
 * 最好创建一个BaseTest，放入配置文件
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
        /**
         * iphoneX
         * Seckill{seckillIdl=0, name='iphoneX', number=100, startTime=Sat Apr 14 00:00:00 CST 2018, endTime=Sat Apr 14 00:02:00 CST 2018, createTime=Fri Apr 13 23:47:18 CST 2018}
         */
    }

    @Test
    public void queryAll() {
        /**
         * Caused by: org.apache.ibatis.binding.BindingException:Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
         * java没有保存形参的记录：queryAll(offset,limit) -> queryAll(org0,org1),这里注意，当1个以上的形参时，需要用@Param 数据绑定一下
         */
        List<Seckill> result = seckillDao.queryAll(0,5);
        for (Seckill seckill : result) {
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() {
        int updateCount = seckillDao.reduceNumber(1001,new Date());
        System.out.println("updateCount:" + updateCount);
    }

}