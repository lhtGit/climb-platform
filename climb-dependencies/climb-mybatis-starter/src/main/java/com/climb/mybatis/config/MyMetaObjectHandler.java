package com.climb.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.climb.common.user.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 要根据具体情况设置 ， 目前暂不启用
 * 插入时，自动添加 created/updated；更新时，自动添加 updated
 * @author lht
 * @since  2020/12/23 17:25
 */
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

	@Resource
	protected HttpServletRequest request;

	@Override
	public void insertFill(MetaObject metaObject) {
		log.info("start insert fill ....");
		this.strictInsertFill(metaObject, "created", String.class, UserUtils.getUserDetails(request).getId());
		this.strictInsertFill(metaObject, "createdName", String.class, UserUtils.getUserDetails(request).getName());
		this.strictInsertFill(metaObject, "updated", String.class, UserUtils.getUserDetails(request).getId());
		this.strictInsertFill(metaObject, "updatedName", String.class, UserUtils.getUserDetails(request).getName());
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		log.info("start update fill ....");
		this.strictUpdateFill(metaObject, "updated", String.class, UserUtils.getUserDetails(request).getId());
		this.strictUpdateFill(metaObject, "updatedName", String.class, UserUtils.getUserDetails(request).getName());
	}
}
