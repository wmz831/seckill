package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by Wang Mingzhen om 2018/4/17
 */
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime 用sql语句来确保秒杀时间在范围之内，所以直接传入时间
     * @return int类型，如果影响行收>1，表示更新的记录行数；如果=0，表示不成功或异常
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * 查询
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset 偏移量
     * @param limit 多少条记录
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap 因为需要把result也传过去，且参数较多，所以用map
     */
    void killByProcedure(Map<String,Object> paramMap);
}
