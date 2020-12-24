package com.climb.redis.support;


import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;

/*
 *
 * @Author lht
 * @Date  2020/9/10 11:17
 */
public class DefaultKeyGenerator implements KeyGenerator {
    SimpleKeyGenerator simpleKeyGenerator = new SimpleKeyGenerator();

    @Override
    public Object generate(Object target, Method method, Object... params) {

        return  simpleKeyGenerator.generate(target,method,params);
    }
}
