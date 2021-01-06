//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.jdbc.bolt;

import org.neo4j.driver.Result;
import org.neo4j.driver.summary.SummaryCounters;
import org.neo4j.jdbc.Loggable;
import org.neo4j.jdbc.Neo4jParameterMetaData;
import org.neo4j.jdbc.Neo4jPreparedStatement;
import org.neo4j.jdbc.Neo4jResultSetMetaData;
import org.neo4j.jdbc.bolt.impl.BoltNeo4jConnectionImpl;
import org.neo4j.jdbc.utils.Neo4jInvocationHandler;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("all")
public class BoltNeo4jPreparedStatement extends Neo4jPreparedStatement implements Loggable {
    private BoltNeo4jPreparedStatement(BoltNeo4jConnectionImpl connection, String rawStatement, int... rsParams) {
        super(connection, rawStatement);
        this.resultSetParams = rsParams;
    }

    public static PreparedStatement newInstance(boolean debug, BoltNeo4jConnectionImpl connection, String rawStatement, int... rsParams) {
        PreparedStatement ps = new BoltNeo4jPreparedStatement(connection, rawStatement, rsParams);
        ((Neo4jPreparedStatement)ps).setDebug(debug);
        return (PreparedStatement)Proxy.newProxyInstance(BoltNeo4jPreparedStatement.class.getClassLoader(), new Class[]{PreparedStatement.class}, new Neo4jInvocationHandler(ps, debug));
    }

    public ResultSet executeQuery() throws SQLException {
        return (ResultSet)this.executeInternal((result) -> {
            this.currentResultSet = BoltNeo4jResultSet.newInstance(this.hasDebug(), this, result, this.resultSetParams);
            this.currentUpdateCount = -1;
            return this.currentResultSet;
        });
    }

    public int executeUpdate() throws SQLException {
        return (Integer)this.executeInternal((result) -> {
            SummaryCounters stats = result.consume().counters();
            this.currentUpdateCount = BoltNeo4jUtils.calculateUpdateCount(stats);
            this.currentResultSet = null;
            return this.currentUpdateCount;
        });
    }

    public boolean execute() throws SQLException {
        return (Boolean)this.executeInternal((result) -> {
            boolean hasResultSet = this.hasResultSet();
            if (hasResultSet) {
                this.currentResultSet = BoltNeo4jResultSet.newInstance(this.hasDebug(), this, result, this.resultSetParams);
                this.currentUpdateCount = -1;
            } else {
                this.currentResultSet = null;
                SummaryCounters stats = result.consume().counters();
                this.currentUpdateCount = BoltNeo4jUtils.calculateUpdateCount(stats);
            }

            return hasResultSet;
        });
    }

    private <T> T executeInternal(Function<Result, T> body) throws SQLException {
        this.checkClosed();
        return BoltNeo4jUtils.executeInTx((BoltNeo4jConnection)this.connection, this.statement, this.parameters, body);
    }

    private boolean hasResultSet() {
        return this.statement != null && this.statement.toLowerCase().contains("return");
    }

    public Neo4jParameterMetaData getParameterMetaData() throws SQLException {
        this.checkClosed();
        return new BoltNeo4jParameterMetaData(this);
    }

    public Neo4jResultSetMetaData getMetaData() throws SQLException {
        return this.currentResultSet == null ? null : (Neo4jResultSetMetaData)this.currentResultSet.getMetaData();
    }

    public int[] executeBatch() throws SQLException {
        this.checkClosed();
        int[] result = new int[0];

        try {
            BoltNeo4jConnection connection = (BoltNeo4jConnection)this.connection;

            int count;
            for(Iterator var3 = this.batchParameters.iterator(); var3.hasNext(); result[result.length - 1] = count) {
                Map<String, Object> parameter = (Map)var3.next();
                count = (Integer)BoltNeo4jUtils.executeInTx(connection, this.statement, parameter, (statementResult) -> {
                    SummaryCounters counters = statementResult.consume().counters();
                    return counters.nodesCreated() + counters.nodesDeleted();
                });
                result = Arrays.copyOf(result, result.length + 1);
            }

            return result;
        } catch (Exception var6) {
            throw new BatchUpdateException(result, var6);
        }
    }


    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.checkClosed();
        this.checkParamsNumber(parameterIndex);
        this.insertParameter(parameterIndex, x.toString());
    }

    protected void setTemporal(int parameterIndex, long epoch, ZoneId zone, Function<ZonedDateTime, Temporal> extractTemporal) throws SQLException {
        this.checkClosed();
        this.checkParamsNumber(parameterIndex);
        ZonedDateTime zdt = Instant.ofEpochMilli(epoch).atZone(zone);
        this.insertParameter(parameterIndex, extractTemporal.apply(zdt));
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), ZoneId.systemDefault(), (zdt) -> {
            return zdt.toLocalDate();
        });
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), ZoneId.systemDefault(), (zdt) -> {
            return zdt.toLocalTime();
        });
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), ZoneId.systemDefault(), (zdt) -> {
            return zdt.toLocalDateTime();
        });
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), cal.getTimeZone().toZoneId(), (zdt) -> {
            return zdt;
        });
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), cal.getTimeZone().toZoneId(), (zdt) -> {
            return zdt;
        });
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.setTemporal(parameterIndex, x.getTime(), cal.getTimeZone().toZoneId(), (zdt) -> {
            return zdt.toOffsetDateTime().toOffsetTime();
        });
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.checkClosed();
        this.insertParameter(parameterIndex, x.getArray());
    }
}
