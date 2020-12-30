package com.climb.redis.lock;

import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author lht
 * @since 2020/12/23 16:29
 */
@Configuration(proxyBeanMethods = false)
public class LockConfig {
    @Resource
    private Redisson redisson;

    @Autowired
    private LockProperties lockProperties;

    /**
     * 初始化分布式锁
     * @author lht
     * @since  2020/12/23 16:38
     * @param
     */
    @PostConstruct
    public void initLockUtil(){
        LockUtil.setLeaseTime(lockProperties.getLeaseTime());
        LockUtil.setRedisson(redisson);
        LockUtil.setWaitTime(lockProperties.getWaitTime());
    }
}
