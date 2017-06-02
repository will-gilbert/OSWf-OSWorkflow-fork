package org.informagen.oswf.functions;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.FunctionProvider;


import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.InvalidInputException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.descriptors.WorkflowDescriptor;


import org.informagen.typedmap.TypedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Timer;

// This is a example of an external event affecting a workflow process instance

public class TimerTask implements FunctionProvider {

    private static final Logger logger = LoggerFactory.getLogger(TimerTask.class);
    
    private static final String ACTOR = "Java TimerTask";

    // Create a workflow engine with 'Java TimerTask' as the 'actor'
    public static OSWfEngine wfEngine = new DefaultOSWfEngine(ACTOR); 
    public static Timer timer = new Timer();
    
    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {

        ProcessInstance processInstance = (ProcessInstance)transientVars.get("pi");

        long piid = processInstance.getProcessInstanceId();
        int action = Integer.parseInt((String)args.get("action"));
        int duration = Integer.parseInt((String)args.get("duration"));

        log(ACTOR, piid, action, duration);
                
        // Duration is in seconds
        timer.schedule(new ActionFiringTask( piid, action), (long)(duration*1000));
            
    }

    // Private inner class does the Action firing
    
    private class ActionFiringTask extends java.util.TimerTask {
    
        private long piid;
        private int action;

        public ActionFiringTask(long piid, int action) {
            this.piid = piid;
            this.action = action;
        }

        public void run() {
            try {
                wfEngine.doAction(piid, action, Collections.EMPTY_MAP);
            } catch(InvalidInputException invalidInputException) {
                logger.error(invalidInputException.getMessage());
            } catch(WorkflowException workflowException) {
                logger.error(workflowException.getMessage());
            }
        }
    }

    // Debugging helper function

    private void log(String actor, long piid, int action, int duration) {
    
        logger.debug( new StringBuffer()
            .append("Actor,'" + actor + "'")
            .append(", will fire action '")
            .append(getActionName(piid, action))
            .append("' on 'Light ")
            .append(piid)
            .append("' in ")
            .append(duration)
            .append(" seconds")
            .toString()
        );
    
    }

    // Move to a Utility class
    
    private String getActionName(long piid, int action) {
    
        String name = "";
    
        try {
            WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
            name = wfDescriptor.getAction(action).getName();
        } catch (Exception exception) {
            name = Integer.toString(action);
        }
        
        return name;
    }




}

