package com.climb.redis.feign;

import com.alibaba.fastjson.JSON;
import com.climb.common.constant.CommonConstant;
import com.climb.redis.lock.LockContext;
import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Set;

/**
 * 设置feign发送的header中的lockContext
 * @author lht
 * @since 2020/11/27 11:34
 */
@Slf4j
@ConditionalOnClass(Client.class)
public class FeignRedisLockInterceptor implements RequestInterceptor {

    @Autowired(required = false)
    private CleanLockRemnant cleanLockRemnant;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        setLockContext(requestTemplate);
    }


    /**
     * 设置 分布式锁 Context
     * @author lht
     * @since  2020/12/17 17:34
     * @param requestTemplate
     */
    private void setLockContext(RequestTemplate requestTemplate){
        Set<String> keys = LockContext.getRecordedKeys();
        try {
            if(keys.isEmpty()){
                return ;
            }
            String keysJson = JSON.toJSONString(keys);
            requestTemplate.header(LockContext.KEY_LOCK, URLEncoder.encode(keysJson, CommonConstant.UTF8));
        } catch (Exception e) {
            log.error("设置 分布式锁 Context:",e);
        }finally {
            //清除context
            if(cleanLockRemnant!=null){
                cleanLockRemnant.clean();
            }
        }
    }

}
