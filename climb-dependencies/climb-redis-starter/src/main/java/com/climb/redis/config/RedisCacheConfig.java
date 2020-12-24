package com.climb.redis.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.climb.redis.exception.IgnoreExceptionCacheErrorHandler;
import com.climb.redis.support.DefaultKeyGenerator;
import com.climb.redis.support.EmptyKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/*
 * 设置二级缓存
 * @Author lht
 * @Date  2020/9/10 10:51
 */
@Configuration
@EnableCaching
@RefreshScope
@ConfigurationProperties("spring.redis")
@CacheConfig(keyGenerator="keyGenerator",cacheManager = "redisCacheManager")
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisCacheConfig extends CachingConfigurerSupport {
    @Value("${mapper.cache.timeout:10}")
    private Long mybatisCacheTimeOut;
    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializationContext.SerializationPair serializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(getRedisSerializer());
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("mapper:cache:")
                .entryTtl(Duration.ofSeconds(mybatisCacheTimeOut))
                .serializeValuesWith(serializationPair);
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(redisCacheConfiguration).build();
    }


    @Override
    public CacheErrorHandler errorHandler() {
        return new IgnoreExceptionCacheErrorHandler();
    }

    @Override
    @Bean("keyGenerator")
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

    @Bean("emptyKeyGenerator")
    public EmptyKeyGenerator emptyKeyGenerator(){
        return new EmptyKeyGenerator();
    }

    private RedisSerializer<Object> getRedisSerializer(){
        return new GenericFastJsonRedisSerializer();
    }
}
