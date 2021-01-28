package com.climb.seata.config;

import com.clomb.feign.hystrix.CheckThrowExecption;
import io.seata.core.context.RootContext;

/**
 * 校验熔断中是否抛出异常
 * @author lht
 * @since 2021/1/27 18:32
 */
public class SeataCheckThrowExecption implements CheckThrowExecption {
    @Override
    public boolean isThrowException(Throwable throwable) {
        return RootContext.getXID()!=null;
    }
}
