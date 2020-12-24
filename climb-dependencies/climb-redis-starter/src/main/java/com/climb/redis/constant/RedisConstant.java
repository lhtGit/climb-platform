package com.climb.redis.constant;

/*
 *
 * @Author lht
 * @Date  2020/9/10 13:46
 */
public interface RedisConstant {
    /*
     * 分布式锁key
     */
    String DISTRIBUTED_LOCK_KEY = "lock:key";

    /*
     * redisTemplate<String,Object> key
     */
    String STR_OBJ_REDIS_TEMPLATE = "redisTemplate";

    /*
     * 数据中心id key
     */
    String DATA_CENTER_KEY = "identified:data";
}
