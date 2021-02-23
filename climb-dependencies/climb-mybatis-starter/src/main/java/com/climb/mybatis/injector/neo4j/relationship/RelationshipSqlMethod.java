package com.climb.mybatis.injector.neo4j.relationship;

import lombok.Getter;

/**
 * neo4j 关系 sql 脚本
 * @author lht
 * @since 2021/2/14 14:29
 */
@Getter
public enum RelationshipSqlMethod {
    INSERT("insert","<script>\n match(a:%s {%s:#{%s}}) match(b:%s {%s:#{%s}}) create (a)-[r:%s %s]->(b)  \n</script>"),
    ;
    private final String methodName;
    private final String sql;

    RelationshipSqlMethod(String methodName, String sql) {
        this.methodName = methodName;
        this.sql = sql;
    }
}
