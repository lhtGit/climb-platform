package com.climb.seata.lcn.datasource;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 使用lcn数据源，可以与seata分布式事务集成
 * 该数据源继承自{@link DruidDataSource}，只是lcn的一个标识
 * lcn目前必须使用AT模式才可以正常使用
 * @author lht
 * @since 2021/1/22 17:11
 */
public class LcnDataSource extends DruidDataSource {
}
