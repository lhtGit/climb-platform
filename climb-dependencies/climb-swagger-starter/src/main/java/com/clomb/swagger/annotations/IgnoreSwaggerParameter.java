package com.clomb.swagger.annotations;


import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * swagger忽略的参数(只对get,delete请求参数有效)
 * swagger自带的hidden属性对于List<对象>无法隐藏，必须使用 {@link IgnoreSwaggerParameter} 才可以隐藏
 * 其他请求或者基本类型的属性隐藏要使用{@link ApiModelProperty} 的属性hidden=true,即可隐藏
 * @author lht
 * @since  2020/10/10 12:35
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreSwaggerParameter {
}
