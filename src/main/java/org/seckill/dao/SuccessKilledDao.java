package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Create by Wang Mingzhen om 2018/4/17
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     * @param SeckillId
     * @param userPhone
     * @return 插入的结果集数量（行数）
     */
    int insertSuccessKilled(@Param("SeckillId") long SeckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询Successkilled并携带秒杀产品对象实体
     * @param SeckillId
     * @return SuccessKilled对象
     */
    SuccessKilled queryWithSeckill(@Param("SeckillId")long SeckillId, @Param("userPhone") long userPhone);

}
