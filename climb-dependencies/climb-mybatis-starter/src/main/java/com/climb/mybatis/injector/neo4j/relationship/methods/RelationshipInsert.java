package com.climb.mybatis.injector.neo4j.relationship.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.climb.mybatis.injector.neo4j.Utils;
import com.climb.mybatis.injector.neo4j.relationship.RelationshipSqlMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * relationship insert method
 * 只针对节点
 * @author lht
 * @since 2021/1/8 10:12
 */
@Slf4j
public class RelationshipInsert extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        RelationshipSqlMethod relationshipSqlMethod = RelationshipSqlMethod.INSERT;


        KeyGenerator keyGenerator = new NoKeyGenerator();
        String keyProperty = null;
        String keyColumn = null;

        //拼接sql 脚本 参数部分
        StringBuilder sqlParamsScriptBuffer = new StringBuilder(Utils.generateKeySqlOfColon(tableInfo));
        tableInfo.getFieldList().stream()
                .filter(tableFieldInfo -> !Utils.isRelationshipPkId(tableFieldInfo.getColumn()))
                .forEach(tableFieldInfo -> {
                    String column = tableFieldInfo.getColumn();
                    String attributes = tableFieldInfo.getProperty();
                    sqlParamsScriptBuffer.append(
                            SqlScriptUtils.convertIf(column + COLON + SqlScriptUtils.safeParam("param."+attributes)+COMMA
                                    , "param."+attributes + "!=null "
                                    , true)
                    )
                    .append(NEWLINE);
        });


        String sqlParamsScript = SqlScriptUtils.convertTrim(sqlParamsScriptBuffer.toString(), LEFT_BRACE, RIGHT_BRACE, null, COMMA);
        //格式化sql
        String sql = String.format(relationshipSqlMethod.getSql(), tableInfo.getTableName(), sqlParamsScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, relationshipSqlMethod.getMethodName(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
