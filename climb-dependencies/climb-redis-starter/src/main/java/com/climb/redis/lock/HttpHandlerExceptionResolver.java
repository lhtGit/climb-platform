package com.climb.redis.lock;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理http异常时 分布式锁正常清除
 * @author lht
 * @since 2020/12/17 18:04
 */
public class HttpHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LockUtil.unlockAll();
        return null;
    }
}
