package org.informagen.oswf.testing

// OSWorkflow
import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.ProcessInstanceState
import org.informagen.oswf.Step

import org.informagen.oswf.descriptors.WorkflowDescriptor

// OSWf - Persistent Variables
import org.informagen.oswf.PersistentVars


// Java Collections
import java.util.Collection
import java.util.Map
import java.util.List
import static java.util.Collections.EMPTY_MAP

// JUnit 4.x testing
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

// Logging
import org.slf4j.Logger


/**
 * @author gilbert Date: Feb 12, 2012
 */

class OSWfAssertions  {

    // M E T H O D S  -------------------------------------------------------------------------

    static void assertRoute(OSWfEngine wfEngine, long piid, int[] actions) {

        try {
            actions.each { actionId ->
                wfEngine.doAction(piid, actionId, EMPTY_MAP)
            }
        } catch(Exception exception) {
            fail(exception.getMessage())
        }

    }

    static void assertCounts(OSWfEngine wfEngine, long piid, Map<String,Integer> counts) {
        assertCounts(wfEngine, piid, counts['historySteps'], counts['currentSteps'], counts['actions'])
    }

    /**
     *  Assert the current states of a process instance.
     *  history step count
     *  current step count
     *  action counts (Not tested for authorization)
     *
     */
    
    static void assertCounts(OSWfEngine wfEngine, long piid, int historyStepCount, int currentStepCount, int actionCount) {
        
        def historySteps = wfEngine.getHistorySteps(piid)
        assert historyStepCount == historySteps.size()
    
        def currentSteps = wfEngine.getCurrentSteps(piid)
        assert currentStepCount == currentSteps.size()

        List<Integer> availableActions = wfEngine.getAvailableActions(piid)
        assert actionCount == availableActions.size()
    }

    /**
     *  Assert the current status of a process instance.
     */

    static void assertProcessInstanceState(OSWfEngine wfEngine, long piid, ProcessInstanceState entryState) {
        assert entryState == wfEngine.getProcessInstanceState(piid)
    }

    /**
     *  Assert the current status of a step.
     */

    static void assertStepState(OSWfEngine wfEngine, Step step, String status) {
        assert status == step.getStatus()
    }

    /**
     *  Assert the current value of a variable as a string. 
     *  This method needs to be generalized to an object
     */

    static void assertProperty(OSWfEngine wfEngine, long piid, String variable, String result) {
        PersistentVars persistentVars = wfEngine.getPersistentVars(piid)
        assert result == persistentVars.getString(variable)
    }


}
