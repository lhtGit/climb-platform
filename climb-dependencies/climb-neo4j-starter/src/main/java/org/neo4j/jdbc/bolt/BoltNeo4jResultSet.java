//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.jdbc.bolt;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.exceptions.value.Uncoercible;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.types.*;
import org.neo4j.driver.util.Pair;
import org.neo4j.jdbc.Neo4jArray;
import org.neo4j.jdbc.Neo4jConnection;
import org.neo4j.jdbc.Neo4jResultSet;
import org.neo4j.jdbc.impl.ListArray;
import org.neo4j.jdbc.utils.DataConverterUtils;
import org.neo4j.jdbc.utils.JSONUtils;
import org.neo4j.jdbc.utils.Neo4jInvocationHandler;
import org.neo4j.jdbc.utils.ObjectConverter;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.Callable;

@SuppressWarnings("all")
public class BoltNeo4jResultSet extends Neo4jResultSet {
    private final Iterator<Record> iterator;
    private final ResultSetMetaData metaData;
    private Record current;
    private final List<String> keys;
    private final List<Type> classes;
    private boolean flattened = false;
    private static final List<String> ACCEPTED_TYPES_FOR_FLATTENING = Arrays.asList("NODE", "RELATIONSHIP");
    private int flatten;
    private LinkedList<Record> prefetchedRecords = null;

    private BoltNeo4jResultSet(Statement statement, Result iterator, int... params) {
        super(statement, params);
        List<Record> recordList = iterator != null ? iterator.list() : Collections.emptyList();
        Optional<Record> first = recordList.stream().findFirst();
        this.iterator = iterator != null ? recordList.iterator() : null;
        this.keys = new ArrayList();
        this.classes = new ArrayList();
        this.prefetchedRecords = new LinkedList();

        try {
            this.flatten = ((Neo4jConnection)this.statement.getConnection()).getFlattening();
        } catch (Exception var7) {
            this.flatten = 0;
        }

        if (this.flatten != 0 && this.flatteningTypes(first)) {
            this.flattenResultSet();
            this.flattened = true;
        } else if (iterator != null) {
            this.keys.addAll(iterator.keys());
            first.ifPresent((record) -> {
                Iterator var2 = record.values().iterator();

                while(var2.hasNext()) {
                    Value value = (Value)var2.next();
                    this.classes.add(value.type());
                }

            });
        }

        this.metaData = BoltNeo4jResultSetMetaData.newInstance(false, this.classes, this.keys);
    }

    public static ResultSet newInstance(boolean debug, Statement statement, Result iterator, int... params) {
        ResultSet rs = new BoltNeo4jResultSet(statement, iterator, params);
        return (ResultSet)Proxy.newProxyInstance(BoltNeo4jResultSet.class.getClassLoader(), new Class[]{ResultSet.class}, new Neo4jInvocationHandler(rs, debug));
    }

    private void flattenResultSet() {
        for(int i = 0; (this.flatten == -1 || i < this.flatten) && this.iterator.hasNext(); ++i) {
            this.prefetchedRecords.add((Record)this.iterator.next());
            this.flattenRecord((Record)this.prefetchedRecords.getLast());
        }

    }

    private void flattenRecord(Record r) {
        Iterator var2 = r.fields().iterator();

        while(var2.hasNext()) {
            Pair<String, Value> pair = (Pair)var2.next();
            if (this.keys.indexOf(pair.key()) == -1) {
                this.keys.add((String)pair.key());
                this.classes.add(r.get((String)pair.key()).type());
            }

            Value val = r.get((String)pair.key());
            if (((String)ACCEPTED_TYPES_FOR_FLATTENING.get(0)).equals(((Value)pair.value()).type().name())) {
                this.flattenNode(val.asNode(), (String)pair.key());
            } else if (((String)ACCEPTED_TYPES_FOR_FLATTENING.get(1)).equals(((Value)pair.value()).type().name())) {
                this.flattenRelationship(val.asRelationship(), (String)pair.key());
            }
        }

    }

    private void flattenNode(Node node, String nodeKey) {
        if (this.keys.indexOf(nodeKey + ".id") == -1) {
            this.keys.add(nodeKey + ".id");
            this.classes.add(InternalTypeSystem.TYPE_SYSTEM.INTEGER());
            this.keys.add(nodeKey + ".labels");
            this.classes.add(InternalTypeSystem.TYPE_SYSTEM.LIST());
        }

        Iterator var3 = node.keys().iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            if (this.keys.indexOf(nodeKey + "." + key) == -1) {
                this.keys.add(nodeKey + "." + key);
                this.classes.add(node.get(key).type());
            }
        }

    }

    private void flattenRelationship(Relationship rel, String relationshipKey) {
        if (this.keys.indexOf(relationshipKey + ".id") == -1) {
            this.keys.add(relationshipKey + ".id");
            this.classes.add(InternalTypeSystem.TYPE_SYSTEM.INTEGER());
            this.keys.add(relationshipKey + ".type");
            this.classes.add(InternalTypeSystem.TYPE_SYSTEM.STRING());
        }

        Iterator var3 = rel.keys().iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            if (this.keys.indexOf(relationshipKey + "." + key) == -1) {
                this.keys.add(relationshipKey + "." + key);
                this.classes.add(rel.get(key).type());
            }
        }

    }

    public boolean flatteningTypes(Optional<Record> peek) {
        return (Boolean)peek.map((record) -> {
            return record.fields().stream();
        }).map((pairStream) -> {
            return pairStream.allMatch((pair) -> {
                return ACCEPTED_TYPES_FOR_FLATTENING.contains(((Value)pair.value()).type().name());
            });
        }).orElse(Boolean.FALSE);
    }

    protected boolean innerNext() throws SQLException {
        if (this.iterator == null) {
            throw new SQLException("ResultCursor not initialized");
        } else {
            if (!this.prefetchedRecords.isEmpty()) {
                this.current = (Record)this.prefetchedRecords.pop();
            } else if (this.iterator.hasNext()) {
                this.current = (Record)this.iterator.next();
            } else {
                this.current = null;
            }

            return this.current != null;
        }
    }

    public void close() throws SQLException {
        if (this.iterator == null) {
            throw new SQLException("ResultCursor not initialized");
        } else {
            this.isClosed = true;
        }
    }

    public boolean wasNull() throws SQLException {
        this.checkClosed();
        return this.wasNull;
    }

    public String getString(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return this.getStringFromValue(value);
    }

    public String getString(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return this.getStringFromValue(value);
    }

    private String getStringFromValue(Value value) {
        try {
            return value.isNull() ? null : value.asString();
        } catch (Uncoercible var3) {
            return JSONUtils.writeValueAsString(value.asObject());
        }
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return !value.isNull() && value.asBoolean();
    }

    private Value fetchPropertyValue(String key, String property) throws SQLException {
        try {
            Object value;
            if ("id".equals(property)) {
                value = new IntegerValue(this.current.get(key).asEntity().id());
            } else if ("labels".equals(property)) {
                Node node = this.current.get(key).asNode();
                List<Value> values = new ArrayList();
                Iterator var6 = node.labels().iterator();

                while(var6.hasNext()) {
                    String label = (String)var6.next();
                    values.add(new StringValue(label));
                }

                value = new ListValue((Value[])values.toArray(new Value[values.size()]));
            } else if ("type".equals(property)) {
                value = new StringValue(this.current.get(key).asRelationship().type());
            } else {
                value = this.current.get(key).get(property);
            }

            return (Value)value;
        } catch (Exception var8) {
            throw new SQLException("Column not present in ResultSet", var8);
        }
    }

    private Value fetchValueFromLabel(String label) throws SQLException {
        Value value;
        if (this.current.containsKey(label)) {
            value = this.current.get(label);
        } else {
            if (!this.flattened || !this.keys.contains(label)) {
                throw new SQLException("Column not present in ResultSet");
            }

            String[] labelKeys = label.split("\\.");
            value = this.fetchPropertyValue(labelKeys[0], labelKeys[1]);
        }

        this.wasNull = value.isNull();
        return value;
    }

    private Value fetchValueFromIndex(int index) throws SQLException {
        Value value;
        if (this.flattened && index > 0 && index - 1 <= this.keys.size()) {
            String[] indexKeys = ((String)this.keys.get(index - 1)).split("\\.");
            if (indexKeys.length > 1) {
                value = this.fetchPropertyValue(indexKeys[0], indexKeys[1]);
            } else {
                value = this.current.get((String)this.keys.get(index - 1));
            }
        } else {
            if (index <= 0 || index - 1 > this.current.size()) {
                throw new SQLException("Column not present in ResultSet");
            }

            value = this.current.get(index - 1);
        }

        this.wasNull = value.isNull();
        return value;
    }

    public int getInt(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return value.isNull() ? 0 : value.asInt();
    }

    public long getLong(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return value.isNull() ? 0L : value.asLong();
    }

    public int findColumn(String columnLabel) throws SQLException {
        this.checkClosed();
        if (!this.keys.contains(columnLabel)) {
            throw new SQLException("Column not present in ResultSet");
        } else {
            return this.keys.indexOf(columnLabel) + 1;
        }
    }

    public int getType() throws SQLException {
        this.checkClosed();
        return this.type;
    }

    public int getConcurrency() throws SQLException {
        this.checkClosed();
        return this.concurrency;
    }

    public int getHoldability() throws SQLException {
        this.checkClosed();
        return this.holdability;
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return !value.isNull() && value.asBoolean();
    }

    public int getInt(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return value.isNull() ? 0 : value.asInt();
    }

    public long getLong(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return value.isNull() ? 0L : value.asLong();
    }

    public float getFloat(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return value.isNull() ? 0.0F : value.asFloat();
    }

    public float getFloat(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return value.isNull() ? 0.0F : value.asFloat();
    }

    public short getShort(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return value.isNull() ? 0 : (short)value.asInt();
    }

    public short getShort(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return value.isNull() ? 0 : (short)value.asInt();
    }

    public double getDouble(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return value.isNull() ? 0.0D : value.asDouble();
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        if(value instanceof  NullValue){
            return null;
        }
        if(value instanceof StringValue){
            return new BigDecimal(value.asString());
        }
        return new BigDecimal(value.toString());
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        if(value instanceof  NullValue){
            return null;
        }
        if(value instanceof StringValue){
            return new BigDecimal(value.asString());
        }
        return new BigDecimal(value.toString());
    }

    public Neo4jArray getArray(int columnIndex) throws SQLException {
        this.checkClosed();
        List<Object> list = this.fetchValueFromIndex(columnIndex).asList();
        Object obj = list.isEmpty() ? new Object() : list.get(0);
        return new ListArray(list, Neo4jArray.getObjectType(obj));
    }

    public Neo4jArray getArray(String columnLabel) throws SQLException {
        this.checkClosed();
        List list = this.fetchValueFromLabel(columnLabel).asList();
        Object obj = list.isEmpty() ? new Object() : list.get(0);
        return new ListArray(list, Neo4jArray.getObjectType(obj));
    }

    public double getDouble(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return value.isNull() ? 0.0D : value.asDouble();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return this.metaData;
    }

    public Object getObject(int columnIndex) throws SQLException {
        this.checkClosed();
        Object obj = this.fetchValueFromIndex(columnIndex).asObject();
        return DataConverterUtils.convertObject(obj);
    }

    public Object getObject(String columnLabel) throws SQLException {
        this.checkClosed();
        Object obj = this.fetchValueFromLabel(columnLabel).asObject();
        return DataConverterUtils.convertObject(obj);
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        try {
            return this.getObject(type, () -> {
                return this.fetchValueFromLabel(columnLabel);
            }, () -> {
                return this.getObject(columnLabel);
            });
        } catch (Exception var4) {
            throw new SQLException(var4);
        }
    }

    private boolean isNeo4jDatatype(Class type) {
        return ObjectValueAdapter.class.isAssignableFrom(type);
    }

    private <T> T getObject(Class<T> type, Callable fetch, Callable getObject) throws Exception {
        this.checkClosed();
        if (type == null) {
            throw new SQLException("Type to cast cannot be null");
        } else if (this.isNeo4jDatatype(type)) {
            return (T) fetch.call();
        } else if (type == ZonedDateTime.class) {
            DateTimeValue value = (DateTimeValue)fetch.call();
            return (T) value.asZonedDateTime();
        } else if (type == LocalDateTime.class) {
            LocalDateTimeValue value = (LocalDateTimeValue)fetch.call();
            return (T) value.asLocalDateTime();
        } else if (type == IsoDuration.class) {
            DurationValue value = (DurationValue)fetch.call();
            return (T) value.asIsoDuration();
        } else if (type == LocalDate.class) {
            DateValue value = (DateValue)fetch.call();
            return (T) value.asLocalDate();
        } else if (type == LocalTime.class) {
            LocalTimeValue value = (LocalTimeValue)fetch.call();
            return (T) value.asLocalTime();
        } else if (type == OffsetTime.class) {
            TimeValue value = (TimeValue)fetch.call();
            return (T) value.asOffsetTime();
        } else if (type == Point.class) {
            PointValue value = (PointValue)fetch.call();
            return (T) value.asPoint();
        } else {
            Object obj = getObject.call();

            try {
                T ret = ObjectConverter.convert(obj, type);
                return ret;
            } catch (Exception var7) {
                throw new SQLException(var7);
            }
        }
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        try {
            return this.getObject(type, () -> {
                return this.fetchValueFromIndex(columnIndex);
            }, () -> {
                return this.getObject(columnIndex);
            });
        } catch (Exception var4) {
            throw new SQLException(var4);
        }
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        Object obj = this.getObject(columnLabel);
        String fromClass = obj.getClass().getCanonicalName();
        Class<?> toClass = (Class)map.get(fromClass);
        if (toClass == null) {
            throw new SQLException(String.format("Mapping for class: %s not found", fromClass));
        } else {
            try {
                Object ret = ObjectConverter.convert(obj, toClass);
                return ret;
            } catch (Exception var8) {
                throw new SQLException(var8);
            }
        }
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        Object obj = this.getObject(columnIndex);

        try {
            Object ret = ObjectConverter.convert(obj, (Class)map.get(obj.getClass().toString()));
            return ret;
        } catch (Exception var6) {
            throw new SQLException(var6);
        }
    }

    public Statement getStatement() {
        return this.statement;
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return DataConverterUtils.valueToTimestamp(value);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return DataConverterUtils.valueToTimestamp(value);
    }

    public Date getDate(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return DataConverterUtils.valueToDate(value);
    }

    public Date getDate(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return DataConverterUtils.valueToDate(value);
    }

    public Time getTime(int columnIndex) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return DataConverterUtils.valueToTime(value);
    }

    public Time getTime(String columnLabel) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return DataConverterUtils.valueToTime(value);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return DataConverterUtils.valueToTimestamp(value, cal.getTimeZone().toZoneId());
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return DataConverterUtils.valueToTimestamp(value, cal.getTimeZone().toZoneId());
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromIndex(columnIndex);
        return DataConverterUtils.valueToTime(value, cal);
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        this.checkClosed();
        Value value = this.fetchValueFromLabel(columnLabel);
        return DataConverterUtils.valueToTime(value, cal);
    }
}
