package com.climb.mybatis.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.climb.common.user.bean.UserInfoBase;
import com.climb.common.user.util.UserUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 扩展{@link ServiceImpl} 增加获取用户信息方法
 * @author lht
 * @since  2020/9/28 11:21
 */
public class ExtensionServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M , T>   {
    @Resource
    protected HttpServletRequest request;

    /**
     * 获取用户信息
     * @author lht
     * @since  2020/12/25 13:52
     * @param
     */
    public UserInfoBase getUserDetails() {
        return UserUtils.getUserDetails(request);
    }
}
