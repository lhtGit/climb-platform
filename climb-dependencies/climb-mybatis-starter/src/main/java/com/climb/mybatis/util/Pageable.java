package com.climb.mybatis.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
 * 创建分页工具类
 * @Author lht
 * @Date  2020/9/14 12:09
 */
public class Pageable {
    private static final  int PAGE = 1;
    private static final  int SIZE = 10;

    public static <T> Page<T> empty(){
        return new Page<T>();
    }

    public static <T> Page<T> page(){
        return page(PAGE,SIZE);
    }

    public static <T> Page<T> page(long page,long size){
        Page<T> pageable = new Page<>();
        pageable.setCurrent(page);
        pageable.setSize(size);
        return pageable;
    }
    public static <T> Page<T> page(long page,long size,OrderItem... orderItems){
        Page<T> pageable = new Page<>();
        pageable.setCurrent(page);
        pageable.setSize(size);
        pageable.setOrders(Arrays.stream(orderItems).collect(Collectors.toList()));
        return pageable;
    }

    public static <T> Page<T> pageAsc(long page,long size,String... orders){
        Page<T> pageable = new Page<>();
        pageable.setCurrent(page);
        pageable.setSize(size);
        pageable.setOrders(OrderItem.ascs(orders));
        return pageable;
    }
    public static <T> Page<T> pageDesc(long page,long size,String... orders){
        Page<T> pageable = new Page<>();
        pageable.setCurrent(page);
        pageable.setSize(size);
        pageable.setOrders(OrderItem.descs(orders));
        return pageable;
    }
}
