package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * java异常主要分编译期异常和运行期异常
 * 分编译期异常不需要手动的try-catch
 * spring声明式事物只接收 运行期异常回滚策略
 * Create by Wang Mingzhen om 2018/4/25
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
