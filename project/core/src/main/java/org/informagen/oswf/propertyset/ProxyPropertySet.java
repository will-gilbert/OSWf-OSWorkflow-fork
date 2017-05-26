package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.*;

import org.informagen.oswf.propertyset.util.PropertySetCloner;
import org.informagen.oswf.propertyset.SerializablePropertySet;
import org.informagen.oswf.propertyset.exceptions.PropertySetException;

import org.w3c.dom.Document;

import java.io.Serializable;

// Java - Collections
import java.util.Collection;
import java.util.Map;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 * A PropertySet which decorates another PropertySet and caches the results.
 * <p>
 *
 * This is only sensible to use in a situation where your application has exclusive access
 *      to the underlying PropertySet (otherwise it can be dangerous to use).
 * <p>
 *
 * You can also use this PropertySet to bulk load data from the decorated property set
 *     when the PS is created.
 * <p>
 *
 * <b>THINK BEFORE USING THIS - IT COULD HURT YOU ;)</b>
 * <p>
 *
 * <b>Required Args</b>
 * <ul>
 *  <li><b>PropertySet</b> - the PropertySet that will be cached</li>
 * </ul>
 * <p>
 *
 * <b>Optional Args</b>
 * <ul>
 *  <li><b>bulkload</b> - Boolean that, when true, causes the cache to be bulk loaded</li>
 *  <li><b>proxyName</b> - the name that can be used to retrieve a SerializablePropertySet, defaults to "serializable"</li>
 * </ul>
 */
 
 
public class ProxyPropertySet implements PropertySet, Serializable {

    PropertySet proxyPropertySet;
    PropertySet propertySet;
    
    private ProxyPropertySet() {}
     
    public ProxyPropertySet(PropertySet propertySet) {
        this(propertySet, false);
    }

    public ProxyPropertySet(PropertySet propertySet, boolean bulkload) {
        PropertySet proxyPropertySet = PropertySetFactory.getInstance().createPropertySet("memory");
        initialize(propertySet, proxyPropertySet, bulkload);
    }
     
    public ProxyPropertySet(PropertySet propertySet, PropertySet proxyPropertySet) {
        this(propertySet, proxyPropertySet, false);
    }
     
    public ProxyPropertySet(PropertySet propertySet, PropertySet proxyPropertySet, boolean bulkload) {
        initialize(propertySet, proxyPropertySet, bulkload);
    }

    // Used by the PropertySetFactory
    public ProxyPropertySet(Map<String,String> parameters, Map<String,Object> args) {

        PropertySet propertySet = (PropertySet)args.get("PropertySet");

        String proxyName = parameters.get("proxyName");
        if (proxyName == null) 
            proxyName = "memory";

        PropertySet proxyPropertySet = PropertySetFactory.getInstance().createPropertySet(proxyName, args);
 
         boolean bulkload = false;
         if(args != null)
            bulkload = (args.get("bulkload") != null) ? (Boolean) args.get("bulkload") : false;
         
        initialize(propertySet, proxyPropertySet, bulkload);
    }

    private void initialize(PropertySet propertySet, PropertySet proxyPropertySet, boolean bulkload) {
        
        this.propertySet = propertySet;
        this.proxyPropertySet = proxyPropertySet;

        if ( bulkload )
            PropertySetCloner.clone(propertySet, proxyPropertySet);
    }


    // Methods ////////////////////////////////////////////////////////////////

    public void setAsActualType(String key, Object value) throws PropertySetException {
        if (value instanceof Boolean) {
            setBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            setInt(key, (Integer)value);
        } else if (value instanceof Long) {
            setLong(key, (Long) value);
        } else if (value instanceof Double) {
            setDouble(key, (Double) value);
        } else if (value instanceof String) {
            if (value.toString().length() > 255) {
                setText(key, (String)value);
            } else {
                setString(key, (String) value);
            }
        } else if (value instanceof Date) {
            setDate(key, (Date) value);
        } else if (value instanceof Document) {
            setXML(key, (Document) value);
        } else if (value instanceof byte[]) {
            setData(key, (byte[]) value);
        } else if (value instanceof Properties) {
            setProperties(key, (Properties) value);
        } else {
            setObject(key, value);
        }
    }

    public Object getAsActualType(String key) throws PropertySetException {

        Type type = getType(key);
        Object value = null;

        switch (type) {
        case BOOLEAN:
            value = new Boolean(getBoolean(key));
            break;

        case INT:
            value = new Integer(getInt(key));
            break;

        case LONG:
            value = new Long(getLong(key));
            break;

        case DOUBLE:
            value = new Double(getDouble(key));
            break;

        case STRING:
            value = getString(key);
            break;

        case TEXT:
            value = getText(key);
            break;

        case DATE:
            value = getDate(key);
            break;

        case XML:
            value = getXML(key);
            break;

        case DATA:
            value = getData(key);
            break;

        case PROPERTIES:
            value = getProperties(key);
            break;

        case OBJECT:
            value = getObject(key);
            break;
        }

        return value;
    }

    public void setBoolean(String key, boolean value) throws PropertySetException {
        propertySet.setBoolean(key, value);
        proxyPropertySet.setBoolean(key, value);
    }

    public boolean getBoolean(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setBoolean(key, propertySet.getBoolean(key));
        }

        return proxyPropertySet.getBoolean(key);
    }

    public void setData(String key, byte[] value) throws PropertySetException {
        propertySet.setData(key, value);
        proxyPropertySet.setData(key, value);
    }

    public byte[] getData(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setData(key, propertySet.getData(key));
        }

        return proxyPropertySet.getData(key);
    }

    public void setDate(String key, Date value) throws PropertySetException {
        propertySet.setDate(key, value);
        proxyPropertySet.setDate(key, value);
    }

    public Date getDate(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setDate(key, propertySet.getDate(key));
        }

        return proxyPropertySet.getDate(key);
    }

    public void setDouble(String key, double value) throws PropertySetException {
        propertySet.setDouble(key, value);
        proxyPropertySet.setDouble(key, value);
    }

    public double getDouble(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setDouble(key, propertySet.getDouble(key));
        }

        return proxyPropertySet.getDouble(key);
    }

    public void setInt(String key, int value) throws PropertySetException {
        propertySet.setInt(key, value);
        proxyPropertySet.setInt(key, value);
    }

    public int getInt(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setInt(key, propertySet.getInt(key));
        }

        return proxyPropertySet.getInt(key);
    }

    public Collection<String> getKeys() throws PropertySetException {
        return propertySet.getKeys();
    }

    public Collection<String> getKeys(Type type) throws PropertySetException {
        return propertySet.getKeys(type);
    }

    public Collection<String> getKeys(String prefix) throws PropertySetException {
        return propertySet.getKeys(prefix);
    }

    public Collection<String> getKeys(String prefix, Type type) throws PropertySetException {
        return propertySet.getKeys(prefix, type);
    }

    public void setLong(String key, long value) throws PropertySetException {
        propertySet.setLong(key, value);
        proxyPropertySet.setLong(key, value);
    }

    public long getLong(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setLong(key, propertySet.getLong(key));
        }

        return proxyPropertySet.getLong(key);
    }

    public void setObject(String key, Object value) throws PropertySetException {
        propertySet.setObject(key, value);
        proxyPropertySet.setObject(key, value);
    }

    public Object getObject(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setObject(key, propertySet.getObject(key));
        }

        return proxyPropertySet.getObject(key);
    }

    public void setProperties(String key, Properties value) throws PropertySetException {
        propertySet.setProperties(key, value);
        proxyPropertySet.setProperties(key, value);
    }

    public Properties getProperties(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setProperties(key, propertySet.getProperties(key));
        }

        return proxyPropertySet.getProperties(key);
    }

    public boolean isSettable(String property) {
        return propertySet.isSettable(property);
    }

    public void setString(String key, String value) throws PropertySetException {
        propertySet.setString(key, value);
        proxyPropertySet.setString(key, value);
    }

    public String getString(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setString(key, propertySet.getString(key));
        }

        return proxyPropertySet.getString(key);
    }

    public void setText(String key, String value) throws PropertySetException {
        propertySet.setText(key, value);
        proxyPropertySet.setText(key, value);
    }

    public String getText(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setText(key, propertySet.getText(key));
        }

        return proxyPropertySet.getText(key);
    }

    public Type getType(String key) throws PropertySetException {
        return propertySet.getType(key);
    }

    public void setXML(String key, Document value) throws PropertySetException {
        propertySet.setXML(key, value);
        proxyPropertySet.setXML(key, value);
    }

    public Document getXML(String key) throws PropertySetException {
        if (!proxyPropertySet.exists(key)) {
            proxyPropertySet.setXML(key, propertySet.getXML(key));
        }

        return proxyPropertySet.getXML(key);
    }

    public boolean exists(String key) throws PropertySetException {
        return propertySet.exists(key);
    }

    public void remove() throws PropertySetException {
        proxyPropertySet.remove();
        propertySet.remove();
    }

    public void remove(String key) throws PropertySetException {
        proxyPropertySet.remove(key);
        propertySet.remove(key);
    }

    public boolean supportsType(Type type) {
        return propertySet.supportsType(type);
    }

    public boolean supportsTypes() {
        return propertySet.supportsTypes();
    }
}
