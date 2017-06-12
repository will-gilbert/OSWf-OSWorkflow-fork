package org.informagen.oswf;


import org.informagen.oswf.exceptions.PersistentVarsException;

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
 * The PersistentVarsFactory is a factory for all types of PersistentVars registered.
 *
 */

public class PersistentVarsFactory {

    private static PersistentVarsFactory instance = null;

    // The configuration parameters (<parameter name ='xxx' value='yyyy'/>) from the configuation file
    private Map<String,Map<String,String>> parameters = new HashMap<String,Map<String,String>>();

    // A list of PersistentVars names with the full qualified class name
    private Map<String,String> classNames = new HashMap<String,String>();
    
    // M E T H O D S  -------------------------------------------------------------------------

    private PersistentVarsFactory() {}

    public static PersistentVarsFactory getInstance() {
        
        if(instance == null) 
            instance = new PersistentVarsFactory()
                .addNamedPersistentVars("memory", "org.informagen.oswf.MemoryPersistentVars")
            ;            
                   
        return instance;
    }

    public PersistentVarsFactory addNamedPersistentVars(String propertySetName, String classname) {
        return addNamedPersistentVars(propertySetName, classname, Collections.EMPTY_MAP);
    }

    public PersistentVarsFactory addNamedPersistentVars(String propertySetName, String classname, Map<String,String> parameters) {
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

    public PersistentVars createPersistentVars(String propertySetName) throws PersistentVarsException {
        return createPersistentVars(propertySetName, Collections.EMPTY_MAP);
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

    public PersistentVars createPersistentVars(String propertySetName, Map<String,Object> args) throws PersistentVarsException {
        
        PersistentVars ps = createPersistentVars(propertySetName, args, PersistentVarsFactory.class.getClassLoader());

        if (ps == null) 
            ps = createPersistentVars(propertySetName, args, Thread.currentThread().getContextClassLoader());

        return ps;
    }

    /**
     */

     @SuppressWarnings("unchecked")
     public PersistentVars createPersistentVars(String propertySetName, Map<String,Object> args, ClassLoader classLoader) throws PersistentVarsException {

        String classname = getClassName(propertySetName);
        
        if(classname == null)
            throw new PersistentVarsException("PersistentVars name '" + propertySetName + " not defined");
        
        Map<String,String> config = null;
        Object object = getParameters(propertySetName);
         
        if(object instanceof Map<?,?>)
             config = (Map<String,String>)object;

        // Attempt to get the PersistentVars class definition from the ClassLoader
        Class propertySetClass = null;
        try {
            propertySetClass = classLoader.loadClass(classname);
        } catch (ClassNotFoundException ex) {
            throw new PersistentVarsException("PersistentVars classname '" + propertySetClass + " not found");
        }

        // Use Java Reflection to instance a class two-arg constructor(config, args)
        PersistentVars propertySet = null;

        try {
            Constructor<PersistentVars> constructor;
            try {
                constructor = propertySetClass.getConstructor(new Class[]{Map.class, Map.class});
                propertySet = constructor.newInstance(config, args);
            } catch (NoSuchMethodException exception) {
                constructor = propertySetClass.getConstructor(new Class[]{});
                propertySet = constructor.newInstance();
            }
            
            
        } catch (Exception exception) {
            throw new PersistentVarsException(exception.getMessage());
        }

        return propertySet;
    }
}
