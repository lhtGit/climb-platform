package com.climb.feign.hystrix;

/**
 * 校验熔断中是否抛出异常
 * @author lht
 * @since 2021/1/27 18:26
 */
public interface CheckThrowExecption {
    /**
     * 是否抛出异常
     * @author lht
     * @since  2021/1/27 18:27
     */
    default boolean isThrowException(Throwable throwable){
        return false;
    }
}
