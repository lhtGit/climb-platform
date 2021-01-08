package com.climb.mybatis.injector.neo4j;

import lombok.Getter;

/**
 * neo4j 自动注入sql 脚本
 * @author lht
 * @since 2021/1/8 13:56
 */
@Getter
public enum  Neo4jSqlMethod {
    /**
     * insert
     */
    INSERT("insert","<script>\n CREATE (n:%s  %s ) \n</script>"),
    /**
     * update
     */
    UPDATE("update","<script>\n MATCH(n:%s %s)  SET %s \n</script>"),
    /**
     * update by id
     */
    UPDATE_BY_ID("updateById","<script>\n MATCH(n:%s %s)  SET %s \n</script>"),
    /**
     * delete
     */
    DELETE("delete","<script>\n MATCH(n:%s %s)  DETACH DELETE n \n</script>"),
    /**
     * delete by id
     */
    DELETE_BY_ID("deleteById","<script>\n MATCH(n:%s %s)  DETACH DELETE n \n</script>"),
    ;



    private final String methodName;
    private final String sql;

    Neo4jSqlMethod(String methodName, String sql) {
        this.methodName = methodName;
        this.sql = sql;
    }
}

