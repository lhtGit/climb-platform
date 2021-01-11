package com.climb.mybatis.page.interceptor;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.SPACE;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import java.util.List;

/**
 * 扩展分页拦截器
 * @author lht
 * @since 2021/1/4 18:00
 */
public class ExtensionPaginationInnerInterceptor extends PaginationInnerInterceptor {
    private final DbType dbType;

    public ExtensionPaginationInnerInterceptor(DbType dbType) {
        super(dbType);
        this.dbType = dbType;
    }

    @Override
    protected String autoCountSql(boolean optimizeCountSql, String sql) {
        //对neo4j数据库 做特殊处理
        if (dbType == DbType.NEO4J) {
            int sqlIndex = sql.toLowerCase().lastIndexOf("return");
            return sql.substring(0,sqlIndex)+" return count(*)";
        }
        return super.autoCountSql(optimizeCountSql, sql);
    }

    @Override
    protected String concatOrderBy(String originalSql, List<OrderItem> orderList) {
        //对neo4j数据库 做特殊处理
        if (dbType == DbType.NEO4J) {
            StringBuilder orderSql = new StringBuilder(" order by ");
            orderList.forEach(orderItem -> {
                orderSql.append(SPACE)
                        .append(orderItem.getColumn())
                        .append(SPACE)
                        .append(orderItem.isAsc()?"asc":"desc")
                        .append(SPACE);
            });

            return originalSql+orderSql.toString();
        }
        return super.concatOrderBy(originalSql, orderList);
    }


}
