package org.informagen.propertyset;

import org.informagen.propertyset.PropertySet;
import org.informagen.propertyset.Type;
import org.informagen.propertyset.util.ByteArray;
import org.informagen.propertyset.exceptions.PropertySetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;

import java.io.Serializable;

// Java - Collections
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 ** PropertySet composed of a collection of other propertysets.
 **
 ** Tried each of the propertysets to find a value, tries to be
 ** as fault tolerant as possible, in that when any error occurs,
 ** it simply tries the operation on the next set.
 ** <p>
 **
 ** <b>Named args</b>
 ** <ul>
 **  <li><b>PropertySets</b> - a List of PropertySet</li>
 ** </ul>
 **
 */

public class AggregatePropertySet extends AbstractPropertySet implements Serializable {
    

    private List<PropertySet> propertySets = new ArrayList<PropertySet>();


    public AggregatePropertySet() {
        this(null, null);
    }

    public AggregatePropertySet(Map<String,Object>args) {
        this(null, args);
    }

    @SuppressWarnings("unchecked")
    public AggregatePropertySet(Map<String,String> config, Map<String,Object>args) {

        List<PropertySet> propertySets = null;
        Object arg = args.get("PropertySets");
            
        if(arg instanceof List<?>)
            propertySets = (List<PropertySet>)arg;
    
        if (propertySets != null) 
            this.propertySets.addAll(propertySets);
    }

    // M E T H O D S --------------------------------------------------------------------------

    public Collection<String> getKeys(String prefix, Type type) throws PropertySetException {

        Collection<String> keys = new HashSet<String>();
        
        for(PropertySet propertySet : propertySets) {
            
            if(propertySet.supportsType(type) == false)
                continue;
                
            try {
                keys.addAll(propertySet.getKeys(prefix, type));
            } catch (PropertySetException ex) {}
        }

        return keys;
    }

    public boolean supportsType(Type type) {

        for(PropertySet propertySet : propertySets) {

            try {
                if(propertySet.supportsType(type))
                    return true;
            } catch (PropertySetException ex) {}
        }

        return false;
    }

    public boolean supportsTypes() {

        for(PropertySet propertySet : propertySets) {

            try {
                if(propertySet.supportsTypes())
                    continue;
            } catch (PropertySetException ex) {}
        }

        return false;
    }


    public boolean isSettable(String property) {
 
        // Is this property settable in any of the PropertySets?
        for(PropertySet propertySet : propertySets) {

            if (propertySet.isSettable(property)) {
                return true;
            }
        }

        return false;
    }

    /**
     ** Checks all propertysets for the specified property. If a match 
     **   is found, the type for the match is returned.
     **
     ** Note that the first match is what is checked, other propertysets
     **   might also define this key, but they would not be checked.
     */

    public Type getType(String key) throws PropertySetException {

        for(PropertySet propertySet : propertySets) {
 
            if( propertySet.exists(key) == false)
                continue;
            
            try {
                return propertySet.getType(key);
            } catch (UnsupportedOperationException ex) {
            } catch (PropertySetException ex) {}
        }

        throw new PropertySetException("Key '" + key + "' not found");
    }

    public boolean exists(String key) throws PropertySetException {

        for(PropertySet propertySet : propertySets) {
            try {
                if (propertySet.exists(key)) 
                    return true;
            } catch (PropertySetException ex) {}
        }

        return false;
    }

    public void remove() throws PropertySetException {
        
        for(PropertySet propertySet : propertySets)
            propertySet.remove();
            
    }

    public void remove(String key) throws PropertySetException {

        for(PropertySet propertySet : propertySets) {
            try {
                propertySet.remove(key);
            } catch (PropertySetException ex) { }
        }
        
    }

    /**
     * Attempts to set the property in each of the PropertySets.
     */

    protected void setImpl(Type type, String key, Object value) throws PropertySetException {

        for(PropertySet propertySet : propertySets) {

            if (propertySet.isSettable(key) == false)
                continue;
                
            switch (type) {
                
                case BOOLEAN:
                    propertySet.setBoolean(key, ((Boolean)value).booleanValue());
                    continue;

                case INT:
                    propertySet.setInt(key, ((Number)value).intValue());
                    continue;

                case LONG:
                    propertySet.setLong(key, ((Number)value).longValue());
                    continue;

                case DOUBLE:
                    propertySet.setDouble(key, ((Number)value).doubleValue());
                    continue;

                case STRING:
                    propertySet.setString(key, (String)value);
                    continue;

                case TEXT:
                    propertySet.setText(key, (String)value);
                    continue;

                case DATE:
                    propertySet.setDate(key, (Date)value);
                    continue;

                case OBJECT:
                    propertySet.setObject(key, value);
                    continue;

                case PROPERTIES:
                    propertySet.setProperties(key, (Properties)value);
                    continue;

                case XML:
                    propertySet.setXML(key, (Document) value);
                    continue;

                case DATA:
                    propertySet.setData(key, ((ByteArray)value).getBytes());
                    continue;
            }
        }
    }


    protected Object get(Type type, String key) throws PropertySetException {

        for(PropertySet propertySet : propertySets) {

            if( propertySet.exists(key) == false)
                continue;
            
            // Since propertySet.get() is protected, we have to call 
            //   'getXXX()' in the current PropertySet, which in turn will call 'get'
            //   turn will call 'get'

            try {

                switch (type) {
                    
                    case BOOLEAN:
                        boolean bool = propertySet.getBoolean(key);
                        return (bool) ? Boolean.TRUE : Boolean.FALSE;
                        //break;

                    case INT:
                        int maybeInt = propertySet.getInt(key);
                        if (maybeInt != 0) 
                            return new Integer(maybeInt);
                        break;

                    case LONG:
                        long maybeLong = propertySet.getLong(key);
                        if (maybeLong != 0) 
                            return new Long(maybeLong);
                        break;

                    case DOUBLE:
                        double maybeDouble = propertySet.getDouble(key);
                        if (maybeDouble != 0)
                            return new Double(maybeDouble);
                        break;

                    case STRING:
                        String string = propertySet.getString(key);
                        if (string != null) 
                            return string;
                        break;

                    case TEXT:
                        String text = propertySet.getText(key);
                        if (text != null)
                            return text;
                        break;

                    case DATE:
                        Date date = propertySet.getDate(key);
                        if (date != null) 
                            return date;
                        break;

                    case OBJECT:

                        Object obj = propertySet.getObject(key);

                        if (obj != null) {
                            return obj;
                        }

                        break;

                    case XML:
                        Document doc = propertySet.getXML(key);
                        if (doc != null) 
                            return doc;
                        break;

                    case DATA:
                        byte[] data = propertySet.getData(key);
                        if (data != null) 
                            return data;
                        break;

                    case PROPERTIES:
                        Properties p = propertySet.getProperties(key);
                        if (p != null) 
                            return p;
                        break;
                }
            } catch (PropertySetException ex) { }
        }

        return null;
    }
}
