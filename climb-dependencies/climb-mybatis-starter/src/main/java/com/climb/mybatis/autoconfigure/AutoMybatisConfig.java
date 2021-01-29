package com.climb.mybatis.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.climb.mybatis.config.MybatisPlusPropertiesBeanpostProcessor;
import com.climb.mybatis.handler.InjectionMetaObjectHandler;
import com.climb.mybatis.page.interceptor.ExtensionPaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis自动装配
 * @author lht
 * @since 2021/1/29 10:11
 */
@Configuration
public class AutoMybatisConfig {

    /**
     * 配置MybatisPlusProperties
     * @author lht
     * @since  2021/1/29 10:15
     */
    @Bean
    public MybatisPlusPropertiesBeanpostProcessor mybatisPlusPropertiesBeanpostProcessor(){
        return new MybatisPlusPropertiesBeanpostProcessor();
    }
    /**
     * 使用mybatis的分页插件
     * @author lht
     * @since  2021/1/29 10:28
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new ExtensionPaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
    /**
     * 避免缓存有问题，在mybatis-plus以后版本会去除，但是当前最新的也还存在
     * @author lht
     * @since  2021/1/29 10:28
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    /**
     * 配置自动注入sql
     * @author lht
     * @since  2021/1/29 10:23
     */
    @ConditionalOnProperty(prefix="mybatis",
            name = {"auto-injection","autoInjection"},
            havingValue = "true"
    )
    @Configuration
    static class InjectionMetaObjectHandlerConfig{
        @Bean
        public InjectionMetaObjectHandler injectionMetaObjectHandler(){
            return new InjectionMetaObjectHandler();
        }
    }

}
