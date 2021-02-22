package com.climb.mybatis.injector.neo4j.relationship.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lht
 * @since 2021/2/22 09:30
 */
@Data
@AllArgsConstructor
public class RelTableInfo {
    /**
     * 关联 table 名称
     */
    private String tableName;

    /**
     * 查询关联 table 的属性
     */
    private String attributeName;

    /**
     * 字段名称
     */
    private String property;
}
