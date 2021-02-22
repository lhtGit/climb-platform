package com.climb.mybatis.injector.neo4j.relationship.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * relationship to annotation
 * @author lht
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToRelationship {
    /**
     * to table name
     */
    String value();

    /**
     * attribute name , default name id
     */
    String attribute() default "id";
}
