package com.climb.swagger.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lht
 * @since 2020/12/30 11:39
 */
@ConfigurationProperties(prefix = "swagger.doc")
@Data
public class SwaggerProperties {

    /**
     * name
     */
    @Value("${spring.application.name}")
    private String name;

    /**
     * title
     */
    private String title = name+"管理Api";

    /**
     * 描述
     */
    private String description = name +"管理";

    /**
     * 版本
     */
    private String version = "v1.0";

    /**
     * swagger扫描基础包
     */
    private String basePackage;

    /**
     * 联系
     */
    private Contact contact = new Contact();

    @Data
    public static class Contact{
        /**
         * 作者
         */
        private String name;
        /**
         * url
         */
        private String url;
        /**
         * 邮箱
         */
        private String email;
    }
}
