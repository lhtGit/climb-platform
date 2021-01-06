package com.climb.neo4j.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * neo4j数据库 配置文件
 * @author lht
 * @since 2021/1/5 15:36
 */
@Data
@ConfigurationProperties("neo4j.datasource")
@Configuration
public class Neo4jDataSourceProperties {
    /**
     * neo4j url jdbc:neo4j:bolt://ip:port
     */
    private String url;

    /**
     * neo4j username
     */
    private String username;

    /**
     * neo4j passoword
     */
    private String password;

    /**
     * neo4j driver
     */
    private String driverClassName = "org.neo4j.jdbc.Driver";

    /**
     * neo4j database
     */
    private String database;

    /**
     * 初始化大小，最小，最大
     */
    private Integer initialSize = 5;
    private Integer minIdle = 5;
    private Integer maxActive = 20;

    /**
     * 配置获取连接等待超时的时间
     */
    private Long maxWait = 60000L;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private Long timeBetweenEvictionRunsMillis = 60000L;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private Long minEvictableIdleTimeMillis = 300000L;

    /**
     * 校验SQL，Oracle配置 spring.datasource.validationQuery= SELECT 1 FROM DUAL，如果不配validationQuery项，则下面三项配置无用
     */
    private String validationQuery;
    private Boolean testWhileIdle = false;
    private Boolean testOnBorrow = false;
    private Boolean testOnReturn = false;

    /**
     * 打开PSCache，并且指定每个连接上PSCache的大小，一般用于存储过程 例如 oracle，会提升效率
     */
    private Boolean poolPreparedStatements = false;
    private Integer maxPoolPreparedStatementPerConnectionSize = 20;

    /**
     * 合并多个DruidDataSource的监控数据
     */
    private Boolean useGlobalDataSourceStat = true;

    /**
     * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
     */
    private String filters = "stat";

    /**
     * 其他配置
     */
    private Properties connectProperties = new Properties();
}
