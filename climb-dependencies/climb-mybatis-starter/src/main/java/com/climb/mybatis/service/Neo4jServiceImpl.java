package com.climb.mybatis.service;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.climb.mybatis.injector.neo4j.Neo4jMapper;
import com.climb.mybatis.injector.neo4j.Neo4jSqlMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * @author lht
 * @since 2021/1/21 18:05
 */
public class Neo4jServiceImpl<M extends Neo4jMapper<T>, T> {
    /**
     * 默认批次提交数量
     */
    private final int DEFAULT_BATCH_SIZE = 1000;

    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    public M getBaseMapper() {
        return baseMapper;
    }

    protected Class<?> entityClass = currentModelClass();

    protected Class<?> mapperClass = currentMapperClass();

    protected Class<T> currentMapperClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }


    /**
     * 批量插入
     *
     * @param entityList ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList){
        return saveBatch(entityList,DEFAULT_BATCH_SIZE);
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(mapperClass, Neo4jSqlMethod.INSERT);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }


    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
    }


    private String getSqlStatement(Class<?> mapper, Neo4jSqlMethod sqlMethod){
        return mapper.getName() + StringPool.DOT + sqlMethod.getMethodName();
    }
}
