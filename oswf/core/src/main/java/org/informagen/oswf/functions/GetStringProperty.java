package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;

import java.util.Map;


/**
 * Get a persistent variable with key of arguemnt 'name' and save as a transient variable with key 'variable'
 *
 * Usage:
 *
 *
 */


public class GetStringProperty implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) {

        // Expects a name/variable pair as argument inputs
        String name = args.get("name");
        String variable = args.get("variable");
        
        if(name == null || variable == null)
            return;
            
        String value = persistentVars.getString(name);
        transientVars.put(variable, value);

    }
}
