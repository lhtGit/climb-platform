package com.clomb.swagger.annotations;

import com.clomb.swagger.config.SwaggerAutoConfigurer;
import com.clomb.swagger.config.SwaggerProperties;
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
