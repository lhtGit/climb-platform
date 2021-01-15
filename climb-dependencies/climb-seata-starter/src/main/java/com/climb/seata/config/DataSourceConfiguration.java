package com.climb.seata.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author lht
 * @date 2020/9/23 10:27
 */
@Configuration(proxyBeanMethods=false)
public class DataSourceConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceProxy dataSourceProxy() {
        return new DataSourceProxy(dataSource);
    }
}
