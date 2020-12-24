package com.climb.redis.support;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;

/**
 * @author lht
 * @since 2020/9/24 10:04
 */
public class EmptyKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return SimpleKey.EMPTY;
    }
}
