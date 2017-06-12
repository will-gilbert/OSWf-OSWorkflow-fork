package org.informagen.oswf.functions;

// OSWorkflow - Main
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.exceptions.WorkflowException;

// OSWorkflow - Other
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.ProcessInstance;

// OSWf - Actor Workflow
import org.informagen.oswf.impl.DefaultOSWfEngine;

// Java - Util
import java.util.Map;


public class FireTrigger implements FunctionProvider {
    
    public void execute(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException {

        ProcessInstance pi = (ProcessInstance)transientVars.get("pi");
        String actor = (String)transientVars.get("actor");

        Long piid = pi.getProcessInstanceId();
        int triggerId = Integer.parseInt((String)args.get("triggerId"));
        
        OSWfEngine wfEngine = new DefaultOSWfEngine(actor);
        wfEngine.setConfiguration((OSWfConfiguration) transientVars.get("configuration"));
                
        wfEngine.executeTriggerFunction(piid, triggerId);
            
    }

}

