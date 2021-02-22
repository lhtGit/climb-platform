package com.climb.mybatis.injector.neo4j;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.climb.mybatis.injector.neo4j.relationship.annotation.FormRelationship;
import com.climb.mybatis.injector.neo4j.relationship.annotation.ToRelationship;
import com.climb.mybatis.injector.neo4j.relationship.bean.RelationshipInfo;
import com.climb.mybatis.injector.neo4j.relationship.bean.RelTableInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.*;
import static com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils.convertIf;
import static com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils.safeParam;

/**
 * @author lht
 * @since 2021/1/11 09:37
 */
public class Utils {

   /**
    * 生成主键sql key:value
    * @author lht
    * @since  2021/1/11 9:57
    * @param tableInfo
    */
    public static String generateKeySqlOfColon(TableInfo tableInfo){
        return generateKeySqlOfColon(tableInfo,EMPTY);
    }

    /**
     * 生成主键sql key:value
     * @author lht
     * @since  2021/1/11 9:57
     * @param tableInfo
     * @param paramAlias
     */
    public static String generateKeySqlOfColon(TableInfo tableInfo, String paramAlias){
        if(tableInfo.havePK()){
            if(StringUtils.isBlank(paramAlias)){
                paramAlias = EMPTY;
            }else{
                paramAlias = paramAlias+DOT;
            }

            String attributes = tableInfo.getKeyColumn();
            String keyProperty = tableInfo.getKeyProperty();
            return convertIf(attributes + COLON + safeParam(paramAlias+keyProperty)+COMMA
                    , paramAlias+keyProperty + "!=null "
                    , true);

        }
        return EMPTY;
    }

    /**
     * 解析关联table信息
     * @author lht
     * @since  2021/2/22 9:44
     * @param  clazz
     */
    public static RelationshipInfo parseRelationshipTableInfo(Class<?> clazz){
        RelationshipInfo info = new RelationshipInfo();
        Set<String> ralationshipFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            FormRelationship formRelationship = field.getAnnotation(FormRelationship.class);
            ToRelationship toRelationship = field.getAnnotation(ToRelationship.class);
            if(formRelationship != null){
                info.setFormTableInfo(new RelTableInfo(formRelationship.value(),formRelationship.attribute(),field.getName()));
                ralationshipFields.add(field.getName());
            }
            if(toRelationship != null){
                info.setToTableInfo(new RelTableInfo(toRelationship.value(),toRelationship.attribute(),field.getName()));
                ralationshipFields.add(field.getName());
            }
            //全部满足则退出循环
            if(info.isSuccess()){
                break;
            }
        }
        info.setRelationshipFields(ralationshipFields);
        return info;
    }

}
