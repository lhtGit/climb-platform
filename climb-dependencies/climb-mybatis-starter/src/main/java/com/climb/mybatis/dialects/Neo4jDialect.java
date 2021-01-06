package com.climb.mybatis.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;

/**
 * 自定义新增 neo4j 方言
 * @author lht
 * @since 2021/1/4 16:15
 */
public class Neo4jDialect implements IDialect {
    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql);
        if (offset != 0L) {
            sql.append(" skip ").append(FIRST_MARK)
                    .append(" limit ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), offset, limit).setConsumerChain();
        } else {
            sql.append(" limit ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
