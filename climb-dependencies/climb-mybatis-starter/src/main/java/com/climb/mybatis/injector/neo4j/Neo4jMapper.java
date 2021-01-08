package com.climb.mybatis.injector.neo4j;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.climb.mybatis.injector.neo4j.constant.StringConstant;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * neo4j  Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得基本功能
 * @author lht
 * @since 2021/1/8 14:11
 */
public interface Neo4jMapper<T> extends Mapper<T> {
    /**
     * 新增节点
     * @author lht
     * @since  2021/1/8 12:01
     * @param t
     */
    int insert(T t);

    /**
     * 修改节点
     * @author lht
     * @since  2021/1/8 17:19
     * @param param  where条件
     * @param val 设置的值
     */
    int update(@Param("param") T param,@Param("val") T val);

    /**
     * 修改 根据id
     * @author lht
     * @since  2021/1/8 18:31
     * @param id
     * @param val
     */
    int updateById(@Param(StringConstant.KEY_ID) Serializable id, @Param("val") T val);
    /**
     * 删除节点及节点相关联的边
     * @author lht
     * @since  2021/1/8 18:01
     * @param param
     */
    int delete(@Param("param") T param);

    /**
     * 删除 根据id
     * @author lht
     * @since  2021/1/8 18:31
     * @param id
     */
    int deleteById(@Param(StringConstant.KEY_ID) Serializable id);
}

