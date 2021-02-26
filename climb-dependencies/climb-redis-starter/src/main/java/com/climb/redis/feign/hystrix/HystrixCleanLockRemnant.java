package com.climb.redis.feign.hystrix;

import com.climb.redis.feign.CleanLockRemnant;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 熔断清除lock残余
 * @author lht
 * @since 2021/2/26 11:50
 */
@ConditionalOnProperty(name="feign.hystrix.enabled",havingValue = "true")
@ConditionalOnClass({HystrixConcurrencyStrategy.class})
public class HystrixCleanLockRemnant implements CleanLockRemnant {
}
