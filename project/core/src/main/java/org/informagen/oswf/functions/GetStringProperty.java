package org.informagen.oswf.functions;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;

import java.util.Map;


/**
 * Sets the transient variable "${variable)" to the value of a PropertySet...
 *
 * Usage:
 *
 *
 */


public class GetStringProperty implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, PropertySet ps) {

        // Expects a name/variable pair as argument inputs
        String name = args.get("name");
        String variable = args.get("variable");
        
        if(name == null || variable == null)
            return;
            
        String value = ps.getString(name);
        transientVars.put(variable, value);

    }
}
