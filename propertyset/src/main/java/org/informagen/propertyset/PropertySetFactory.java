package org.informagen.propertyset;


import org.informagen.propertyset.exceptions.PropertySetException;

// Potential Exceptions when using Reflection
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

// Java - Collections
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

// Java - Reflection
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * The PropertySetFactory is a factory for all types of PropertySets registered.
 *
 */

public class PropertySetFactory {

    private static PropertySetFactory instance = null;

    // The configuration parameters (<parameter name ='xxx' value='yyyy'/>) from the configuation file
    private Map<String,Map<String,String>> parameters = new HashMap<String,Map<String,String>>();

    // A list of PropertySet names with the full qualified class name
    private Map<String,String> classNames = new HashMap<String,String>();
    
    // M E T H O D S  -------------------------------------------------------------------------

    private PropertySetFactory() {}

    public static PropertySetFactory getInstance() {
        
        if(instance == null) 
            instance = new PropertySetFactory()
                .addNamedPropertySet("aggregate", "org.informagen.propertyset.AggregatePropertySet")
                .addNamedPropertySet("proxy", "org.informagen.propertyset.ProxyPropertySet")
                .addNamedPropertySet("bean", "org.informagen.propertyset.BeanPropertySet")
                .addNamedPropertySet("map", "org.informagen.propertyset.MapPropertySet")
                .addNamedPropertySet("memory", "org.informagen.propertyset.MemoryPropertySet")
                .addNamedPropertySet("serializable", "org.informagen.propertyset.SerializablePropertySet")
                .addNamedPropertySet("xml", "org.informagen.propertyset.XMLPropertySet")
            ;            
                   
        return instance;
    }

    public PropertySetFactory addNamedPropertySet(String propertySetName, String classname) {
        return addNamedPropertySet(propertySetName, classname, Collections.EMPTY_MAP);
    }

    public PropertySetFactory addNamedPropertySet(String propertySetName, String classname, Map<String,String> parameters) {
        this.classNames.put(propertySetName, classname);
        this.parameters.put(propertySetName,  parameters);
        return this;
    }

    public Map<String,String> getParameters(String propertySetName) {
        return parameters.get(propertySetName);
    }

    public String getClassName(String propertySetName) {
        return classNames.get(propertySetName);
    }

    public PropertySet createPropertySet(String propertySetName) throws PropertySetException {
        return createPropertySet(propertySetName, Collections.EMPTY_MAP);
    }

    /**
     * Get a propertyset by name
     *
     * @param propertySetName The name of the propertyset as registered in the configuration file
     *
     * @param args The arguments to pass to the propertyset for initialization.
     *     Consult the javadocs for a particular propertyset to see what arguments
     *     it requires and supports.
     */

    public PropertySet createPropertySet(String propertySetName, Map<String,Object> args) throws PropertySetException {
        
        PropertySet ps = createPropertySet(propertySetName, args, PropertySetFactory.class.getClassLoader());

        if (ps == null) 
            ps = createPropertySet(propertySetName, args, Thread.currentThread().getContextClassLoader());

        return ps;
    }

    /**
     */

     @SuppressWarnings("unchecked")
     public PropertySet createPropertySet(String propertySetName, Map<String,Object> args, ClassLoader classLoader) throws PropertySetException {

        String classname = getClassName(propertySetName);
        
        if(classname == null)
            throw new PropertySetException("PropertySet name '" + propertySetName + " not defined");
        
        Map<String,String> config = null;
        Object object = getParameters(propertySetName);
         
        if(object instanceof Map<?,?>)
             config = (Map<String,String>)object;

        // Attempt to get the PropertySet class definition from the ClassLoader
        Class propertySetClass = null;
        try {
            propertySetClass = classLoader.loadClass(classname);
        } catch (ClassNotFoundException ex) {
            throw new PropertySetException("PropertySet classname '" + propertySetClass + " not found");
        }

        // Use Java Reflection to instance a class two-arg constructor(config, args)
        PropertySet propertySet = null;

        try {
            Constructor<PropertySet> constructor;
            try {
                constructor = propertySetClass.getConstructor(new Class[]{Map.class, Map.class});
                propertySet = constructor.newInstance(config, args);
            } catch (NoSuchMethodException exception) {
                constructor = propertySetClass.getConstructor(new Class[]{});
                propertySet = constructor.newInstance();
            }
            
            
        } catch (Exception exception) {
            throw new PropertySetException(exception.getMessage());
        }

        return propertySet;
    }
}
