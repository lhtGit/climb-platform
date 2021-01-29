package com.climb.neo4j.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.climb.mybatis.page.interceptor.ExtensionPaginationInnerInterceptor;
import com.climb.neo4j.constant.Neo4jConstant;
import com.climb.neo4j.properties.Neo4jDataSourceProperties;
import com.climb.seata.lcn.datasource.LcnDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * neo4j mybatis的配置
 * @author lht
 * @since 2021/1/5 13:52
 */
@Configuration
@MapperScan(basePackages  = {"${neo4j.mybatis.basePackages}"},sqlSessionTemplateRef  ="neo4jSqlSessionTemplate")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class Neo4jMybatisMappingConfig {

    private final String DATABASE = "database";

    /**
     * mapper xml localtion
     */
    @Value("${neo4j.mybatis.localtionPattern}")
    private String localtionPattern;

    @javax.annotation.Resource
    private Neo4jDataSourceProperties dataSourceProperties;

    @Bean(Neo4jConstant.NEO4J_DATASOURCE_NAME)
    public DataSource getDataSource() throws Exception{
        LcnDataSource datasource = new LcnDataSource();
        datasource.setUrl(dataSourceProperties.getUrl());
        datasource.setUsername(dataSourceProperties.getUsername());
        datasource.setPassword(dataSourceProperties.getPassword());
        datasource.setDriverClassName(dataSourceProperties.getDriverClassName());
        //druid configuration
        datasource.setInitialSize(dataSourceProperties.getInitialSize());
        datasource.setMinIdle(dataSourceProperties.getMinIdle());
        datasource.setMaxActive(dataSourceProperties.getMaxActive());
        datasource.setMaxWait(dataSourceProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(dataSourceProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(dataSourceProperties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(dataSourceProperties.getValidationQuery());
        datasource.setTestWhileIdle(dataSourceProperties.getTestWhileIdle());
        datasource.setTestOnBorrow(dataSourceProperties.getTestOnBorrow());
        datasource.setTestOnReturn(dataSourceProperties.getTestOnReturn());
        datasource.setPoolPreparedStatements(dataSourceProperties.getPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        datasource.setUseGlobalDataSourceStat(dataSourceProperties.getUseGlobalDataSourceStat());
        datasource.setFilters(dataSourceProperties.getFilters());
        Properties properties  = dataSourceProperties.getConnectProperties();
        properties.setProperty(DATABASE,dataSourceProperties.getDatabase());
        datasource.setConnectProperties(properties);
        return datasource;
    }


    @Bean
    public SqlSessionFactory neo4SqlSessionFactory(@Qualifier(Neo4jConstant.NEO4J_DATASOURCE_NAME) DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean fb = new MybatisSqlSessionFactoryBean();
        fb.setDataSource(dataSource);
        //配饰插件 分页、
        fb.setPlugins(mybatisPlusInterceptor());
        Resource[] resources=new PathMatchingResourcePatternResolver().getResources(localtionPattern);
        fb.setMapperLocations(resources);

        return fb.getObject();
    }

    @Bean
    public SqlSessionTemplate neo4jSqlSessionTemplate(@Qualifier("neo4SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    /**
     * 使用mybatis的分页插件 ，该分页插件已经扩展为可以用于neo4j
     */
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new ExtensionPaginationInnerInterceptor(DbType.NEO4J));
        return interceptor;
    }


    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

}
