package com.climb.mybatis.injector.neo4j.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.climb.mybatis.injector.neo4j.Neo4jSqlMethod;
import com.climb.mybatis.injector.neo4j.Utils;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author lht
 * @since 2021/1/8 17:35
 */
public class Neo4jDelete extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        Neo4jSqlMethod neo4jSqlMethod = Neo4jSqlMethod.DELETE;
        //拼接sql 脚本 where条件部分
        StringBuilder sqlWhereScriptBuffer = new StringBuilder(Utils.generateKeySqlOfColon(tableInfo));


        tableInfo.getFieldList().forEach(tableFieldInfo -> {
            String column = tableFieldInfo.getColumn();
            String attributes = tableFieldInfo.getProperty();
            sqlWhereScriptBuffer.append(
                    SqlScriptUtils.convertIf(column + COLON + SqlScriptUtils.safeParam(attributes)+COMMA
                            , attributes + "!=null "
                            , true)
            )
                    .append(NEWLINE);
        });

        String sqlWhereScript = SqlScriptUtils.convertTrim(sqlWhereScriptBuffer.toString(), LEFT_BRACE, RIGHT_BRACE, null, COMMA);
        //格式化sql
        String sql = String.format(neo4jSqlMethod.getSql(), tableInfo.getTableName(), sqlWhereScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addMappedStatement(mapperClass,
                neo4jSqlMethod.getMethodName(),
                sqlSource,
                SqlCommandType.DELETE,
                modelClass,
                null,
                Integer.class,
                new NoKeyGenerator(),
                null,
                null);
    }
}
