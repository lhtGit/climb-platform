package com.climb.redis.config;

import com.climb.redis.lock.HttpHandlerExceptionResolver;
import com.climb.redis.lock.LockIntercepter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 设置分布式锁处理拦截器
 * @author lht
 * @since 2020/12/17 18:02
 */
public class RedisLockHttpAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LockIntercepter());
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new HttpHandlerExceptionResolver());
    }
}
