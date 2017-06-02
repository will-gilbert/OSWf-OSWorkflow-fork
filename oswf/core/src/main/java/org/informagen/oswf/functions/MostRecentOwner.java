package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;

import java.util.*;


/**
 * Sets the transient variable "mostRecentOwner" to the owner of the most
 * recent step that had an id equal to one of the values in the stepId list. If there is
 * none found, the variable is unset. This function accepts the following
 * arguments:
 *
 * <ul>
 *  <li>stepId - a comma-seperated list of the most recent steps to look for (required)</li>
 * </ul>
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @author <a href="mailto:mischwar@cisco.com">Mike Schwartz</a>
 * @version $Revision: 1.3 $
 */
public class MostRecentOwner implements FunctionProvider {

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {

        // Expects a stepId name/value pair
        String stepIdString = args.get("stepId");
        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");

        if (stepIdString == null) 
            throw new WorkflowException("This function expects a stepId!");

        StringTokenizer st = new StringTokenizer(stepIdString, ",");
        List<String> stepIds = new LinkedList<String>();

        while (st.hasMoreTokens()) {
            stepIds.add(st.nextToken().trim());
        }

        WorkflowStore store = (WorkflowStore) transientVars.get("store");
        List<Step> historySteps = store.findHistorySteps(pi.getProcessInstanceId());

        for (Step step : historySteps) {

            if (stepIds.contains(String.valueOf(step.getStepId())) && stringSet(step.getOwner())) {
                transientVars.put("mostRecentOwner", step.getOwner());

                break;
            }
        }
    }
    
    private boolean stringSet(String string) {
        return (string != null) && !"".equals(string);
    }
    
}
