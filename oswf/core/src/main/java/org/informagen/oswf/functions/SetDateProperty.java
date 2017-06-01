package org.informagen.oswf.functions;

import org.informagen.oswf.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;

import java.util.Map;
import java.util.Date;

public class SetDateProperty implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) {

        String name = args.get("name");
        
        if(name == null)
            return;
        
        ps.setDate(name, new Date());
    }
}
