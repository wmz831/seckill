package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Create by Wang Mingzhen om 2018/4/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao skd;

    @Test
    public void insertSuccessKilled() {
        /**
         * 第一次：  insertCount:1
         * 重复插入：insertCount:0
         */
        long SeckillId=1002L;
        long phone=1390L;
        int insertCount = skd.insertSuccessKilled(SeckillId,phone);
        System.out.println("insertCount:" + insertCount);
    }

    @Test
    public void queryWithSeckill() {
        SuccessKilled sk = skd.queryWithSeckill(1002L,1390);
        System.out.println(sk);
        System.out.println(sk.getSeckill());
    }
}