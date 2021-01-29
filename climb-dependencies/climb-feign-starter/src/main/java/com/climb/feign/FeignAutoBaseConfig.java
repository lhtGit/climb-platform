package com.climb.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * feign 基础配置
 * @author lht
 * @since 2020/11/27 12:03
 */
public class FeignAutoBaseConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignBasicRequestInterceptor();
    }

    @Bean
    public RequestHystrixConcurrencyStrategy hystrixRequestAutoConfiguration(){
        return new RequestHystrixConcurrencyStrategy();
    }
}
