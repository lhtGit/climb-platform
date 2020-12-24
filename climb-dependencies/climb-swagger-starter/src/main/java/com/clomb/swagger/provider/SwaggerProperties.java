package com.clomb.swagger.provider;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;

/**
 * @author lht
 * @date 2020/9/18 13:37
 */
@SuppressWarnings("all")
@Configuration
@RefreshScope
@ConfigurationProperties("swagger")
@Data
public class SwaggerProperties {
    private List<SwaggerResource> resources;
}
