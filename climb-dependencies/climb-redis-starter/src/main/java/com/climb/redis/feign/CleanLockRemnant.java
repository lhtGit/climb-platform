package com.climb.redis.feign;


import com.climb.redis.lock.LockContext;

/**
 * 用于lock清除残余
 * @author lht
 * @since 2021/2/26 11:47
 */
public interface CleanLockRemnant {
    default void clean(){
        LockContext.removeAll();
    }
}
