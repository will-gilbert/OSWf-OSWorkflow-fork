package org.informagen.oswf.functions;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;

import org.informagen.oswf.impl.DefaultStep;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;

import java.util.*;


/**
 *
 * <ul>
 *  <li>stepId - a comma-seperated list of the Current Steps to be moved to History Steps </li>
 *  <li>status - String; Optional status to assign to the step's exit-status </li>
 * </ul>
 *
 */
public class ClearStep implements FunctionProvider {

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, PropertySet ps) throws WorkflowException {

        // Expects a 'stepId' name/value pair
        String stepIdString = args.get("stepId");
        String status = args.get("status");
        
        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");

        if (stepIdString == null) 
            throw new WorkflowException("This function expects a stepId!");

        StringTokenizer st = new StringTokenizer(stepIdString, ",");
        List<String> stepIds = new LinkedList();

        while (st.hasMoreTokens()) 
            stepIds.add(st.nextToken().trim());

        WorkflowStore store = (WorkflowStore) transientVars.get("store");

        List<Step> currentSteps = store.findCurrentSteps(pi.getProcessInstanceId());
        List<Step> clearedSteps = new ArrayList<Step>();

        // Collect the current steps which need to be cleared
        for (Step step : currentSteps) 
            if (stepIds.contains(String.valueOf(step.getStepId()))) 
                clearedSteps.add(step);

        // Optionally change the step status, then move them to History
        for (Step step : clearedSteps) {
            if(status != null) {
                store.moveToHistory(step, 0, null, status, null);
                // store.markFinished(step, 0, null, status, null);
                // store.moveToHistory(step);
            }
        }
 
    }
}
