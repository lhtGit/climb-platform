package com.climb.feign.util;

import com.alibaba.fastjson.JSON;
import com.climb.common.constant.CommonConstant;
import com.climb.common.user.bean.UserInfoBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 设置用户后 会覆盖登录用户信息
 * @author lht
 * @since 2021/2/1 15:22
 */
@Slf4j
public class FeignUtils {
    private FeignUtils() {
    }


    /**
     * 设置用户到当前请求中，设置之后所有的feign请求的用户信息被覆盖
     * @author lht
     * @since  2021/2/1 15:56
     * @param user
     */
    public static void appointUser(UserInfoBase user) throws UnsupportedEncodingException {
        if(user==null){
            return ;
        }
        HttpServletRequest currentRequest = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userStr = JSON.toJSONString(user);
        currentRequest.setAttribute(CommonConstant.APPOINT_USER_INFO,URLEncoder.encode(userStr, CommonConstant.UTF8));
    }

}
