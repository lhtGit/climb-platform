package com.climb.mybatis.injector.neo4j.relationship.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * relationship form annotation
 * @author lht
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormRelationship {
    /**
     * form table name
     */
    String value();

    /**
     * attribute name , default name id
     */
    String attribute() default "id";

}
