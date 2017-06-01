package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;

import java.util.Map;


/**
 * Sets the arguments "name" and "value" into the TypedMap.
 *
 *
 * Usage:
 *
 *
 */


public class SetStringProperty implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) {

        // Expects a name/value pair as argument inputs
        String name = args.get("name");
        
        if(name == null)
            return;
        
        String value = args.get("value");
        String theDefault = args.get("default");
        
        if(value == null)
            value = theDefault;
        
        if(value != null)        
            ps.setString(name, value);
    }
}
