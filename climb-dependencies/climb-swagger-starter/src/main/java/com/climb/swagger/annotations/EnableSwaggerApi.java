package com.climb.swagger.annotations;

import com.climb.swagger.config.SwaggerAutoConfigurer;
import com.climb.swagger.config.SwaggerProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启swagger
 * @author lht
 * @since  2020/12/23 17:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SwaggerAutoConfigurer.class, SwaggerProperties.class})
public @interface EnableSwaggerApi {
}
