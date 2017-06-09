package org.informagen.oswf.testing;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;


import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;
import org.informagen.oswf.descriptors.StepConditionDescriptor;

import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.testing.OSWfAssertions;

import org.informagen.typedmap.TypedMap;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Java - Collections
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.text.SimpleDateFormat;


// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;



/**
*/

public abstract class OSWfTestCase  {

    protected static Marker fatal = MarkerFactory.getMarker("FATAL"); 
    protected static SimpleDateFormat dateFormatter = new SimpleDateFormat("S");

    // Class variable
    // protected static OSWfEngine wfEngine;
    // protected static String workflowName;
    
    protected Logger logger;

    protected OSWfTestCase() {}
    
    protected OSWfTestCase(Logger logger) {
        setLogger(logger);
    }

    protected void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    // Workflow Methods =======================================================================

    protected long createProcessInstance(OSWfEngine wfEngine, String workflowFilename, int initialAction) {
        return createProcessInstance(wfEngine, workflowFilename, initialAction, Collections.EMPTY_MAP);
    }

    protected long createProcessInstance(OSWfEngine wfEngine, String workflowFilename, int initialAction, 
                                                Map<String,Object> instanceVariables){
        
        long processInstanceId = 0L;

        try {
            assertTrue("Can't initialize this workflow", wfEngine.canInitialize(workflowFilename, initialAction));
            processInstanceId = wfEngine.initialize(workflowFilename, initialAction, instanceVariables);
        } catch (InvalidInputException invalidInputException) {
            fail("createProcessInstance InvalidInputException: " + invalidInputException.getMessage());
        } catch (WorkflowException workflowException) {
            fail("OSWorkflowTestCase.createProcessInstance WorkflowException: " + workflowException.getMessage());
        }
        
        return processInstanceId;
    }

    protected void chooseAction(OSWfEngine wfEngine,long piid, int actionId) throws Exception {
        chooseAction(wfEngine, piid, actionId, Collections.EMPTY_MAP);
    }
    
    protected void chooseAction(OSWfEngine wfEngine,long piid, int actionId, Map params) throws Exception {
    
        logInstanceState(wfEngine, piid);
        
        WorkflowDescriptor wfDescriptor = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));

        String actionName = wfDescriptor.getAction(actionId).getName();
        logger.debug(String.format("Choose Action: %3d  %s", actionId, actionName));
        
        wfEngine.doAction(piid, actionId, params);
    }


    // Protected Assert Methods ==============================================================

    protected void assertRoute(OSWfEngine wfEngine,long piid, int[] actions) throws Exception {
        OSWfAssertions.assertRoute(wfEngine, piid, actions);
    }


    protected void assertCounts(OSWfEngine wfEngine, long piid, int historyStepCount, int currentStepCount, int actionCount) {
        OSWfAssertions.assertCounts(wfEngine, piid, historyStepCount, currentStepCount, actionCount);       
    }


    protected void assertProcessInstanceState(OSWfEngine wfEngine, long piid, ProcessInstanceState entryState) {
        OSWfAssertions.assertProcessInstanceState(wfEngine, piid, entryState);
        // assertEquals("Entry State", entryState, wfEngine.getProcessInstanceState(piid));
    }


    protected void assertStepState(Step step, String status) {
        assertEquals("Step Status", status, step.getStatus());
    }


    protected void assertProperty(OSWfEngine wfEngine, long piid, String property, String result) {
        OSWfAssertions.assertProperty(wfEngine, piid, property, result);
        // TypedMap persistentVars = wfEngine.getTypedMap(piid);
        // assertEquals(result, persistentVars.getString(property));
    }

    // Logging ================================================================================

    private boolean cannotLog() {
    
        if(logger == null)
            return true;
    
        if(logger.isDebugEnabled() == false)
            return true;
            
        return false;
    }

    protected void logInstanceState(OSWfEngine wfEngine, long piid)     { 
    
        if(cannotLog())
            return;
            
        logHistorySteps(wfEngine, piid); 
        logCurrentSteps(wfEngine, piid); 
        logAvailableActions(wfEngine, piid); 
        logger.debug("========================================");
    }

    protected void logCurrentSteps(OSWfEngine wfEngine,long piid) {
    
        if(cannotLog()) 
            return;

        OSWfAssertions.logCurrentSteps(logger, piid, wfEngine);
            
    }

    protected void logMessage(String message) {

        if(cannotLog())
            return;

        logger.debug(message);
    }

    protected void logAvailableActions(OSWfEngine wfEngine, long piid) {

        if(cannotLog())
            return;

        OSWfAssertions.logAvailableActions(logger, piid, wfEngine);
    }

    

    protected void logHistorySteps(OSWfEngine wfEngine, long piid) {
    
        if(cannotLog())
            return;
        
        OSWfAssertions.logHistorySteps(logger, piid, wfEngine);
    }

    protected String getProcessInstanceState(OSWfEngine wfEngine, long piid) {
       return wfEngine.getProcessInstanceState(piid).getName();
    }

    protected void logStepConditions(OSWfEngine wfEngine, long piid) {
        
        // Get passed stepConditions for this step
        Map<Integer,Set<StepCondition>> stepConditions = wfEngine.getStepConditions(piid);
        assertNotNull(stepConditions);

        for(Set<StepCondition> stepConditionSet : stepConditions.values())
            for(StepCondition stepCondition : stepConditionSet)
                logger.debug("Step: '" + stepCondition.getStepName() +"'("+ stepCondition.getStepId() + ") StepCondition: '" + stepCondition.getName() + "' ==> " + stepCondition.getValue());
    }

    protected boolean hasStepCondition(OSWfEngine wfEngine, long piid, String stepConditionName) {

        // Get step-conditions for the current step(s) which are true

        Map<Integer,Set<StepCondition>> stepConditions = wfEngine.getStepConditions(piid);
        assertNotNull(stepConditions);

        for(Set<StepCondition> stepConditionSet : stepConditions.values())
            for(StepCondition stepCondition : stepConditionSet)
                if( stepCondition.getName().equals(stepConditionName))
                    return stepCondition.getValue();

        return false;


/*
         // Access the workflow process description
         WorkflowDescriptor wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));

         // Get the all of the current steps for this process instance 
         List<Step> steps = wfEngine.getCurrentSteps(piid);

         for (Step step : steps) { 

             // Get a list of all step-conditions for this current step
             StepDescriptor sd = wd.getStep(step.getStepId()); 
             List<StepConditionDescriptor> stepStepConditions = sd.getStepConditions();

             for (StepConditionDescriptor pd : stepStepConditions) {
                 if(stepCondition.equals(pd.getName()))
                     return externalStepConditions.contains(pd.getName()) ? Boolean.TRUE : Boolean.FALSE;
             }
         }

         return null;
*/

     }

}
