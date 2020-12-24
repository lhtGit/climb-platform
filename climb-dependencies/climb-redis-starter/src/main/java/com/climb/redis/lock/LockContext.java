package com.climb.redis.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lht
 * @since 2020/12/17 13:39
 */
@Slf4j
public class LockContext {

    public static final String KEY_LOCK = "key_lock";

    private static final ThreadLocal<Set<String>> LOCK_THREAD = ThreadLocal.withInitial(HashSet::new);


    /**
     * 是否已经加锁
     * @author lht
     * @since  2020/12/17 14:23
     * @param key
     */
    public static boolean isLock(String key){
        return LOCK_THREAD.get().contains(key);
    }


    /**
     * 批量记录lock
     * @author lht
     * @since  2020/12/17 15:50
     * @param keysReq
     */
    public static void recordLock(Collection<String> keysReq){
        Set<String> keys = LOCK_THREAD.get();
        keys.addAll(keysReq);
    }
    /**
     * 记录lock
     * @author lht
     * @since  2020/12/17 15:42
     * @param key
     */
    public static void recordLock(String key){
        Set<String> keys = LOCK_THREAD.get();
        keys.add(key);
    }


    /**
     * 获取当前上下文
     * @author lht
     * @since  2020/12/17 14:34
     * @param
     */
    public static Set<String> getRecordedKeys(){
        return LOCK_THREAD.get();
    }



    /**
     * 清除当前lockid 的 key  lock
     * @author lht
     * @since  2020/12/17 16:15
     * @param key
     */
    public static void remove(String key){
        Set<String> keys = LOCK_THREAD.get();
        keys.remove(key);
    }
    /**
     * 清除当前lockid lock
     * @author lht
     * @since  2020/12/17 14:46
     * @param
     */
    public static void removeAll(){
        LOCK_THREAD.remove();
    }
}
