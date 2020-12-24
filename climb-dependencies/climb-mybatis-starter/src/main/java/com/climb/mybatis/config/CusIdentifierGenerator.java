package com.climb.mybatis.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.climb.common.util.IdUtils;

/**
 * 让mybatis自动生成id时使用我们在common设置的雪花算法id
 * @author lht
 * @since 2020/9/29 15:44
 */
public class CusIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return IdUtils.nextIdNumber();
    }

    @Override
    public String nextUUID(Object entity) {
        return IdUtils.nextId();
    }
}
