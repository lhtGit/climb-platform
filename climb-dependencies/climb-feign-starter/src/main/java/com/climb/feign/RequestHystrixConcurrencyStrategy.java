package com.climb.feign;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * 只处理request传播
 * Hystrix只允许有一个并发策略，
 * 但是它允许链式调用，所以只写自己的逻辑就可以了，
 * 只需要注意wrapCallable方法的写法，要判断是否为链式调用
 * @author lht
 * @since  2020/11/27 12:14
 */
public class RequestHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {


    private HystrixConcurrencyStrategy delegate;

    public RequestHystrixConcurrencyStrategy() {
        this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
        HystrixPlugins.reset();
        HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
    }

    @Override
    public <K> Callable<K> wrapCallable(Callable<K> c) {
        if (c instanceof WrapContextCallable) {
            return c;
        }

        Callable<K> wrappedCallable;
        if (this.delegate != null) {
            wrappedCallable = this.delegate.wrapCallable(c);
        }
        else {
            wrappedCallable = c;
        }
        if (wrappedCallable instanceof WrapContextCallable) {
            return wrappedCallable;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return new WrapContextCallable<>(wrappedCallable,requestAttributes);
    }


    private static class WrapContextCallable<K> implements Callable<K> {

        private final Callable<K> actual;
        private final RequestAttributes requestAttributes;

        WrapContextCallable(Callable<K> actual,RequestAttributes requestAttributes ) {
            this.actual = actual;
            this.requestAttributes = requestAttributes;
        }

        @Override
        public K call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return actual.call();
            }
            finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }

    }
}
