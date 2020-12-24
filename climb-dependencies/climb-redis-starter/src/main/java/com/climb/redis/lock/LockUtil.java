package com.climb.redis.lock;

import cn.hutool.core.util.IdUtil;
import com.climb.common.exception.GlobalException;
import com.climb.redis.exception.ErrorCode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 * 分布式锁自定义实现
 * @Author lht
 * @Date  2020/9/10 12:19
 */
@Slf4j
@SuppressWarnings("all")
public class LockUtil {
    private static ThreadLocal<Map<String,RLock>> lockThread = ThreadLocal.withInitial(HashMap::new);
    private static final String lockPath = "redisson:lock:";
    private static final String defaultPrefixKey = "";


    private static  Redisson redisson;
    /*
     * 等待获取锁时间 秒
     */
    private static  long waitTime;
    /*
     * 保存锁时间 秒
     */
    private static  long leaseTime;





    /*
     * 批量加锁，redlock
     * @author: lht
     * @date: 2020/10/31 18:29
     * @param keys:
     * @return: 解锁的key
     **/
    public static String multiLock(Collection<String> keys){
        String unkey = IdUtil.simpleUUID();
        if(!tryMultiLock(defaultPrefixKey,unkey,keys)){
            throwException();
        }
        return unkey;
    }

    /*
     * 批量加锁,redlock
     * @author: lht
     * @date: 2020/10/31 18:30
     * @param prefixKey:
     * @param keys:
     * @return: 解锁的key
     **/
    public static String multiLock(String prefixKey,Collection<String> keys){
        String unkey = IdUtil.simpleUUID();
        if(!tryMultiLock(prefixKey,unkey,keys)){
            throwException();
        }
        return unkey;
    }


    /*
     * 批量加锁
     * @author: lht
     * @date: 2020/10/31 18:31
     * @param keys:
     * @return: void
     **/
    public static void lock(Collection<String> keys){
        if(!tryLock(defaultPrefixKey,keys)){
            throwException();
        }
    }

    /*
     * 批量加锁
     * @author: lht
     * @date: 2020/10/31 18:31
     * @param prefixKey:
     * @param keys:
     * @return: void
     **/
    public static void lock(String prefixKey,Collection<String> keys){
        if(!tryLock(prefixKey,keys)){
            throwException();
        }
    }

    /*
     * 加锁
     * @author: lht
     * @date: 2020/10/31 18:31
     * @param key:
     * @return: void
     **/
    public static void lock(String key){
        if(!tryLock(defaultPrefixKey,key)){
            throwException();
        }
    }

    /*
     * 加锁
     * @author: lht
     * @date: 2020/10/31 18:31
     * @param prefixKey:
     * @param key:
     * @return: void
     **/
    public static void lock(String prefixKey,String key){
        if(!tryLock(prefixKey,key)){
            throwException();
        }
    }
//===================

    /*
     * 尝试加锁
     * @author: lht
     * @date: 2020/10/31 18:32
     * @param prefixKey:
     * @param keys:
     * @return: boolean
     **/
    public static boolean tryLock(String prefixKey,Collection<String> keys){
        if(!CollectionUtils.isEmpty(keys)){
            boolean isError = false;
            for(String key:keys){
                if(!tryLock(prefixKey,key)){
                    isError = true;
                    break;
                }
            }
            //如果有错误，将本次key全部释放
            if(isError){
                //释放
                unlock(keys);
                return false;
            }
        }
        return true;
    }


    /*
     * 尝试加锁
     * @author: lht
     * @date: 2020/10/31 18:32
     * @param prefixKey:
     * @param unkey:
     * @param keys:
     * @return: boolean
     **/
    public static boolean tryMultiLock(String prefixKey,String unkey,Collection<String> keys){
        if(!CollectionUtils.isEmpty(keys)){
            List<RLock> list = new ArrayList<>();
            //先记录下来
            List<String> recordKeys = Lists.newArrayListWithCapacity(keys.size());
            for(String key:keys){
                String temp = prefixKey+ key;
                //没有加锁
                if(!LockContext.isLock(temp)){
                    //执行加锁
                    list.add(redisson.getLock(lockPath+temp));
                    //记录锁
                    recordKeys.add(temp);
                }
            }
            //如果全部已经加锁，直接返回true
            if(!list.isEmpty()){
                RLock[] rLocks = new RLock[list.size()];
                list.toArray(rLocks);
                RLock rLock = redisson.getRedLock(rLocks);
                //确定lock执行成功后，记录到context
                if(doTryLock(unkey,rLock)){
                    //批量记录lock
                    LockContext.recordLock(recordKeys);
                    //记录red lock
                    LockContext.recordLock(unkey);
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * 尝试加锁
     * @author: lht
     * @date: 2020/10/31 18:32
     * @param prefixKey:
     * @param key:
     * @return: boolean
     **/
    public static boolean tryLock(String prefixKey,String key){
        String temp = prefixKey+key;
        //已经加锁
        if(LockContext.isLock(temp)){
            return true;
        }
        RLock rLock = redisson.getLock(lockPath+temp);
        if(doTryLock(temp,rLock)){
            //记录lock
            LockContext.recordLock(temp);
            return true;
        }else{
            return false;
        }
    }

//======================= 加锁 end

    /*
     * 批量解锁
     * @author: lht
     * @date: 2020/10/31 18:32
     * @param keys:
     * @return: void
     **/
    public static boolean unlock(Collection<String> keys){
        return unlock(defaultPrefixKey,keys);
    }

    /*
     * 批量解锁
     * @author: lht
     * @date: 2020/10/31 18:33
     * @param prefixKey:
     * @param keys:
     * @return: void
     **/
    public static boolean unlock(String prefixKey,Collection<String> keys){
        boolean boo = true;
        for (String key : keys) {
            if(!unlock(prefixKey,key)){
                boo = false;
            }
        }
        return boo;
    }


    /*
     * 单个解锁
     * @author: lht
     * @date: 2020/10/31 18:33
     * @param key:
     * @return: void
     **/
    public static boolean unlock(String key){
        return unlock(defaultPrefixKey,key);
    }

    /*
     * 单个解锁
     * @author: lht
     * @date: 2020/10/31 18:33
     * @param prefixKey:
     * @param key:
     * @return: void
     **/
    public static boolean unlock(String prefixKey,String key){
        Map<String,RLock> lockMap = lockThread.get();
        String temp = prefixKey+key;
        RLock lock = lockMap.get(temp);
        if(lock != null){
            try{
                lock.unlock();
            }catch (Exception e){
                return false;
            }finally {
                lockMap.remove(temp);
                LockContext.remove(temp);
            }
        }
        return true;
    }



    /*
     * 全部解锁
     * @author: lht
     * @date: 2020/10/31 18:34
     *
     * @return: void
     **/
    public static boolean unlockAll(){
        Map<String,RLock> lockMap = lockThread.get();
        for (RLock lock : lockMap.values()) {
            try{
                lock.unlock();
            }catch (Exception e){
                return false;
            }finally {
                lockThread.remove();
            }
        }
        LockContext.removeAll();
        return true;
    }

    /*
     * 尝试加锁
     * @author: lht
     * @date: 2020/10/31 18:34
     * @param key:
     * @param rLock:
     * @return: boolean
     **/
    private static boolean doTryLock(String key,RLock rLock){
        try{
            if(rLock.tryLock(3L,20L,TimeUnit.SECONDS)){
                Map<String,RLock> rLockMap =lockThread.get();
                rLockMap.put(key,rLock);
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /*
     * 获取锁失败异常
     * @author: lht
     * @date: 2020/10/31 18:34
     *
     * @return: void
     **/
    private static void throwException(){
        throw new GlobalException(ErrorCode.LOCK_NOT_FIND);
    }


    static void setWaitTime(long waitTime) {
        LockUtil.waitTime = waitTime;
    }

    static void setLeaseTime(long leaseTime) {
        LockUtil.leaseTime = leaseTime;
    }

    static void setRedisson(Redisson redisson) {
        LockUtil.redisson = redisson;
    }


}
