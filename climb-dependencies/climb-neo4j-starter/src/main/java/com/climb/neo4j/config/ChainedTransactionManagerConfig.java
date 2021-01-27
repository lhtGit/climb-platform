package com.climb.neo4j.config;

import com.climb.neo4j.constant.Neo4jConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 开启本地事务
 * @author lht
 * @since 2021/1/27 09:22
 */
@Configuration
@ConditionalOnProperty( prefix = "neo4j",
        name = { "enable-open-local-transaction"},
        havingValue = "true")
public class ChainedTransactionManagerConfig {

    /**
     * 开启链式事务
     * @author lht
     * @since  2021/1/27 11:51
     * @param platformTransactionManagers
     */
    @Bean("chainedTransactionManager")
    @Primary
    public ChainedTransactionManager chainedTransactionManager(PlatformTransactionManager[] platformTransactionManagers){
        return new ChainedTransactionManager(platformTransactionManagers);
    }


    /**
     * 注册neo4j本地事务
     * @author lht
     * @since  2021/1/27 11:51
     * @param dataSource
     */
    @Bean
    public DataSourceTransactionManager neo4jTransactionManager(@Qualifier(Neo4jConstant.NEO4J_DATASOURCE_NAME) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
