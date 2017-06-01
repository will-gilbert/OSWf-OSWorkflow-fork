package org.informagen.oswf.conditions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.Condition;
import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * A simple utility condition that returns true if the the current user or actor
 *   is the step owner.
 * </p>
 *
 * <p>
 * This condition may be used to deny the owner of the step by negating the
 * condition in the workflow descriptor with <code>negate='true'</code>.
 * <p>
 *
 * <p>
 * Looks at ALL current steps unless a step id is given in the optional argument
 * "stepId".
 * </p>
 *
 * <p>
 * The implementation was originally contained in AllowOwnerOfStepCondition by
 * Pat Lightbody.
 * </p>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody </a> (original implementation)
 * @author <a href="mailto:adam@southtech.co.uk">Adam Southall </a> (refactored owner conditions to use this generic class.
 */
public class IsStepOwner implements Condition {
    
    // M E T H O D S  -------------------------------------------------------------------------

    // ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowStoreException {

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

        String actor = (String) transientVars.get("actor");
        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");
        WorkflowStore store = (WorkflowStore) transientVars.get("store");
        List<Step> currentSteps = store.findCurrentSteps(pi.getProcessInstanceId());

        if (stepId == null) {
            for (Step step : currentSteps) {
                
                if ((step.getOwner() != null) && actor.equals(step.getOwner())) 
                    return true;
            }
        } else {
            for (Step step : currentSteps) {
                if (stepId.equals(step.getStepId()))
                    if ((step.getOwner() != null) && actor.equals(step.getOwner()))
                        return true;
            }
        }

        return false;
    }
}
