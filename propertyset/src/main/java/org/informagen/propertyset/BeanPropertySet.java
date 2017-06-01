package org.informagen.propertyset;

import org.informagen.propertyset.*;

import org.informagen.propertyset.exceptions.PropertySetException;
import org.informagen.propertyset.exceptions.InvalidPropertyTypeException;
import org.informagen.propertyset.exceptions.PropertyImplementationException;

import java.beans.*;

// Java - Collections
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 * PropertySet wrapper for a POJO, Plain Old Java Object
 *
 * Dynamically looks up all POJO properties (those exposed by is|get/setXXX) and invokes
 * them on the getXXX/setXXX propertyset methods.
 * <p>
 *
 * <b>Required Args</b>
 * <ul>
 *  <li><b>bean</b> - the POJO to be introspected</li>
 * </ul>
 */

public class BeanPropertySet extends AbstractPropertySet {

    private Map<String,PropertyDescriptor> descriptors = new HashMap<String,PropertyDescriptor>();
    private final Object bean;

    private BeanPropertySet() {
        bean = null;
    }

    public BeanPropertySet(Object bean) throws PropertyImplementationException {
        this.bean = bean;
        setPojo(bean);        
    }
       
    public BeanPropertySet(Map<String,String> config, Map<String,Object>args) {
 
        bean = (args != null) ? args.get("bean") : null;
        setPojo(bean);
    }
        
    // M E T H O D S  -------------------------------------------------------------------------

    protected void setPojo(Object bean) throws PropertyImplementationException {
        
        if(bean == null)
            throw new PropertyImplementationException("BeanPropertySet received a null object for the bean");

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());

            for(PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors())
                descriptors.put(propertyDescriptor.getName(), propertyDescriptor);
            
        } catch (IntrospectionException introspectionException) {
            throw new PropertyImplementationException("The 'bean' object is not a bean", introspectionException);
        }
    }

    public Collection<String> getKeys(String prefix, Type type) throws PropertySetException {
        
        Collection<String> keys = new HashSet<String>();

        for(String name : descriptors.keySet()) 
                keys.add(name);

        return keys;
    }

    public boolean isSettable(String key) {
        PropertyDescriptor descriptor = descriptors.get(key);
        return (descriptor != null) && (descriptor.getWriteMethod() != null);
    }

    public Type getType(String key) throws PropertySetException {

        PropertyDescriptor descriptor = descriptors.get(key);

        if (descriptor == null)
            throw new PropertySetException("Key '" + key + "' not found");

        Class clazz = descriptor.getPropertyType();

        if ((clazz == Integer.TYPE) || (clazz == Integer.class))
            return Type.INT;

        if ((clazz == Long.TYPE) || (clazz == Long.class)) 
            return Type.LONG;

        if ((clazz == Double.TYPE) || (clazz == Double.class)) 
            return Type.DOUBLE;

        if (clazz == String.class) 
            return Type.STRING;

        if ((clazz == Boolean.TYPE) || (clazz == Boolean.class))
            return Type.BOOLEAN;

        if (clazz == byte[].class) 
            return Type.DATA;

        if (java.util.Date.class.isAssignableFrom(clazz)) 
            return Type.DATE;

        if (java.util.Properties.class.isAssignableFrom(clazz)) 
            return Type.PROPERTIES;

        return Type.OBJECT;
    }

    public boolean exists(String key) throws PropertySetException {
        return descriptors.containsKey(key);
    }

    public void remove() throws PropertySetException {
        throw new PropertyImplementationException("Removing all properties not supported in BeanPropertySet");
    }

    public void remove(String key) throws PropertySetException {
        throw new PropertyImplementationException("Remove not supported in BeanPropertySet, use setXXX(null) instead");
    }

    protected void setImpl(Type type, String key, Object value) throws PropertySetException {
        
        if (getType(key) != type) 
            throw new InvalidPropertyTypeException(key + " is not of type " + type);

        PropertyDescriptor descriptor = descriptors.get(key);

        try {
            Object result = descriptor.getWriteMethod().invoke(bean, new Object[] {value});
        } catch (NullPointerException nullPointerException) {
            throw new PropertyImplementationException("Property " + key + " is read-only");
        } catch (Exception exception) {
            throw new PropertyImplementationException("Cannot invoke write method for key " + key, exception);
        }
    }

    protected Object get(Type type, String key) throws PropertySetException {
        
        if (getType(key) != type) 
            throw new InvalidPropertyTypeException(key + " is not of type " + type);

        PropertyDescriptor descriptor = (PropertyDescriptor) descriptors.get(key);

        try {
            return descriptor.getReadMethod().invoke(bean, new Object[0]);
        } catch (NullPointerException ex) {
            throw new PropertyImplementationException("Property " + key + " is write-only");
        } catch (Exception ex) {
            throw new PropertyImplementationException("Cannot invoke read method for key " + key, ex);
        }
    }
}
