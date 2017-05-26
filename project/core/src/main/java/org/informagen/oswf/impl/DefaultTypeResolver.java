package org.informagen.oswf.impl;

// OSWf Core - Interfaces
import org.informagen.oswf.TypeResolver;
import org.informagen.oswf.Condition;
import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.Register;
import org.informagen.oswf.Validator;
import org.informagen.oswf.OSWfEngine;

// OSWf - Utilitiy
import org.informagen.oswf.util.ClassLoaderHelper;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class DefaultTypeResolver implements TypeResolver {
        
    private static final Logger logger = LoggerFactory.getLogger(TypeResolver.class);

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------
    
    // Type Lookup tables
    protected Map<String,String> conditions = new HashMap<String,String>();
    protected Map<String,String> functions = new HashMap<String,String>();
    protected Map<String,String> registers = new HashMap<String,String>();
    protected Map<String,String> validators = new HashMap<String,String>();

    // M E T H O D S  ------------------------------------------------------------------------- 

    public DefaultTypeResolver() {
        this(Collections.EMPTY_MAP, Collections.EMPTY_MAP);
    }

    public DefaultTypeResolver(Map<String,String> config, Map<String,Object> args) {

        // Builtin Condition aliases
        conditions.put("jndi",            "org.informagen.oswf.conditions.CanLookupJNDI");
        conditions.put("hasStatusOf",     "org.informagen.oswf.conditions.HasStatusOf");
        conditions.put("isStepOwner",     "org.informagen.oswf.conditions.IsStepOwner");
        conditions.put("hasRole",         "org.informagen.oswf.conditions.HasRole");
        conditions.put("canLookupJNDI",   "org.informagen.oswf.conditions.CanLookupJNDI");
        conditions.put("hasProperty",     "org.informagen.oswf.conditions.HasPropertySetValue");
        conditions.put("isPropertyEqual", "org.informagen.oswf.conditions.IsStringPropertyEqual");

        
        // Builtin FunctionProvider aliases
        functions.put("jndi",            "org.informagen.oswf.functions.JNDIFunctionProvider");
        functions.put("assertEquals",    "org.informagen.oswf.functions.AssertEquals");
        functions.put("setActor",        "org.informagen.oswf.functions.SetActor");
        functions.put("fireTrigger",     "org.informagen.oswf.functions.FireTrigger");
        functions.put("mostRecentOwner", "org.informagen.oswf.functions.MostRecentOwner");
        functions.put("timer",           "org.informagen.oswf.functions.TimerTask");
        functions.put("clearStep",       "org.informagen.oswf.functions.ClearStep");
        functions.put("setString",       "org.informagen.oswf.functions.SetStringProperty");
        functions.put("getString",       "org.informagen.oswf.functions.GetStringProperty");
        functions.put("now",             "org.informagen.oswf.functions.SetDateProperty");

        // Registers
        registers.put("jndi",      "org.informagen.oswf.registers.JNDIRegister");
        registers.put("beanshell", "org.informagen.oswf.registers.BeanShellRegister");
 
        // Built-in or registered registers
        registers.put("slf4jLogger",    "org.informagen.oswf.registers.SLF4jLogger");
        
        // Validators
        validators.put("jndi",      "org.informagen.oswf.validators.JNDIValidator");
        validators.put("beanshell", "org.informagen.oswf.validators.BeanShellValidator");

    }

    public void addConditionAlias(String name, String classname) {
        logger.debug("Adding '" + classname + "' as condition: " + name);
        conditions.put(name, classname);
    }

    public void addFunctionAlias(String name, String classname) {
        logger.debug("Adding '" + classname + "' as function: " + name);
        functions.put(name, classname);
    }

    public void addRegisterAlias(String name, String classname) {
        logger.debug("Adding '" + classname + "' as register: " + name);
        registers.put(name, classname);
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Condition getCondition(String type, String name, Map<String,String> args) throws WorkflowException {

        // We need to track down where in the Loader that missing attributes are set to ""
        //    and not 'null'; then we can remove this test
        if(name != null && name.trim().length() == 0)
            name = null;        
         String className = null;
        
        // 'alias' type uses the 'name' to lookup the classname in the functions Map         
        if("alias".equals(type))
            className = conditions.get(name);
        
        // 'class' type specifies the classname in the 'name' attribute
        else if("class".equals(type))
            className = name;
        
        else if("beanshell".equals(type))
            className = "org.informagen.oswf.conditions.BeanShell";

        // For added condiitons and a last attempt
        if(className == null)
            className = conditions.get(type);
        
        // For backwards support of 'class' type where the classname is passed as an arg
        if (className == null) 
            className = args.get("class.name");

        if (className == null) 
            className = args.get("classname");
        
        if (className == null)
            throw new WorkflowException("No type or 'classname' specified to TypeResolver");

        return (Condition) loadObject(className);
    }

    public FunctionProvider getFunction(String type, String name, Map<String,String> args) throws WorkflowException {

        // We need to track down where in the Loader that missing attributes are set to ""
        //    and not 'null'; then we can remove this test
        if(name != null && name.trim().length() == 0)
            name = null;        

        String className = null;
        
        // 'alias' type uses the 'name' to lookup the classname in the functions Map         
        if("alias".equals(type))
            className = functions.get(name);
        
        // 'class' type specifies the classname in the 'name' attribute
        else if("class".equals(type))
            className = name;
        
        else if("beanshell".equals(type))
            className = "org.informagen.oswf.functions.BeanShellFunctionProvider";
            
        // For added function types and a last attempt
        if(className == null)
            className = functions.get(type);
        
        // For backwards support of 'class' type where the classname is passed as an arg
        if (className == null) 
            className = args.get("class.name");

        if (className == null) 
            className = args.get("classname");
        
        // If we get here with no classname we are out of luck
        if (className == null)
            throw new WorkflowException("No type or 'classname' specified to TypeResolver");

        return (FunctionProvider)loadObject(className);
    }

    public Register getRegister(String type, Map<String,String> args) throws WorkflowException {

        String className = registers.get(type);

        if (className == null) 
            className = args.get("class.name");

        if (className == null) 
            className = args.get("classname");

        if (className == null)
            throw new WorkflowException("No type or 'classname' specified for Register '"+type+"' to TypeResolver");

        return (Register) loadObject(className);
    }

    public Validator getValidator(String type, Map<String,String> args) throws WorkflowException {

        String className = validators.get(type);

        if (className == null) 
            className = args.get("class.name");

        if (className == null) 
            className = args.get("classname");

        if (className == null) 
            throw new WorkflowException("No type or 'classname' specified to TypeResolver");

        return (Validator) loadObject(className);
    }
    
    // Classloader

    protected Object loadObject(String className) {
        try {
            return ClassLoaderHelper.loadClass(className.trim(), getClass()).newInstance();
        } catch (Exception e) {
            logger.error("Could not load class: '" + className + "'", e);
            return null;
        }
    }
}
