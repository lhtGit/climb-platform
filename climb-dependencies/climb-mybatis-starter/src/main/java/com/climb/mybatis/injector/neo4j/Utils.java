package com.climb.mybatis.injector.neo4j;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.*;
import static com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils.*;

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

}
