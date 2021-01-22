package com.climb.seata.lcn.datasource;

import com.climb.seata.lcn.datasource.connection.ConnectionProxyLcn;
import com.climb.seata.lcn.exception.SeataException;
import com.climb.seata.lcn.transaction.ContextHolder;
import io.seata.core.model.BranchType;
import io.seata.rm.datasource.AbstractDataSourceProxy;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author lht
 * @since 2021/1/21 16:40
 */
@Slf4j
public class DataSourceProxyLcn extends AbstractDataSourceProxy {


    private String id;

    private DataSource targetDataSource;


    public DataSourceProxyLcn(DataSource targetDataSource, String id) {
        super(targetDataSource);
        this.id = id;
        this.targetDataSource = targetDataSource;
    }

    public String getId() {
        return id;
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.AT;
    }

    @Override
    public Connection getConnection() throws SQLException {
        ConnectionProxyLcn connectionProxyLcn = ContextHolder.get().put(id,new ConnectionProxyLcn(targetDataSource.getConnection()));
        connectionProxyLcn.setAutoCommit(false);
        return connectionProxyLcn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return ContextHolder.get().put(id,new ConnectionProxyLcn(targetDataSource.getConnection(username,password)));
    }
}
