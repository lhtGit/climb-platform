package com.climb.feign;

import com.climb.common.constant.CommonConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 设置feign发送的header中的用户信息
 * @author lht
 * @since 2020/11/27 11:34
 */
@Slf4j
public class FeignBasicRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        setUserInfo(requestTemplate,request);
    }

    /**
     * 设置用户信息
     * @author lht
     * @since  2020/12/17 17:33
     * @param requestTemplate
     * @param request
     */
    private void setUserInfo(RequestTemplate requestTemplate,HttpServletRequest request){
        String loginUserStr = request.getHeader(CommonConstant.USER_INFO);
        if(loginUserStr!=null){
            requestTemplate.header(CommonConstant.USER_INFO, loginUserStr);
        }
    }

}
