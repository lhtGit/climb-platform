package com.climb.swagger.annotations;

import com.climb.swagger.provider.SwaggerProperties;
import com.climb.swagger.provider.SwaggerProvider;
import com.climb.swagger.provider.SwaggerResourceController;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 集成各个模块的swagger文档,一般用于gateway使用
 * @author lht
 * @date 2020/9/18 16:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SwaggerProperties.class, SwaggerProvider.class, SwaggerResourceController.class})
public @interface EnableSwaggerProvider {
}
