package com.climb.seata.config;

import com.climb.feign.hystrix.CheckThrowExecption;
import io.seata.core.context.RootContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * 校验熔断中是否抛出异常
 * @author lht
 * @since 2021/1/27 18:32
 */
@ConditionalOnClass(CheckThrowExecption.class)
public class SeataCheckThrowExecption implements CheckThrowExecption {
    @Override
    public boolean isThrowException(Throwable throwable) {
        return RootContext.getXID()!=null;
    }
}
