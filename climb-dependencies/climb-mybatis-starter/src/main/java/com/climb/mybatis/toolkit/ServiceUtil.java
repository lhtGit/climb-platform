package com.climb.mybatis.toolkit;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.climb.mybatis.injector.neo4j.relationship.RelationshipSqlMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * service util
 * @author lht
 * @since 2021/2/22 16:26
 */
public class ServiceUtil {
    private static Log log;

    private ServiceUtil() {
        log = LogFactory.getLog(getClass());
    }

    /**
     * 默认批次提交数量
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;


    /**
     * neo4j relationship 批量插入
     * @author lht
     * @since  2021/2/22 16:44
     * @param  clazz
     * @param  mapperClass
     * @param  entityList
     */
    public static  <T> boolean saveBatchRelationship(Class<T> clazz, Class<? extends Mapper<T>> mapperClass, Collection<T> entityList) {
        return saveBatch(clazz,mapperClass,entityList, RelationshipSqlMethod.INSERT.getMethodName(),DEFAULT_BATCH_SIZE);
    }
    /**
     * 批量插入
     * @author lht
     * @since  2021/2/22 16:43
     * @param  clazz
     * @param  mapperClass
     * @param  entityList
     * @param  method
     */
    public static <T> boolean saveBatch(Class<T> clazz, Class<? extends Mapper<T>> mapperClass, Collection<T> entityList, String method) {
        return saveBatch(clazz,mapperClass,entityList,method,DEFAULT_BATCH_SIZE);
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    public static <T> boolean saveBatch(Class<T> clazz, Class<? extends Mapper<T>> mapperClass, Collection<T> entityList, String method, int batchSize) {
        String sqlStatement = getSqlStatement(mapperClass, method);
        return executeBatch(clazz,entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
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
    private static <E> boolean executeBatch(Class<E> clazz,Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(clazz, log, list, batchSize, consumer);
    }

    private static String getSqlStatement(Class<?> mapper, String method){
        return mapper.getName() + StringPool.DOT + method;
    }
}
