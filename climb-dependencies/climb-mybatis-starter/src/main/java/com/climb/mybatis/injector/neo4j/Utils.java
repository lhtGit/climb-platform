package com.climb.mybatis.injector.neo4j;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.climb.mybatis.injector.neo4j.relationship.bean.BaseRelationship;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.*;
import static com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils.*;

/**
 * @author lht
 * @since 2021/1/11 09:37
 */
public class Utils {
    private final static Set<String> BASE_RELATIONSHIP_SET =
            Stream.of(BaseRelationship.class.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());

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
    public static String generateKeySqlOfColon(TableInfo tableInfo,String paramAlias){
        if(tableInfo.havePK()){
            if(StringUtils.isBlank(paramAlias)){
                paramAlias = EMPTY;
            }else{
                paramAlias = paramAlias+DOT;
            }

            String attributes = tableInfo.getKeyColumn();
            return convertIf(attributes + COLON + safeParam(paramAlias+attributes)+COMMA
                    , paramAlias+attributes + "!=null "
                    , true);

        }
        return EMPTY;
    }

    /**
     * 判断是否为关系的 基础id 字段
     * @author lht
     * @since  2021/2/14 14:45
     * @param column
     */
    public static boolean isRelationshipPkId(String column){
        return BASE_RELATIONSHIP_SET.contains(column);
    }

}
