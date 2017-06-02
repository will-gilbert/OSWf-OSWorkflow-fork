package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.impl.DefaultWorkflowContext;

import java.util.Map;


/**
 * Changed the Process Instance "actor" to a defined value.  Useful for 'auto'
 *   steps so the invoking user doesn't get 'credit' or 'blame' for the action
 */


public class SetActor implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) {

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        String actor = args.get("name");
        
        if(context == null || actor == null)
            return;

        if( context instanceof DefaultWorkflowContext ) 
            ((DefaultWorkflowContext)context).setActor(actor);

    }
}
