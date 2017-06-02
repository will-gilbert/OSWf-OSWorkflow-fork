package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;

import java.util.Map;
import java.util.Date;

public class SetDateProperty implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) {

        String name = args.get("name");
        
        if(name == null)
            return;
        
        persistentVars.setDate(name, new Date());
    }
}
