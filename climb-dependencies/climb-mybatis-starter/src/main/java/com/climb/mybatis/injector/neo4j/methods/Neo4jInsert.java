package com.climb.mybatis.injector.neo4j.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.climb.mybatis.injector.neo4j.Neo4jSqlMethod;
import com.climb.mybatis.injector.neo4j.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

/**
 * neo4j insert method
 * 只针对节点
 * @author lht
 * @since 2021/1/8 10:12
 */
@Slf4j
public class Neo4jInsert extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        Neo4jSqlMethod neo4jSqlMethod = Neo4jSqlMethod.INSERT;


        KeyGenerator keyGenerator = new NoKeyGenerator();
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo.havePK()) {
            //如果IdType为AUTO ，则警告不会做任何操作 等价于没有设置主键
            if (tableInfo.getIdType() == IdType.AUTO) {
                log.warn(modelClass.getName() + "的主键生成类型不能为AUTO，neo4j不支持该生成规则！");
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(neo4jSqlMethod.getMethodName(), tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        //拼接sql 脚本 参数部分
        StringBuilder sqlParamsScriptBuffer = new StringBuilder(Utils.generateKeySqlOfColon(tableInfo));
        tableInfo.getFieldList().forEach(tableFieldInfo -> {
            String column = tableFieldInfo.getColumn();
            String attributes = tableFieldInfo.getProperty();
            sqlParamsScriptBuffer.append(
                    SqlScriptUtils.convertIf(column + COLON + SqlScriptUtils.safeParam(attributes)+COMMA
                            , attributes + "!=null "
                            , true)
            )
                    .append(NEWLINE);
        });
        String sqlParamsScript = SqlScriptUtils.convertTrim(sqlParamsScriptBuffer.toString(), LEFT_BRACE, RIGHT_BRACE, null, COMMA);
        //格式化sql
        String sql = String.format(neo4jSqlMethod.getSql(), tableInfo.getTableName(), sqlParamsScript);

        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, neo4jSqlMethod.getMethodName(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
