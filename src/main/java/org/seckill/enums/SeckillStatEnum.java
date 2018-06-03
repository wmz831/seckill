package org.seckill.enums;

/**
 * 使用枚举表述常量数据字典
 * 一般数据字典会用一个常量，也可用数据库来存储
 *
 * 其实可以在DTO的SeckillExecution里面写枚举，但由于要把DTO对象转化成json用于ajax，默认的json在转换枚举时有问题
 * 也可根据枚举类型写一个专门的json转换器transfer
 *
 * Create by Wang Mingzhen om 2018/4/25
 */
public enum SeckillStatEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");//数据篡改也可以单独设置为一种异常类型

    private int state;

    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    /**
     * 在枚举中有valueOf(String str)可以根据参数自适应，但没有入参为int类型的，所以自己写一个
     * @param index
     * @return
     */
    public static SeckillStatEnum stateOf(int index) {
        for (SeckillStatEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
