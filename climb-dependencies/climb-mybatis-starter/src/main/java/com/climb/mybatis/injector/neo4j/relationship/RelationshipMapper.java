package com.climb.mybatis.injector.neo4j.relationship;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.climb.mybatis.injector.neo4j.relationship.bean.BaseRelationship;
import org.apache.ibatis.annotations.Param;

/**
 * neo4j 关系处理mapper
 * @author lht
 * @since 2021/2/14 14:28
 */
public interface RelationshipMapper<T extends BaseRelationship> extends Mapper<T> {
    int insert(@Param("param") T t,@Param("formTable")String formTable,@Param("toTable")String toTable);
}
