package org.informagen.oswf.typedmap;


import org.informagen.oswf.typedmap.exceptions.TypedMapException;

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
 * The TypedMapFactory is a factory for all types of TypedMaps registered.
 *
 */

public class TypedMapFactory {

    private static TypedMapFactory instance = null;

    // The configuration parameters (<parameter name ='xxx' value='yyyy'/>) from the configuation file
    private Map<String,Map<String,String>> parameters = new HashMap<String,Map<String,String>>();

    // A list of TypedMap names with the full qualified class name
    private Map<String,String> classNames = new HashMap<String,String>();
    
    // M E T H O D S  -------------------------------------------------------------------------

    private TypedMapFactory() {}

    public static TypedMapFactory getInstance() {
        
        if(instance == null) 
            instance = new TypedMapFactory()
                // .addNamedTypedMap("aggregate", "org.informagen.oswf.typedmap.AggregateTypedMap")
                // .addNamedTypedMap("proxy", "org.informagen.oswf.typedmap.ProxyTypedMap")
                // .addNamedTypedMap("bean", "org.informagen.oswf.typedmap.BeanTypedMap")
                // .addNamedTypedMap("map", "org.informagen.oswf.typedmap.MapTypedMap")
                .addNamedTypedMap("memory", "org.informagen.oswf.typedmap.MemoryTypedMap")
                // .addNamedTypedMap("serializable", "org.informagen.oswf.typedmap.SerializableTypedMap")
                // .addNamedTypedMap("xml", "org.informagen.oswf.typedmap.XMLTypedMap")
            ;            
                   
        return instance;
    }

    public TypedMapFactory addNamedTypedMap(String propertySetName, String classname) {
        return addNamedTypedMap(propertySetName, classname, Collections.EMPTY_MAP);
    }

    public TypedMapFactory addNamedTypedMap(String propertySetName, String classname, Map<String,String> parameters) {
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

    public TypedMap createTypedMap(String propertySetName) throws TypedMapException {
        return createTypedMap(propertySetName, Collections.EMPTY_MAP);
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

    public TypedMap createTypedMap(String propertySetName, Map<String,Object> args) throws TypedMapException {
        
        TypedMap ps = createTypedMap(propertySetName, args, TypedMapFactory.class.getClassLoader());

        if (ps == null) 
            ps = createTypedMap(propertySetName, args, Thread.currentThread().getContextClassLoader());

        return ps;
    }

    /**
     */

     @SuppressWarnings("unchecked")
     public TypedMap createTypedMap(String propertySetName, Map<String,Object> args, ClassLoader classLoader) throws TypedMapException {

        String classname = getClassName(propertySetName);
        
        if(classname == null)
            throw new TypedMapException("TypedMap name '" + propertySetName + " not defined");
        
        Map<String,String> config = null;
        Object object = getParameters(propertySetName);
         
        if(object instanceof Map<?,?>)
             config = (Map<String,String>)object;

        // Attempt to get the TypedMap class definition from the ClassLoader
        Class propertySetClass = null;
        try {
            propertySetClass = classLoader.loadClass(classname);
        } catch (ClassNotFoundException ex) {
            throw new TypedMapException("TypedMap classname '" + propertySetClass + " not found");
        }

        // Use Java Reflection to instance a class two-arg constructor(config, args)
        TypedMap propertySet = null;

        try {
            Constructor<TypedMap> constructor;
            try {
                constructor = propertySetClass.getConstructor(new Class[]{Map.class, Map.class});
                propertySet = constructor.newInstance(config, args);
            } catch (NoSuchMethodException exception) {
                constructor = propertySetClass.getConstructor(new Class[]{});
                propertySet = constructor.newInstance();
            }
            
            
        } catch (Exception exception) {
            throw new TypedMapException(exception.getMessage());
        }

        return propertySet;
    }
}
