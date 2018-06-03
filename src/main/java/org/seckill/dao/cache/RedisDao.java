package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Create by Wang Mingzhen om 2018/5/19
 * 缓存代码可以放在service层(SeckillServiceImpl.exportSeckillUrl方法)，但建议放在dao层
 * 常用缓存伪代码：
 * get from cache
 * if null
 *   get DB
 *   put cache
 * return ...
 *
 * 序列化：Object -> Byte[] 将对象的状态信息转换为可以存储或传输的形式（字节序列）的过程
 * 反序列化：byte[] -> Object 是指从序列化的表示形式中提取数据，并直接设置对象状态
 * 对于任何可能包含重要的安全性数据的对象，如果可能，应该使该对象不可序列化。如果它必须为可序列化的，可尝试生成特定字段来保存不可序列化的重要数据
 * 如果无法实现这一点，则应注意该数据会被公开给任何拥有序列化权限的代码，并确保不让任何恶意代码获得该权限。
 * 一般java对象需要序列化可用jdk自己的序列化机制，即让bean实现Serializable接口即可，注意给serialVersionUID赋值，transient 关键字修饰的属性不会被序列化
 */

public class RedisDao {

    //Jedis连接，类似于DB的Connection Pool
    private final JedisPool jedisPool;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 类的字节码(Seckill.class)通过反射可以拿到该类的属性、方法，基于此做一个模式，创建对象时根据模式赋予相应的值，反射即是实现序列化的本质
     */
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //使用构造器注入
    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip,port);
    }

    /**
     * 从redis中获取seckill对象
     * @param seckillId
     * @return
     */
    //
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillId:"+seckillId;
                /**
                 * redis或jedis并没有实现 内部序列化 操作
                 * 缓存访问逻辑：get -> byte[] -> 反序列化 -> Object(seckill)
                 * 高并发中很容易被忽视但很重要的点：序列化
                 * 这里不用bean实现Serializable接口的方式，采用protostuff自定义序列化
                 * pojo
                 */
                //是一个反序列化的过程：get -> byte[] -> Object(seckill)
                byte[] bytes = jedis.get(key.getBytes());//根据id得到该对象
                if(bytes !=null){
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    return seckill;
                }

            }finally {
//                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 当缓存中没有时，put该seckill对象
     * @param seckill
     * @return 正确：OK  错误：错误信息
     */
    public String putSeckill(Seckill seckill){
        //put是一个序列化的过程：Object(seckill) -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillId:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60 * 60;//缓存一小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                System.out.println("result:"+result);
                return result;
            } finally {
//                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void test(){
        try {
            Jedis jedis = jedisPool.getResource();
            System.out.println("test:"+jedis.get("test"));
//            jedis.set("java","java");
            System.out.println("java:"+jedis.get("java"));
//            jedis.setex("setex",3600,"setex");
            System.out.println("setex:"+jedis.get("setex"));

            byte[] bytes = {1,2,3};
//            jedis.setex(bytes,3600,bytes);
            System.out.println("bytes:"+jedis.get(bytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
