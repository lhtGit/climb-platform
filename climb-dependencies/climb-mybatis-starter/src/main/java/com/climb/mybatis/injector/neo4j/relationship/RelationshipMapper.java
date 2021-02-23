package com.climb.mybatis.injector.neo4j.relationship;

import com.baomidou.mybatisplus.core.mapper.Mapper;

/**
 * neo4j 关系处理mapper
 * @author lht
 * @since 2021/2/14 14:28
 */
public interface RelationshipMapper<T> extends Mapper<T> {
    int insert(T t);
}
