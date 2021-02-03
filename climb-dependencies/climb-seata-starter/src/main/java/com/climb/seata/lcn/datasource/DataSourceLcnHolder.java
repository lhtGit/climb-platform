package com.climb.seata.lcn.datasource;

import com.climb.common.util.IdUtils;
import com.climb.lcn.datasource.LcnDataSource;
import io.seata.common.util.CollectionUtils;
import io.seata.core.model.BranchType;
import io.seata.rm.datasource.SeataDataSourceProxy;
import io.seata.spring.annotation.datasource.DataSourceProxyHolder;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lcn代理数据源持有者
 * copy自{@link DataSourceProxyHolder}，预期作用一致，都是用来持有代理数据源与源数据源之间关系的
 * @author lht
 * @since 2021/1/21 17:00
 */
public class DataSourceLcnHolder {
    private static final int MAP_INITIAL_CAPACITY = 8;
    private final ConcurrentHashMap<DataSource, DataSourceProxyLcn> dataSourceProxyMap;

    private DataSourceLcnHolder() {
        dataSourceProxyMap = new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
    }


    /**
     * the type holder
     */
    private static class Holder {
        private static final DataSourceLcnHolder INSTANCE;

        static {
            INSTANCE = new DataSourceLcnHolder();
        }
    }

    /**
     * Get DataSourceProxyHolder instance
     *
     * @return the INSTANCE of DataSourceProxyHolder
     */
    public static DataSourceLcnHolder get() {
        return DataSourceLcnHolder.Holder.INSTANCE;
    }



    public boolean isLcn(DataSource dataSource){
        return dataSource instanceof LcnDataSource ||dataSource instanceof DataSourceProxyLcn;
    }

    /**
     * Put dataSource
     *
     * @param dataSource          the data source
     * @param dataSourceProxyMode the data source proxy mode
     * @return dataSourceProxy
     */
    public SeataDataSourceProxy putDataSource(DataSource dataSource, BranchType dataSourceProxyMode) {
        DataSource originalDataSource;
        if (dataSource instanceof DataSourceProxyLcn) {
            DataSourceProxyLcn dataSourceProxy = (DataSourceProxyLcn) dataSource;

//            //If it's an right proxy, return it directly.
//            if (dataSourceProxyMode == dataSourceProxy.getBranchType()) {
//                return (SeataDataSourceProxy)dataSource;
//            }

            //Get the original data source.
            originalDataSource = dataSourceProxy.getTargetDataSource();
        } else {
            originalDataSource = dataSource;
        }
        return CollectionUtils.computeIfAbsent(this.dataSourceProxyMap,
                originalDataSource,
                dataSourceTemp -> new DataSourceProxyLcn(dataSourceTemp, IdUtils.nextId())
        );
    }
}
