package org.informagen.oswf.conditions;

import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.Condition;
//import org.informagen.oswf.exceptions.WorkflowException;

import java.util.Map;

//     Unit test: tests/PropertySetTest.java
// Workflow test: core/PropetySet.oswf.xml

public class HasPropertySetValue implements Condition {

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) {

        // Expects a propertyset name as an argument input
        // Optionally get a value for equality testing
        String name = args.get("name");
        String value = args.get("value");
                
        // Required argument
        if(name == null) 
            throw new IllegalArgumentException("'name' argument expected in 'HasPropertySetValue' Condition");
        
        // We're only interested if a value has been defined for this property, not it's actual value
        if(value == null)
            return persistentVars.exists(name);

        // Here we are testing for string equality; 'args' can only be specified as strings
        if(value != null) {
           
            Object object = persistentVars.getAsActualType(name);
            
            if(object == null)
                return false;

            return value.equals(object.toString());
        } else
            return false;

        
    }
}
