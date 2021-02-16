package com.climb.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.climb.mybatis.injector.expansion.methods.ShareSelectList;
import com.climb.mybatis.injector.neo4j.Neo4jMapper;
import com.climb.mybatis.injector.neo4j.methods.*;
import com.climb.mybatis.injector.neo4j.relationship.RelationshipMapper;
import com.climb.mybatis.injector.neo4j.relationship.methods.RelationshipInsert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * SQL 注入器
 * 增强{@link com.baomidou.mybatisplus.core.injector.DefaultSqlInjector}
 * @author lht
 * @since 2021/1/8 10:52
 */
public class EnhancejSqlInjector extends AbstractSqlInjector {

    private final Map<Class<? extends Mapper>,List<AbstractMethod>> CACHE_SQL_INJECTOR = new HashMap<>();

    public EnhancejSqlInjector() {
        init();
    }

    /**
     * 初始化 sql注入器 关系
     * @author lht
     * @since  2021/1/8 13:43
     */
    private void init(){
        //添加基于BaseMapper的注入器
        CACHE_SQL_INJECTOR.put(BaseMapper.class,Stream.of(
                new Insert(),
                new Delete(),
                new DeleteByMap(),
                new DeleteById(),
                new DeleteBatchByIds(),
                new Update(),
                new UpdateById(),
                new SelectById(),
                new SelectBatchByIds(),
                new SelectByMap(),
                new SelectOne(),
                new SelectCount(),
                new SelectMaps(),
                new SelectMapsPage(),
                new SelectObjs(),
                new SelectList(),
                new SelectPage(),
                new ShareSelectList()
        ).collect(toList()));
        //添加基于Neo4jMapper的注入器
        CACHE_SQL_INJECTOR.put(Neo4jMapper.class,Stream.of(
                new Neo4jInsert(),
                new Neo4jUpdate(),
                new Neo4jUpdateById(),
                new Neo4jDelete(),
                new Neo4jDeleteById()
        ).collect(toList()));
        //关系 基于 RelationshipMapper的注入器
        CACHE_SQL_INJECTOR.put(RelationshipMapper.class,Stream.of(
                new RelationshipInsert()
        ).collect(toList()));
    }


    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        for (Class<? extends Mapper> calzz : CACHE_SQL_INJECTOR.keySet()) {
            if(calzz.isAssignableFrom(mapperClass)){
                return CACHE_SQL_INJECTOR.get(calzz);
            }
        }
        return new ArrayList<>();
    }
}
