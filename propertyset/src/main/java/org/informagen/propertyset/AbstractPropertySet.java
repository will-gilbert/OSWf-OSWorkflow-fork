package org.informagen.propertyset;

import org.informagen.propertyset.Type;
import org.informagen.propertyset.util.ByteArray;

import org.informagen.propertyset.exceptions.PropertySetException;
import org.informagen.propertyset.exceptions.IllegalPropertyException;

import org.w3c.dom.Document;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
import java.util.Collection;
import java.util.Map;

// Java - Util
import java.util.Date;
import java.util.Properties;

/**
 * Base implementation of PropertySet.
 *
 * <p>Performs necessary casting for get???/set??? methods which wrap around the
 * following 2 methods which are declared <code>protected abstract</code> and need
 * to be implemented by subclasses:</p>
 *
 *
 * <p>The following methods are declared <code>public abstract</code> and are the
 * remainder of the methods that need to be implemented at the very least:</p>
 *
 * <ul>
 * <li> {@link #exists(java.lang.String)} </li>
 * <li> {@link #remove(java.lang.String)} </li>
 * </ul>
 *
 * <p>The <code>supports???</code> methods are implemented and all return true by default.
 * Override if necessary.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @version $Revision: 151 $
 */
public abstract class AbstractPropertySet implements PropertySet {

    protected static final Logger log = LoggerFactory.getLogger("org.informagen.propertyset");


    // A B S T R A C T   M E T H O D S  -------------------------------------------------------

    public abstract boolean exists(String key) throws PropertySetException;
    
    public abstract void remove() throws PropertySetException;

    public abstract Type getType(String key) throws PropertySetException;

    protected abstract void setImpl(Type type, String key, Object value) throws PropertySetException;

    protected abstract Object get(Type type, String key) throws PropertySetException;
    
    // M E T H O D S  -------------------------------------------------------------------------

    public void setAsActualType(String key, Object value) throws PropertySetException {

        Type type;

        if (value instanceof Boolean) {
            type = Type.BOOLEAN;
        } else if (value instanceof Integer) {
            type = Type.INT;
        } else if (value instanceof Long) {
            type = Type.LONG;
        } else if (value instanceof Double) {
            type = Type.DOUBLE;
        } else if (value instanceof String) {
            if (value.toString().length() > 255) {
                type = Type.TEXT;
            } else {
                type = Type.STRING;
            }
        } else if (value instanceof Date) {
            type = Type.DATE;
        } else if (value instanceof Document) {
            type = Type.XML;
        } else if (value instanceof byte[]) {
            type = Type.DATA;
        } else if (value instanceof Properties) {
            type = Type.PROPERTIES;
        } else {
            type = Type.OBJECT;
        }

        set(type, key, value);
    }

    public Object getAsActualType(String key) throws PropertySetException {

        Type type = getType(key);
        Object value = null;

        switch (type) {
            case BOOLEAN: value = new Boolean(getBoolean(key)); break;

            case INT: value = new Integer(getInt(key)); break;

            case LONG: value = new Long(getLong(key)); break;

            case DOUBLE: value = new Double(getDouble(key)); break;

            case STRING: value = getString(key); break;

            case TEXT: value = getText(key); break;

            case DATE: value = getDate(key); break;

            case XML: value = getXML(key); break;

            case DATA: value = getData(key); break;

            case PROPERTIES: value = getProperties(key); break;

            case OBJECT: value = getObject(key); break;
        }

        return value;
    }

    public void setBoolean(String key, boolean value) {
        set(Type.BOOLEAN, key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public boolean getBoolean(String key) {
        try {
            return ((Boolean) get(Type.BOOLEAN, key)).booleanValue();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Constructs {@link org.informagen.propertyset.util.ByteArray} wrapper around bytes.
     */
    public void setData(String key, byte[] value) {
        set(Type.DATA, key, new ByteArray(value));
    }

    /**
     * Casts to {@link org.informagen.propertyset.util.ByteArray} and returns bytes.
     */
    public byte[] getData(String key) {
        try {
            Object data = get(Type.DATA, key);

            if (data instanceof ByteArray) {
                return ((ByteArray) data).getBytes();
            } else if (data instanceof byte[]) {
                return (byte[]) data;
            }
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    public void setDate(String key, Date value) {
        set(Type.DATE, key, value);
    }

    public Date getDate(String key) {
        try {
            return (Date) get(Type.DATE, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setDouble(String key, double value) {
        set(Type.DOUBLE, key, new Double(value));
    }

    public double getDouble(String key) {
        try {
            return ((Double) get(Type.DOUBLE, key)).doubleValue();
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    public void setInt(String key, int value) {
        set(Type.INT, key, new Integer(value));
    }

    public int getInt(String key) {
        try {
            return ((Integer) get(Type.INT, key)).intValue();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Calls <code>getKeys(null,null)</code>
     */
    public Collection<String> getKeys() throws PropertySetException {
        return getKeys(null, null);
    }

    /**
     * Calls <code>getKeys(null,type)</code>
     */
    public Collection<String> getKeys(Type type) throws PropertySetException {
        return getKeys(null, type);
    }

    /**
     * Calls <code>getKeys(prefix, null)</code>
     */
    public Collection<String> getKeys(String prefix) throws PropertySetException {
        return getKeys(prefix, null);
    }

    public void setLong(String key, long value) {
        set(Type.LONG, key, new Long(value));
    }

    public long getLong(String key) {
        try {
            return ((Long) get(Type.LONG, key)).longValue();
        } catch (NullPointerException e) {
            return 0L;
        }
    }

    public void setObject(String key, Object value) {
        set(Type.OBJECT, key, value);
    }

    public Object getObject(String key) {
        try {
            return get(Type.OBJECT, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setProperties(String key, Properties value) {
        set(Type.PROPERTIES, key, value);
    }

    public Properties getProperties(String key) {
        try {
            return (Properties) get(Type.PROPERTIES, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns true.
     */
    public boolean isSettable(String property) {
        return true;
    }

    /**
     * Throws IllegalPropertyException if value length greater than 255.
     */
    public void setString(String key, String value) {
        if ((value != null) && (value.length() > 255)) {
            throw new IllegalPropertyException("String exceeds 255 characters.");
        }

        set(Type.STRING, key, value);
    }

    public String getString(String key) {
        try {
            return (String) get(Type.STRING, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setText(String key, String value) {
        set(Type.TEXT, key, value);
    }

    public String getText(String key) {
        try {
            return (String) get(Type.TEXT, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setXML(String key, Document value) {
        set(Type.XML, key, value);
    }

    public Document getXML(String key) {
        try {
            return (Document) get(Type.XML, key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Default returns true for all types
     */
    public boolean supportsType(Type type) {
        return true;
    }

    /**
     * Default returns true
     */
    public boolean supportsTypes() {
        return true;
    }

    /**
     * Simple human readable representation of contents of PropertySet.
     */

    public String toString() {
        
        StringBuffer result = new StringBuffer();
        result.append(getClass().getName());
        result.append(" {\n");

        for(String key : getKeys()) {
            
            Type type = getType(key);

            if (type != null) {
                result.append('\t');
                result.append(key);
                result.append(" = ");
                result.append(get(type, key));
                result.append('\n');
            }
        }

        result.append("}\n");

        return result.toString();
    }

    protected String type(Type type) {
        return (type != null) ? type.getName() : null;
    }

    protected Type type(String type) {
        
        if (type == null) 
            return null;

        type = type.toUpperCase();
        
        return Type.getType(type.toUpperCase());

    }

    private void set(Type type, String key, Object value) throws PropertySetException {
        //we're ok this far, so call the actual setter.
        setImpl(type, key, value);
    }
}
