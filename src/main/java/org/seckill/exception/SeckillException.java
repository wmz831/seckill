package org.seckill.exception;

/**
 * 秒杀相关业务异常（通用异常）
 * Create by Wang Mingzhen om 2018/4/25
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
