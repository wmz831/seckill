package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口:站在"使用者"角度去设计接口
 * 三个方面：方法定义颗粒度，参数(简练、直接)，返回类型(return 类型/异常)
 * Create by Wang Mingzhen om 2018/4/24
 */
public interface SeckillService {

    /**
     * 查询所有秒杀信息
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀信息
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时，输出秒杀接口的地址，否则输出系统时间和秒杀时间
     * 所有输出类型为void或为封装好的dto对象
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * throws 全部异常有些冗余，目的时为了告诉接口使用方，尽可能给出具体的错误
     * @param seckillId
     * @param userPhone
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 执行秒杀操作 by 存储过程
     * throws 全部异常有些冗余，目的时为了告诉接口使用方，尽可能给出具体的错误
     * @param seckillId
     * @param userPhone
     */
    SeckillExecution executeSeckillProc(long seckillId, long userPhone, String md5);
}
