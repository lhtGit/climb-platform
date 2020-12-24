package com.clomb.swagger.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author lht
 * @date  2020/9/17 10:29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "swagger.doc")
@RefreshScope
@EnableSwagger2
//@EnableSwaggerBootstrapUi
public class SwaggerAutoConfigurer {
    @Value("${spring.application.name}")
    private String name;

    private String title;
    private String description;
    private String author;
    private String version;
    private String basePackage;


    @Bean
    @RefreshScope
    public Docket campaignApi() {
        if(StringUtils.isEmpty(title)){
            title = name+"管理Api";
        }
        if(StringUtils.isEmpty(description)){
            description = name+"管理";
        }
        if(StringUtils.isEmpty(version)){
            version = "v1.0";
        }


        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(name)
                .apiInfo(new ApiInfoBuilder()
                        .title(title)
                        .description(description)
                        .contact(new Contact(author, "", ""))
                        .version(version)
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any()).build();
    }
}
