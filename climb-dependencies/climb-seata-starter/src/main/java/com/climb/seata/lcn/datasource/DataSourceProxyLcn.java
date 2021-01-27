package com.climb.seata.lcn.datasource;

import com.climb.seata.lcn.datasource.connection.ConnectionProxyLcn;
import com.climb.seata.lcn.exception.SeataException;
import com.climb.seata.lcn.transaction.ContextHolder;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import io.seata.core.model.Resource;
import io.seata.rm.DefaultResourceManager;
import io.seata.rm.datasource.AbstractDataSourceProxy;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * lcn数据源的代理
 * @author lht
 * @since 2021/1/21 16:40
 */
@Slf4j
public class DataSourceProxyLcn extends AbstractDataSourceProxy implements Resource {


    private final String resourceId;

    private final DataSource targetDataSource;

    private static final String DEFAULT_RESOURCE_GROUP_ID = "DEFAULT";

    private final String resourceGroupId;

    public DataSourceProxyLcn(DataSource targetDataSource, String resourceId) {
        super(targetDataSource);
        this.resourceId = resourceId;
        this.targetDataSource = targetDataSource;
        this.resourceGroupId = DEFAULT_RESOURCE_GROUP_ID;
        init();
    }

    private void init(){
        DefaultResourceManager.get().registerResource(this);
        //Set the default branch type to 'AT' in the RootContext.
        RootContext.setDefaultBranchType(this.getBranchType());
    }

    @Override
    public String getResourceGroupId() {
        return resourceGroupId;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.AT;
    }

    @Override
    public Connection getConnection() throws SQLException {
        ConnectionProxyLcn connectionProxyLcn = ContextHolder.get().put(resourceId,new ConnectionProxyLcn(targetDataSource.getConnection(),this));
        connectionProxyLcn.setAutoCommit(false);
        return connectionProxyLcn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        ConnectionProxyLcn connectionProxyLcn =
                ContextHolder.get().put(resourceId,new ConnectionProxyLcn(targetDataSource.getConnection(username,password),this));
        connectionProxyLcn.setAutoCommit(false);
        return connectionProxyLcn;
    }
}
