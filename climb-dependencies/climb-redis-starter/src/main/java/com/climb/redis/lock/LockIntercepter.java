package com.climb.redis.lock;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import com.climb.common.constant.CommonConstant;
import com.climb.common.exception.GlobalException;
import com.climb.redis.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Set;

/**
 * 处理分布式锁传递及清除
 * @author lht
 * @since 2020/11/2 09:24
 */
@Slf4j
public class LockIntercepter extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String keysJson = request.getHeader(LockContext.KEY_LOCK);
        if(keysJson!=null){
            try{
                Set<String> keys = JSON.parseObject(URLDecoder.decode(keysJson, CommonConstant.UTF8), new TypeReference<Set<String>>(){});
                LockContext.recordLock(keys);
            }catch (Exception e){
                log.error("解析分布式锁Context异常 ",e);
                throw new GlobalException(ErrorCode.LOCK_PARSING_CONTEXT,e);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LockUtil.unlockAll();
    }
}
