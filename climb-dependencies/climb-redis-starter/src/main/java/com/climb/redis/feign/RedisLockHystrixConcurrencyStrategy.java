package com.climb.redis.feign;

import com.climb.redis.lock.LockContext;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 只处理分布式锁传播
 * Hystrix只允许有一个并发策略，
 * 但是它允许链式调用，所以只写自己的逻辑就可以了，
 * 只需要注意wrapCallable方法的写法，要判断是否为链式调用
 * @author lht
 * @since  2020/11/27 12:14
 */
public class RedisLockHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {


    private HystrixConcurrencyStrategy delegate;

    public RedisLockHystrixConcurrencyStrategy() {
        this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
        HystrixPlugins.reset();
        HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
    }

    @Override
    public <K> Callable<K> wrapCallable(Callable<K> c) {
        if (c instanceof RedisLockWrapContextCallable) {
            return c;
        }

        Callable<K> wrappedCallable;
        if (this.delegate != null) {
            wrappedCallable = this.delegate.wrapCallable(c);
        }
        else {
            wrappedCallable = c;
        }
        if (wrappedCallable instanceof RedisLockWrapContextCallable) {
            return wrappedCallable;
        }
        return new RedisLockWrapContextCallable<>(wrappedCallable,LockContext.getRecordedKeys());
    }


    private static class RedisLockWrapContextCallable<K> implements Callable<K> {

        private final Callable<K> actual;
        private final Set<String> lockKeys;

        RedisLockWrapContextCallable(Callable<K> actual,Set<String> lockKeys) {
            this.actual = actual;
            this.lockKeys = lockKeys;
        }

        @Override
        public K call() throws Exception {
            try {
                LockContext.recordLock(lockKeys);
                return actual.call();
            }
            finally {
                LockContext.removeAll();
            }
        }

    }
}
