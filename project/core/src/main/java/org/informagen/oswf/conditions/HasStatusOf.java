package org.informagen.oswf.conditions;

import org.informagen.oswf.Condition;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;

// OS PropertySet
import org.informagen.oswf.propertyset.PropertySet;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowStoreException;

// Java Util
import java.util.List;
import java.util.Map;


/**
 * Built-in condition that returns true if the current step's status is
 * the same as the required argument "status". 
 *
 * Looks at ALL current steps unless a stepId is given in the optional argument "stepId".
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */


public class HasStatusOf implements Condition {


    // M E T H O D S  -------------------------------------------------------------------------

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, PropertySet ps) throws WorkflowStoreException {

        String status = (args.get("status") != null) ? args.get("status") : "";
        Integer stepId = null;
        String stepIdValue = args.get("stepId");
        
        // Has 'stepId' been supplied and is it an integer?
        if (stepIdValue != null) {
            try {
                stepId = new Integer(stepIdValue);
            } catch (NumberFormatException numberFormatException) {
                stepId = null;
            }
        }

        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");
        WorkflowStore store = (WorkflowStore) transientVars.get("store");
        List<Step> currentSteps = store.findCurrentSteps(pi.getProcessInstanceId());

        // Example the current steps; Otherwise only check the specified step
        
        if (stepId == null) {
            
            for ( Step step :  currentSteps ) 
                if (status.equals(step.getStatus())) 
                    return true;
            
        } else { 
            
            for (Step step : currentSteps) 
                if (stepId.equals(step.getStepId())) 
                    if (status.equals(step.getStatus()))
                        return true;
        }

        return false;
    }
}
