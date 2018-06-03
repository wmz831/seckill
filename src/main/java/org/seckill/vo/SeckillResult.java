package org.seckill.vo;

/**
 * 所有aja请求的放回类型，封装json结果，泛型vo
 * Create by Wang Mingzhen om 2018/5/8
 */
public class SeckillResult<T> {

    //注意:这个success是请求是否成功，并不是秒杀是否才成功
    private boolean success;

    private T data;

    private String error;

    /**
     * true/false + 返回数据
     * @param success
     * @param data
     */
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * galse + 错误信息
     * @param success
     * @param error
     */
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
