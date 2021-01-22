package com.climb.seata.lcn.transaction;

import com.climb.seata.lcn.datasource.connection.ConnectionProxyLcn;
import com.climb.seata.lcn.exception.SeataException;
import io.seata.common.util.CollectionUtils;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lht
 * @since 2021/1/22 11:22
 */
@Slf4j
public class ContextHolder {
    private static final int MAP_INITIAL_CAPACITY = 8;
    //<xid,<dataSourceId,ConnectionProxyLcn>>
    private ConcurrentHashMap<String, ConcurrentHashMap<String,ConnectionProxyLcn>> contextMap;

    private ContextHolder() {
        contextMap = new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
    }


    /**
     * the type holder
     */
    private static class Holder {
        private static final ContextHolder INSTANCE;

        static {
            INSTANCE = new ContextHolder();
        }
    }

    /**
     * Get ContextHolder instance
     *
     * @return the INSTANCE of DataSourceProxyHolder
     */
    public static ContextHolder get() {
        return ContextHolder.Holder.INSTANCE;
    }


    public ConnectionProxyLcn put(String dataSourceId,ConnectionProxyLcn connectionProxyLcn) {
        String xid = RootContext.getXID();
        ConcurrentHashMap<String,ConnectionProxyLcn> connectionMap =
                CollectionUtils.computeIfAbsent(contextMap,xid,xidTemp ->  new ConcurrentHashMap<>());
        return CollectionUtils.computeIfAbsent(connectionMap,dataSourceId,dataSourceIdTemp ->  connectionProxyLcn);
    }

    /**
     * 通知事务提交/回滚
     * @author lht
     * @since  2021/1/22 12:15
     * @param xid
     * @param isCommit
     */
    public void notify(String xid,boolean isCommit)  {
        ConcurrentHashMap<String,ConnectionProxyLcn> connectionMap = contextMap.get(xid);
        if(connectionMap==null){
            return ;
        }
        for (ConnectionProxyLcn connectionProxyLcn : connectionMap.values()) {
            connectionProxyLcn.notify(isCommit);
        }
        //删除
        contextMap.remove(xid);
    }
}
