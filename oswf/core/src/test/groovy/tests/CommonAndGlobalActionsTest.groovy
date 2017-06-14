package tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.Step
import org.informagen.oswf.ProcessInstance
import org.informagen.oswf.ProcessInstanceState

// OSWf - SPI
import org.informagen.oswf.impl.DefaultOSWfEngine

// OSWf Exception
import org.informagen.oswf.exceptions.InvalidActionException


import java.net.URL

import java.util.Arrays
import java.util.List
import java.util.ArrayList

// JUnit 4.x testing
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.fail

class CommonAndGlobalActionsTest  {


    private static final int START_WORKFLOW   = 50   // Initital action
    private static final int RESTART_WORKFLOW = 100  // Common action
    private static final int CONDITIONAL_RESTART    = 101  // Common action
    private static final int GO_TO_STEP_2     = 1    // Step 1 action, 'Go To Step 2'

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine

    @Before
    void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine()
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    void testBasicCommonAction() throws Exception {
        
        def url = getClass().getResource('/core/common-actions.oswf.xml')
        def piid = wfEngine.initialize(url.toString(), START_WORKFLOW)
        
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)

        // Verify that a common action (Restart Workflow) can be called
        wfEngine.doAction(piid, RESTART_WORKFLOW)

        def historySteps = wfEngine.getHistorySteps(piid)
        assert 'Restarted' == historySteps.first().getStatus()

        // Now let's move to step 2
        wfEngine.doAction(piid, GO_TO_STEP_2)

        // Now let's check if we can call a common action not listed in the step
        try {
            wfEngine.doAction(piid, RESTART_WORKFLOW)
            fail('Should not be able to call a common-action which is not listed')
        } catch (InvalidActionException invalidActionException) {
            assert invalidActionException.message.contains('Action 100 was not found')
        }

        // Restart the workflow, conditionally

        wfEngine.doAction(piid, CONDITIONAL_RESTART)
        historySteps = wfEngine.getHistorySteps(piid)
        assert 'Restarted' == historySteps.first().getStatus()
    }

    @Test
    void testBasicGlobalAction() throws Exception {
        
        def url = getClass().getResource('/core/global-actions.oswf.xml')
        def piid = wfEngine.initialize(url.toString(), START_WORKFLOW)

        // Step 1 has two common actions and one step action
        def availableActions = wfEngine.getAvailableActions(piid)
        assert [RESTART_WORKFLOW, CONDITIONAL_RESTART, GO_TO_STEP_2] == availableActions

        // Verify that a global action can be called
        wfEngine.doAction(piid, RESTART_WORKFLOW)

        def historySteps = wfEngine.getHistorySteps(piid)
        assert 'Restarted' == historySteps.first().getStatus()

        // Now let's move to step 2
        wfEngine.doAction(piid, GO_TO_STEP_2)

        // Now test -1 stepId stuff
        wfEngine.doAction(piid, CONDITIONAL_RESTART)
        historySteps = wfEngine.getHistorySteps(piid)
        assert 'Restarted' == historySteps.first().getStatus()
    }

}
