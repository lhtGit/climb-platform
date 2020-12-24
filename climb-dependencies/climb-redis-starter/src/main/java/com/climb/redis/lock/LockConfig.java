package com.climb.redis.lock;

import lombok.Getter;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author lht
 * @since 2020/12/23 16:29
 */
public class LockConfig {
    @Resource
    private Redisson redisson;


    /**
     * 等待获取锁时间 秒
     * @author lht
     * @since  2020/12/23 16:38
     */
    @Getter
    @Value("${customize.lock.wait.time:3}")
    private long waitTime;

    /**
     * 保存锁时间 秒
     * @author lht
     * @since  2020/12/23 16:38
     */
    @Value("${customize.lock.lease.time:20}")
    private long leaseTime;

    /**
     * 初始化分布式锁
     * @author lht
     * @since  2020/12/23 16:38
     * @param
     */
    @PostConstruct
    public void initLockUtil(){
        LockUtil.setLeaseTime(leaseTime);
        LockUtil.setRedisson(redisson);
        LockUtil.setWaitTime(waitTime);
    }
}
