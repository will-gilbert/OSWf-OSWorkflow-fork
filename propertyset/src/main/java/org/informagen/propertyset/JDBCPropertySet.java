package org.informagen.propertyset;

import org.informagen.propertyset.*;
import org.informagen.propertyset.exceptions.PropertySetException;
import org.informagen.propertyset.exceptions.InvalidPropertyTypeException;


import org.informagen.propertyset.util.ByteArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.sql.*;

// Java - Collections
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Java - Util
import java.util.Date;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

/**
 * This is an implementation of a property set manager for JDBC. It relies on
 * one table, called "os_propertyset" that has four columns: "type" (integer),
 * "keyValue" (string), "globalKey" (string), and "value" (string). This is not
 * likely to be enough for people who store BLOBS as properties. Of course,
 * those people need to get a life.
 * <p>
 *
 * For Postgres(?):<br>
 * CREATE TABLE OS_PROPERTYENTRY (GLOBAL_KEY varchar(255), ITEM_KEY varchar(255), ITEM_TYPE smallint, STRING_VALUE varchar(255), DATE_VALUE timestamp, DATA_VALUE oid, FLOAT_VALUE float8, NUMBER_VALUE numeric, primary key (GLOBAL_KEY, ITEM_KEY));
 * <p>
 *
 * For Oracle (Thanks to Michael G. Slack!):<br>
 * CREATE TABLE OS_PROPERTYENTRY (GLOBAL_KEY varchar(255), ITEM_KEY varchar(255), ITEM_TYPE smallint, STRING_VALUE varchar(255), DATE_VALUE date, DATA_VALUE long raw, FLOAT_VALUE float, NUMBER_VALUE numeric, primary key (GLOBAL_KEY, ITEM_KEY));
 * <p>
 *
 * Other databases may require small tweaks to the table creation scripts!
 *
 * <p>
 *
 * <b>Required Args</b>
 * <ul>
 *  <li><b>globalKey</b> - the globalKey to use with this PropertySet</li>
 * </ul>
 * <p>
 *
 * <b>Required OSWfConfiguration</b>
 * <ul>
 *  <li><b>datasource</b> - JNDI path for the DataSource</li>
 *  <li><b>table.name</b> - the table name</li>
 *  <li><b>col.globalKey</b> - column name for the globalKey</li>
 *  <li><b>col.itemKey</b> - column name for the itemKey</li>
 *  <li><b>col.itemType</b> - column name for the itemType</li>
 *  <li><b>col.string</b> - column name for the string value</li>
 *  <li><b>col.date</b> - column name for the date value</li>
 *  <li><b>col.data</b> - column name for the data value</li>
 *  <li><b>col.float</b> - column name for the float value</li>
 *  <li><b>col.number</b> - column name for the number value</li>
 * </ul>
 *
 */
 
public class JDBCPropertySet extends AbstractPropertySet {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    // config
    protected DataSource dataSource;
    protected String colData;
    protected String colDate;
    protected String colFloat;
    protected String colGlobalKey;
    protected String colItemKey;
    protected String colItemType;
    protected String colNumber;
    protected String colString;

    // args
    protected String globalKey;
    protected String tableName;
    protected boolean closeConnWhenDone = false;

    public JDBCPropertySet() {}

    public JDBCPropertySet(Map<String,String> parameters, Map<String,Object>args) {

        // args
        if(args != null)
            globalKey = (String) args.get("globalKey");
 
        if(globalKey == null)
            globalKey = "";
        
        // config
        String dataSourceName = parameters.get("jndi");

        if (dataSourceName != null) {
            try {
                dataSource = (DataSource)lookup(dataSourceName);
                
                if (dataSource == null) 
                    dataSource = (DataSource) new javax.naming.InitialContext().lookup(dataSourceName);
                
            } catch (Exception exception) {
                log.error("Error looking up DataSource at " + dataSourceName, exception);
                return;
            }
            
        } 
        
        // Explcitly passed 'datasource' overrides configuratiojn
        if( args != null && args.get("datasource") != null) 
            dataSource = (DataSource)args.get("datasource");
            
        tableName = parameters.get("table.name");
        colGlobalKey = parameters.get("col.globalKey");
        colItemKey = parameters.get("col.itemKey");
        colItemType = parameters.get("col.itemType");
        colString = parameters.get("col.string");
        colDate = parameters.get("col.date");
        colData = parameters.get("col.data");
        colFloat = parameters.get("col.float");
        colNumber =  parameters.get("col.number");
    }


    // M E T H O D S  -------------------------------------------------------------------------

    public Collection<String> getKeys(String prefix, Type type) throws PropertySetException {
        
        if (prefix == null) {
            prefix = "";
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            conn = getConnection();

            String sql = "SELECT " + colItemKey + " FROM " + tableName + " WHERE " + colItemKey + " LIKE ? AND " + colGlobalKey + " = ?";

            if (type == null) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, prefix + "%");
                ps.setString(2, globalKey);
            } else {
                sql = sql + " AND " + colItemType + " = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, prefix + "%");
                ps.setString(2, globalKey);
                ps.setInt(3, type.getValue());
            }

            List<String> list = new ArrayList<String>();
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString(colItemKey));
            }

            return list;
            
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, rs);
        }
    }

    public Type getType(String key) throws PropertySetException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT " + colItemType + " FROM " + tableName + " WHERE " + colGlobalKey + " = ? AND " + colItemKey + " = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, globalKey);
            ps.setString(2, key);

            rs = ps.executeQuery();

            Type type = null;

            if (rs.next()) {
                type = Type.getType(rs.getInt(colItemType));
            }

            return type;
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, rs);
        }
    }

    public boolean exists(String key) throws PropertySetException {
        return getType(key) != null;
    }

    public void remove() throws PropertySetException {
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            String sql = "DELETE FROM " + tableName + " WHERE " + colGlobalKey + " = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, globalKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, null);
        }
    }

    public void remove(String key) throws PropertySetException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            String sql = "DELETE FROM " + tableName + " WHERE " + colGlobalKey + " = ? AND " + colItemKey + " = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, globalKey);
            ps.setString(2, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, null);
        }
    }

    public boolean supportsType(Type type) {
        
        switch (type) {
            case PROPERTIES:
            case XML:
                return false;
        }

        return true;
    }

    public Connection getConnection() throws SQLException {
        closeConnWhenDone = true;
        return dataSource.getConnection();
    }

    protected void setImpl(Type type, String key, Object value) throws PropertySetException {
        
        if (value == null) 
            throw new PropertySetException("JDBCPropertySet does not allow for null values to be stored");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();

            String sql = "UPDATE " + tableName + " SET " + colString + " = ?, " + colDate + " = ?, " + colData + " = ?, " + colFloat + " = ?, " + colNumber + " = ?, " + colItemType + " = ? " + " WHERE " + colGlobalKey + " = ? AND " + colItemKey + " = ?";
            ps = conn.prepareStatement(sql);
            setValues(ps, type, key, value);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                // ok, this is a new value, insert it
                sql = "INSERT INTO " + tableName + " (" + colString + ", " + colDate + ", " + colData + ", " + colFloat + ", " + colNumber + ", " + colItemType + ", " + colGlobalKey + ", " + colItemKey + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                ps.close(); // previous ps, before reassigning reference.
                ps = conn.prepareStatement(sql);
                setValues(ps, type, key, value);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, null);
        }
    }

    protected void cleanup(Connection connection, Statement statement, ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException ex) {
                log.error("Error closing resultset", ex);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                log.error("Error closing statement", ex);
            }
        }

        if ((connection != null) && closeConnWhenDone) {
            try {
                connection.close();
            } catch (SQLException ex) {
                log.error("Error closing connection", ex);
            }
        }
    }

    protected Object get(Type type, String key) throws PropertySetException {
        String sql = "SELECT " + colItemType + ", " + colString + ", " + colDate + ", " + colData + ", " + colFloat + ", " + colNumber + " FROM " + tableName + " WHERE " + colItemKey + " = ? AND " + colGlobalKey + " = ?";

        Object o = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, key);
            ps.setString(2, globalKey);

            int propertyType;
            rs = ps.executeQuery();

            if (rs.next()) {
                propertyType = rs.getInt(colItemType);

                if (propertyType != type.getValue()) {
                    throw new InvalidPropertyTypeException();
                }

                switch (type) {
                case BOOLEAN:

                    int boolVal = rs.getInt(colNumber);
                    o = new Boolean(boolVal == 1);

                    break;

                case DATA:
                    o = rs.getBytes(colData);

                    break;

                case DATE:
                    o = rs.getTimestamp(colDate);

                    break;

                case OBJECT:

                    InputStream bis = rs.getBinaryStream(colData);

                    try {
                        ObjectInputStream is = new ObjectInputStream(bis);
                        o = is.readObject();
                    } catch (IOException e) {
                        throw new PropertySetException("Error de-serializing object for key '" + key + "' from store:" + e);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case DOUBLE:
                    o = new Double(rs.getDouble(colFloat));

                    break;

                case INT:
                    o = new Integer(rs.getInt(colNumber));

                    break;

                case LONG:
                    o = new Long(rs.getLong(colNumber));

                    break;

                case STRING:
                    o = rs.getString(colString);

                    break;

                case TEXT:
                    o = rs.getString(colString);

                    break;

                default:
                    throw new InvalidPropertyTypeException("JDBCPropertySet doesn't support this type yet.");
                }
            }
        } catch (SQLException e) {
            throw new PropertySetException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new PropertySetException(e.getMessage());
        } finally {
            cleanup(conn, ps, rs);
        }

        return o;
    }

    private void setValues(PreparedStatement ps, Type type, String key, Object value) throws SQLException, PropertySetException {
        ps.setNull(1, Types.VARCHAR);
        ps.setNull(2, Types.TIMESTAMP);
        ps.setNull(3, Types.VARBINARY);
        ps.setNull(4, Types.FLOAT);
        ps.setNull(5, Types.NUMERIC);
        ps.setInt(6, type.getValue());
        ps.setString(7, globalKey);
        ps.setString(8, key);

        switch (type) {
        case BOOLEAN:

            Boolean boolVal = (Boolean) value;
            ps.setInt(5, boolVal.booleanValue() ? 1 : 0);

            break;

        case DATA:

            if (value instanceof ByteArray) {
                ByteArray data = (ByteArray) value;
                ps.setBytes(3, data.getBytes());
            }

            if (value instanceof byte[]) {
                ps.setBytes(3, (byte[]) value);
            }

            break;

        case OBJECT:

            if (!(value instanceof Serializable)) {
                throw new PropertySetException(value.getClass() + " does not implement java.io.Serializable");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ObjectOutputStream os = new ObjectOutputStream(bos);
                os.writeObject(value);
                ps.setBytes(3, bos.toByteArray());
            } catch (IOException e) {
                throw new PropertySetException("I/O Error when serializing object:" + e);
            }

            break;

        case DATE:

            Date date = (Date) value;
            ps.setTimestamp(2, new Timestamp(date.getTime()));

            break;

        case DOUBLE:

            Double d = (Double) value;
            ps.setDouble(4, d.doubleValue());

            break;

        case INT:

            Integer i = (Integer) value;
            ps.setInt(5, i.intValue());

            break;

        case LONG:

            Long l = (Long) value;
            ps.setLong(5, l.longValue());

            break;

        case STRING:
            ps.setString(1, (String) value);

            break;

        case TEXT:
            ps.setString(1, (String) value);

            break;

        default:
            throw new PropertySetException("This type isn't supported!");
        }
    }

    private Object lookup(String location) throws NamingException {
        
        try {
            
            InitialContext context = new InitialContext();

            try {
                return context.lookup(location);
            } catch (NamingException e) {
                return context.lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            throw e;
        }
    }
}
