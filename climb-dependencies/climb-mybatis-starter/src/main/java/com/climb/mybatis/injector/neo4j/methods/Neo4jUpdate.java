package com.climb.mybatis.injector.neo4j.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.climb.mybatis.injector.neo4j.Neo4jSqlMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author lht
 * @since 2021/1/8 16:58
 */
public class Neo4jUpdate extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        Neo4jSqlMethod neo4jSqlMethod = Neo4jSqlMethod.UPDATE;
        //拼接sql 脚本 where条件部分
        StringBuilder sqlWhereScriptBuffer = new StringBuilder();
        tableInfo.getFieldList().forEach(tableFieldInfo -> {
            String attributes = tableFieldInfo.getColumn();
            sqlWhereScriptBuffer.append(
                    SqlScriptUtils.convertIf(attributes + COLON + SqlScriptUtils.safeParam("param."+attributes)+COMMA
                            , "param."+attributes + "!=null "
                            , true)
            )
            .append(NEWLINE);
        });

        //拼接sql 脚本 set value 部分
        StringBuilder sqlValueScriptBuffer = new StringBuilder();
        tableInfo.getFieldList().forEach(tableFieldInfo -> {
            String attributes = tableFieldInfo.getColumn();
            sqlValueScriptBuffer.append(
                    SqlScriptUtils.convertIf("n."+attributes + EQUALS + SqlScriptUtils.safeParam("val."+attributes)+COMMA
                            , "val."+attributes + "!=null "
                            , true)
            )
            .append(NEWLINE);
        });



        String sqlWhereScript = SqlScriptUtils.convertTrim(sqlWhereScriptBuffer.toString(), LEFT_BRACE, RIGHT_BRACE, null, COMMA);
        String sqlValueScript = SqlScriptUtils.convertTrim(sqlValueScriptBuffer.toString(), null, null, null, COMMA);
        //格式化sql
        String sql = String.format(neo4jSqlMethod.getSql(), tableInfo.getTableName(), sqlWhereScript,sqlValueScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, neo4jSqlMethod.getMethodName(), sqlSource);
    }
}
