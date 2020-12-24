package com.climb.redis.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author lht
 * @date 2020/9/22 16:17
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisTemplateConfig {
    /**
     * RedisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        FastJsonRedisSerializer<Object> jacksonSeial = new FastJsonRedisSerializer<>(Object.class);
        template.setValueSerializer(jacksonSeial);
        template.setKeySerializer(jacksonSeial);
        template.setHashKeySerializer(jacksonSeial);
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();
        return template;
    }


}
