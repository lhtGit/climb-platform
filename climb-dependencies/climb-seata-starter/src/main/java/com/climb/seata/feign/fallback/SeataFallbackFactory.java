package com.climb.seata.feign.fallback;

import com.climb.common.exception.GlobalException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import feign.hystrix.FallbackFactory;
import io.seata.core.context.RootContext;

/**
 * feign启用熔断后会将所有异常全部吃掉，继承这个类后默认如果是seata事务会抛出异常
 * @author lht
 * @since 2020/12/28 12:17
 */
public abstract class SeataFallbackFactory<T> implements FallbackFactory<T> {
    @Override
    public T create(Throwable throwable) {
        return process(throwable);
    }

    private T process(Throwable throwable){
        if(isThrowException()){
            throw new GlobalException(throwable.getMessage());
        }
        //超时设置消息文本
        if(throwable instanceof HystrixTimeoutException){
            throwable = new GlobalException("访问超时报错",throwable);
        }
        return createObject(throwable);
    }

    /**
     * 与{@link FallbackFactory}的create方法一直
     * @author lht
     * @since  2020/12/28 12:26
     * @param throwable
     */
    protected abstract T createObject(Throwable throwable);

    /**
     * 是否抛出异常
     * @author lht
     * @since  2020/12/28 12:25
     * @param
     */
    protected boolean isThrowException(){
        return isGlobalTransactional();
    }

    /**
     * 是否为 seata分布式事务
     * @author lht
     * @since  2020/12/28 12:25
     * @param
     */
    protected boolean isGlobalTransactional(){
        return RootContext.getXID()!=null;
    }
}
