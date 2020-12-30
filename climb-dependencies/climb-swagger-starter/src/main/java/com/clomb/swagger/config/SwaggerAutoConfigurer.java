package com.clomb.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 *
 * @author lht
 * @date  2020/9/17 10:29
 */
@EnableSwagger2
public class SwaggerAutoConfigurer {

    @Resource
    private SwaggerProperties swaggerProperties;

    @Bean
    public Docket campaignApi() {
        if(StringUtils.isEmpty(swaggerProperties.getTitle())){
            swaggerProperties.setTitle(swaggerProperties.getName()+"管理Api");
        }
        if(StringUtils.isEmpty(swaggerProperties.getDescription())){
            swaggerProperties.setDescription(swaggerProperties.getName()+"管理");
        }
        if(StringUtils.isEmpty(swaggerProperties.getVersion())){
            swaggerProperties.setVersion("v1.0");
        }

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerProperties.getName())
                .apiInfo(new ApiInfoBuilder()
                        .title(swaggerProperties.getTitle())
                        .description(swaggerProperties.getDescription())
                        .contact(new Contact(
                                swaggerProperties.getContact().getName(),
                                swaggerProperties.getContact().getUrl(),
                                swaggerProperties.getContact().getEmail()
                        ))
                        .version(swaggerProperties.getVersion())
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any()).build();
    }
}
