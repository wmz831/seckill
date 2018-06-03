package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Create by Wang Mingzhen om 2018/5/21
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest{

    private long id = 1007;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill(){
        //get and put
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);
            if(seckill!=null){
                String result = redisDao.putSeckill(seckill);
                System.out.println("Redis put result:"+result);
                //put 后再get一次，测试是否成功
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }

        }

    }

    @Test
    public void getSeckill() {
    }

    @Test
    public void putSeckill() {
    }

    @Test
    public void testRedisConn(){
        redisDao.test();
    }
}