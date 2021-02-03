package com.climb.neo4j.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.climb.mybatis.page.interceptor.ExtensionPaginationInnerInterceptor;
import com.climb.neo4j.constant.Neo4jConstant;
import com.climb.neo4j.properties.Neo4jDataSourceProperties;
import com.climb.lcn.datasource.LcnDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
@ConditionalOnProperty(prefix = "neo4j",name = "config.enable",havingValue = "true" ,matchIfMissing = true)
public class Neo4jMybatisMappingConfig {

    private  final String DATABASE = "database";
    /**
     * mapper xml localtion
     */
    @Value("${neo4j.mybatis.localtionPattern}")
    private String localtionPattern;


    @Bean(Neo4jConstant.NEO4J_DATASOURCE_NAME)
    public DataSource getDataSource(Neo4jDataSourceProperties dataSourceProperties) throws Exception{
        LcnDataSource dataSource = new LcnDataSource();
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        //druid configuration
        dataSource.setInitialSize(dataSourceProperties.getInitialSize());
        dataSource.setMinIdle(dataSourceProperties.getMinIdle());
        dataSource.setMaxActive(dataSourceProperties.getMaxActive());
        dataSource.setMaxWait(dataSourceProperties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(dataSourceProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(dataSourceProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(dataSourceProperties.getValidationQuery());
        dataSource.setTestWhileIdle(dataSourceProperties.getTestWhileIdle());
        dataSource.setTestOnBorrow(dataSourceProperties.getTestOnBorrow());
        dataSource.setTestOnReturn(dataSourceProperties.getTestOnReturn());
        dataSource.setPoolPreparedStatements(dataSourceProperties.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setUseGlobalDataSourceStat(dataSourceProperties.getUseGlobalDataSourceStat());
        dataSource.setFilters(dataSourceProperties.getFilters());
        Properties properties  = dataSourceProperties.getConnectProperties();
        properties.setProperty(DATABASE,dataSourceProperties.getDatabase());
        dataSource.setConnectProperties(properties);
        return dataSource;
    }


    @Bean("neo4SqlSessionFactory")
    public SqlSessionFactory neo4SqlSessionFactory(@Qualifier(Neo4jConstant.NEO4J_DATASOURCE_NAME) DataSource dataSource, MybatisPlusProperties properties) throws Exception {
        MybatisSqlSessionFactoryBean fb = new MybatisSqlSessionFactoryBean();
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setUseDeprecatedExecutor(false);
        fb.setConfiguration(mybatisConfiguration);
        fb.setDataSource(dataSource);
        //配饰插件 分页、
        fb.setPlugins(mybatisPlusInterceptor());
        Resource[] resources=new PathMatchingResourcePatternResolver().getResources(localtionPattern);
        fb.setMapperLocations(resources);
        fb.setGlobalConfig(properties.getGlobalConfig());
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



}
