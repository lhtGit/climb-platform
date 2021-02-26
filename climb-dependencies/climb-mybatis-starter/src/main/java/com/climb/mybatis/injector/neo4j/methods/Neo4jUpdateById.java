package com.climb.mybatis.injector.neo4j.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.climb.mybatis.injector.neo4j.Neo4jSqlMethod;
import com.climb.mybatis.injector.neo4j.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.util.StringUtils;

/**
 * @author lht
 * @since 2021/1/8 16:58
 */
@Slf4j
public class Neo4jUpdateById extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        Neo4jSqlMethod neo4jSqlMethod = Neo4jSqlMethod.UPDATE_BY_ID;
        //拼接sql 脚本 where条件部分
        StringBuilder sqlWhereScriptBuffer = new StringBuilder();
        //设置id
        String keyId = tableInfo.getKeyColumn();
        if(!tableInfo.havePK()){
            log.warn(mapperClass+"因为没有指定id，所以不能使用"+neo4jSqlMethod.getMethodName()+"方法，如果要使用请使用@TableId指定");
            return null;
        }

        sqlWhereScriptBuffer.append(LEFT_BRACE)
                .append(keyId)
                .append(COLON)
                .append(SqlScriptUtils.safeParam(tableInfo.getKeyProperty()))
                .append(NEWLINE)
                .append(RIGHT_BRACE);

        //拼接sql 脚本 set value 部分
        StringBuilder sqlValueScriptBuffer = new StringBuilder();
        tableInfo.getFieldList().forEach(tableFieldInfo -> {
            String column = tableFieldInfo.getColumn();
            String attributes = tableFieldInfo.getProperty();
            sqlValueScriptBuffer.append(
                    SqlScriptUtils.convertIf("n."+column + EQUALS + SqlScriptUtils.safeParam(attributes)+COMMA
                            , attributes + "!=null "
                            , true)
            ).append(NEWLINE);
        });


        String sqlValueScript = SqlScriptUtils.convertTrim(sqlValueScriptBuffer.toString(), null, null, null, COMMA);
        //格式化sql
        String sql = String.format(neo4jSqlMethod.getSql(), tableInfo.getTableName(), sqlWhereScriptBuffer.toString(),sqlValueScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, neo4jSqlMethod.getMethodName(), sqlSource);
    }
}
