package com.climb.redis.lock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lht
 * @since 2020/12/30 13:37
 */
@Data
@ConfigurationProperties("redis.lock")
public class LockProperties {

    /**
     * 等待获取锁时间 秒
     */
    private Long waitTime = 3L;

    /**
     * 保存锁时间 秒
     */
    private Long leaseTime = 30L;
}
