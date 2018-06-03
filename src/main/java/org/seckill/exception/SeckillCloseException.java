package org.seckill.exception;

/**
 * 秒杀关闭异常
 * Create by Wang Mingzhen om 2018/4/25
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
