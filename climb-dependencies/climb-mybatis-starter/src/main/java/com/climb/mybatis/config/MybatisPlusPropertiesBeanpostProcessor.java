package com.climb.mybatis.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.climb.mybatis.injector.EnhancejSqlInjector;
import com.climb.mybatis.toolkit.CusIdentifierGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * 设置MybatisPlusProperties参数
 * @author lht
 * @since 2021/1/29 09:52
 */
public class MybatisPlusPropertiesBeanpostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof MybatisPlusProperties) {
            //设置GlobalConfig的id生成器和增强的SQL注入器
            GlobalConfig config = ((MybatisPlusProperties) bean).getGlobalConfig();
            config.setIdentifierGenerator(new CusIdentifierGenerator());
            config.setSqlInjector(new EnhancejSqlInjector());
        }
        return bean;
    }
}
