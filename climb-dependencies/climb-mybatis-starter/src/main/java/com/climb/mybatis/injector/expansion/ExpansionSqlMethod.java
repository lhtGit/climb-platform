package com.climb.mybatis.injector.expansion;

import lombok.Getter;

/**
 * 扩展baseMapper
 * @author lht
 * @since 2021/1/28 18:28
 */
@Getter
public enum  ExpansionSqlMethod {
    SHARE_SELECT_LIST("shareSelectList","查询满足条件所有数据 加共享锁","<script>%s SELECT %s FROM %s %s %s  lock in share mode\n</script>")
    ;
    private final String method;
    private final String desc;
    private final String sql;

    ExpansionSqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }
}
