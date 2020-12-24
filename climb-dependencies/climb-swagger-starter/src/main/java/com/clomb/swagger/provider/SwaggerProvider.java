package com.clomb.swagger.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;

/**
 * @author lht
 * @date 2020/9/17 14:05
 */
@Component
public class SwaggerProvider implements SwaggerResourcesProvider {

    @Autowired
    public SwaggerProperties swaggerProperties;


    @Override
    public List<SwaggerResource> get() {
        return swaggerProperties.getResources();
    }



}