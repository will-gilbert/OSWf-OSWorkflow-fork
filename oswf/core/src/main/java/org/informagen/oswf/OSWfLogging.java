package org.informagen.oswf;

// OSWorkflow
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.Step;

import org.informagen.oswf.descriptors.WorkflowDescriptor;


// Java Collections
import java.util.Collection;
import java.util.List;
import static java.util.Collections.EMPTY_MAP;

// JUnit 4.x testing
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

// Logging
import org.slf4j.Logger;


/**
 * @author gilbert Date: Feb 12, 2012
 */

public class OSWfLogging  {

    // M E T H O D S  -------------------------------------------------------------------------

 
    /** 
     *  Write out the state the all the current steps. 
     */

    public static void logCurrentSteps(Logger logger, long piid, OSWfEngine wfEngine) {
                
        List<Step> steps = wfEngine.getCurrentSteps(piid);
        WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
        
        if(steps.size() > 0)
            for (Step step : steps) 
                logger.debug(String.format("Current Step: %3d %-20s %s '%s'",
                    step.getStepId(),
                    wfDescriptor.getStep(step.getStepId()).getName(),
                    step.getStatus(),
                    step.getOwner()
                    //dateFormatter.format(step.getStartDate())
                ));
        else
            logger.debug("No available current steps");
            
    }

    /** 
     *  Write out the avaiblable action for a process instance. 
     */

    public static void logAvailableActions(Logger logger, long piid, OSWfEngine wfEngine) {
    
        List<Integer> actions = wfEngine.getAvailableActions(piid, EMPTY_MAP);
        WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
    
        // for (int i = 0; i < actions.size(); i++) {
        for (Integer actionId : actions) {
            String name = wfDescriptor.getAction(actionId).getName();
            logger.debug(String.format("Available Action: %3d %s", actionId, name));
        }
    }

    /** 
     *  Write out the state the all the history steps. 
     */

    public static void logHistorySteps(Logger logger, long piid, OSWfEngine wfEngine) {
            
        List<Step> steps = wfEngine.getHistorySteps(piid);
        WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));

        if(steps.size() > 0) 
            for (Step step : steps) 
                logger.debug(String.format("History Step: %3d %-20s %s  ",   // %3s-%3s
                    step.getStepId(),
                    wfDescriptor.getStep(step.getStepId()).getName(),
                    step.getStatus()
                    // dateFormatter.format(step.getStartDate()),
                    // dateFormatter.format(step.getFinishDate())
                ));
       else
            logger.debug("No available history steps");
    }


}
