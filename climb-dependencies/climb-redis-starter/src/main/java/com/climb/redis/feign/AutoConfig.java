package com.climb.redis.feign;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import feign.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign 的redislock配置
 * @author lht
 * @since 2020/12/24 11:19
 */
@Configuration(proxyBeanMethods = false)
public class AutoConfig {

    @Bean
    @ConditionalOnClass(Client.class)
    public FeignRedisLockInterceptor feignRedisLockInterceptor(){
        return new FeignRedisLockInterceptor();
    }

    @Bean
    @ConditionalOnClass({HystrixConcurrencyStrategy.class})
    public RedisLockHystrixConcurrencyStrategy redisLockHystrixConcurrencyStrategy(){
        return new RedisLockHystrixConcurrencyStrategy();
    }
}
