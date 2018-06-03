package org.seckill.dto;

/**
 * 暴露秒杀地址DTO
 * 不同构造器是为了方便对象初始化
 * dto是方便service返回的数据封装
 * Create by Wang Mingzhen om 2018/4/24
 */
public class Exposer {

    //是否开启秒杀
    private boolean exposed;

    //一种加密措施
    private String md5;

    //id
    private long seckillId;

    //系统当前时间(毫秒)
    private long now;

    //系统开始时间(毫秒)
    private long start;

    //系统结束时间(毫秒)
    private long end;

    //开启，返回
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    //未开启，所以给出时间
    public Exposer(boolean exposed, long now, long start, long end) {
        this.exposed = exposed;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    //异常或其他
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
