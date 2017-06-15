package groovy.usage;

import org.informagen.oswf.testing.OSWfTestCase


import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.ProcessInstanceState
import org.informagen.oswf.exceptions.WorkflowException

import org.informagen.oswf.impl.DefaultOSWfEngine

import org.informagen.oswf.OSWfConfiguration
import org.informagen.oswf.impl.MemoryOSWfConfiguration

import org.informagen.oswf.descriptors.WorkflowDescriptor

import org.informagen.oswf.exceptions.InvalidInputException

import org.informagen.oswf.impl.stores.MemoryWorkflowStore

import org.informagen.oswf.PersistentVars

import org.informagen.oswf.Step
import org.informagen.oswf.ProcessInstance


// Java Util
import java.util.Collection
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map

// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

import static java.util.Collections.EMPTY_MAP
import static org.informagen.oswf.testing.OSWfAssertions.assertCounts


/**
 *   assertCount(Long piid, int historySteps, int currentSteps, int actions);
 */


class LeaveRequestTest extends OSWfTestCase implements usage.Constants {

    // Process Instance identifier and engine
    protected long piid;
    private OSWfEngine wfEngine;
    private WorkflowDescriptor wd

    @Before
    void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine();
                
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));
                          
        wfEngine.setConfiguration(config);
    
        // Create a 'Leave Request' PI for 'Bob BobbleHead'
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstName", "Bob");
        inputVariables.put("lastName", "Bobblehead");
        
        piid = createProcessInstance(wfEngine, "LeaveRequest - Alt", INITIAL_ACTION, inputVariables)
        wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid))
    }

    @After
    void teardown() {
        MemoryWorkflowStore.reset();
    }


    @Test
    void initialState()  {
    
        assertCounts historySteps:0, currentSteps:1, actions:2
        
        def actions = wfEngine.getAvailableActions(piid);

        // Available actions in Step '100'
        assert 2 == actions.size()
        assert actions.find{it == REQUEST_HOLIDAYS }
        assert actions.find{it == REQUEST_CANCEL }

        assert 'Request day off' == wd.getAction(REQUEST_HOLIDAYS).name
        assert 'cancel' == wd.getAction(REQUEST_CANCEL).name

        // Current steps 
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assert 1 == currentSteps.size()
        
        def currentStep = currentSteps[0] as Step
        assertStepState(currentStep, 'Pending')    
        
        assert EMPLOYEE_REQUEST_STEP == currentStep.stepId

        // History steps; 'initial-action' is not a history step
        List historySteps = wfEngine.getHistorySteps(piid);
        assert 0 == historySteps.size()

    }


    @Test
    void requestHoliday()  {

        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
        assertCounts historySteps:0, currentSteps:1, actions:2
        
        // 'REQUEST_HOLIDAYS' enters a split which create two current steps
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);

        assertCounts historySteps:1, currentSteps:2, actions:4
        
        // ============================== Split ========================================
        List<Integer> actions = wfEngine.getAvailableActions(piid);        
        
        // Available actions at the split
        assert 4 == actions.size()
        assert actions.any{ it == LINE_MANAGER_APPROVES }
        assert actions.any{ it == LINE_MANAGER_DENIES }
        assert actions.any{ it == HUMAN_RESOURCES_APPROVES }
        assert actions.any{ it == HUMAN_RESOURCES_DENIES }

        assert 'Manager approves' == wd.getAction(actions.find{it == LINE_MANAGER_APPROVES}).name
        assert 'Manager denies' == wd.getAction(actions.find{it == LINE_MANAGER_DENIES}).name
        assert 'HR approves' == wd.getAction(actions.find{it == HUMAN_RESOURCES_APPROVES}).name
        assert 'HR denies' == wd.getAction(actions.find{it == HUMAN_RESOURCES_DENIES}).name
        
        // Available steps after the split
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);
        
        // Count and thier step IDs
        assert 2 == currentSteps.size()
        assert currentSteps.any{ it.stepId == LINE_MANAGER_DESCISION_STEP }
        assert currentSteps.any{ it.stepId == HUMAN_RESOURCES_DESCISION_STEP }

        // Verify the Step names
        assert 'Manager Revision' == wd.getStep(LINE_MANAGER_DESCISION_STEP).name
        assert 'HR Revision' == wd.getStep(HUMAN_RESOURCES_DESCISION_STEP).name


        // ... and their states
        assert 'Pending' == currentSteps.find{it.stepId == LINE_MANAGER_DESCISION_STEP}.status
        assert 'Pending' == currentSteps.find{it.stepId == HUMAN_RESOURCES_DESCISION_STEP}.status

 
        // History steps after the split; Count, stepId and status
        List historySteps = wfEngine.getHistorySteps(piid);
        assert 1 == historySteps.size()
        assert historySteps.any{ it.stepId == EMPLOYEE_REQUEST_STEP }
        assert 'Finished' == historySteps.find{it.stepId == EMPLOYEE_REQUEST_STEP}.status 

    }


    @Test
    void hrApproves_ManagerApproves()  {
 
               
        // Starting point
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)

        assertCounts currentSteps:1, actions:2, historySteps:0

        wfEngine.doAction piid, REQUEST_HOLIDAYS
        assertCounts  currentSteps:2, actions:4, historySteps:1

        wfEngine.doAction piid, HUMAN_RESOURCES_APPROVES
        assertCounts  currentSteps:1, actions:2, historySteps:2

        wfEngine.doAction piid, LINE_MANAGER_APPROVES
        assertCounts  currentSteps:1, actions:1, historySteps:3
 
        wfEngine.doAction piid, NOTIFY_EMPLOYEE
        assertCounts currentSteps:0, actions:0, historySteps:4
       
        // Order the history steps by ascending 'finishDate' 
        def historySteps = wfEngine.getHistorySteps(piid).sort {a,b -> a.finishDate <=> b.finishDate}

        // Verify the order by name ...
        assert historySteps[0].stepId == EMPLOYEE_REQUEST_STEP
        assert historySteps[1].stepId == HUMAN_RESOURCES_DESCISION_STEP
        assert historySteps[2].stepId == LINE_MANAGER_DESCISION_STEP
        assert historySteps[3].stepId == NOTIFY_EMPLOYEE_STEP
        
        // ... and by status
        assert historySteps.every { it.status == 'Finished' }
        
        // Get final process instance state
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
         
        // Get persistent values
        PersistentVars persistentVars = wfEngine.getPersistentVars(piid)
        assert 'approved' == persistentVars.getString('Manager Result')
        assert 'approved' == persistentVars.getString('HR Result')
        assert 'approved' == persistentVars.getString('Employee Request')
 
    }

    @Test
    void managerApproves_HRApproves() {

        // Manager approves then HR approves
 
        // Starting point
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
        assertCounts currentSteps:1, actions:2, historySteps:0

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts currentSteps:2, actions:4, historySteps:1

        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);
        assertCounts currentSteps:1, actions:2, historySteps:2

        wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES);
        assertCounts currentSteps:1, actions:1, historySteps:3

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts currentSteps:0, actions:0, historySteps:4
        
        // Order the history steps by ascending 'finishDate' 
        def historySteps = wfEngine.getHistorySteps(piid).sort {a,b -> a.finishDate <=> b.finishDate}

        // Verify the order by name ...
        assert historySteps[0].stepId == EMPLOYEE_REQUEST_STEP
        assert historySteps[1].stepId == LINE_MANAGER_DESCISION_STEP
        assert historySteps[2].stepId == HUMAN_RESOURCES_DESCISION_STEP
        assert historySteps[3].stepId == NOTIFY_EMPLOYEE_STEP
        
        // ... and by status
        assert historySteps.every { it.status == 'Finished' }
    
        // Get final process instance state
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
         
        // Get persistent values
        PersistentVars persistentVars = wfEngine.getPersistentVars(piid)
        assert 'approved' == persistentVars.getString('Manager Result')
        assert 'approved' == persistentVars.getString('HR Result')
        assert 'approved' == persistentVars.getString('Employee Request')
    }
    
    @Test
    void firstDeniedByLineManager() throws Exception {
 
        def actions = wfEngine.getAvailableActions(piid)
        def currentSteps = wfEngine.getCurrentSteps(piid)
        def persistentVars = wfEngine.getPersistentVars(piid)

        // Starting value
        assert persistentVars.getString('Employee Request') == null
        
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assert persistentVars.getString('Employee Request') == null

        // Both Manager and HR have steps
        assert currentSteps.any{ it.stepId == LINE_MANAGER_DESCISION_STEP }
        assert currentSteps.any{ it.stepId == HUMAN_RESOURCES_DESCISION_STEP }

        // The manager denies, which...
        wfEngine.doAction(piid, LINE_MANAGER_DENIES);
        assert persistentVars.getString('Employee Request') == 'denied'

        // ...causes the HR descision step to be removed
        assert currentSteps.any{ it.stepId == HUMAN_RESOURCES_DESCISION_STEP } == false

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assert persistentVars.getString('Employee Request') == 'denied'

    }

    @Test
    void firstDeniedByHumanResources() throws Exception {

        def actions = wfEngine.getAvailableActions(piid)
        def currentSteps = wfEngine.getCurrentSteps(piid)
        def persistentVars = wfEngine.getPersistentVars(piid)

        // Starting value
        assert persistentVars.getString('Employee Request') == null
        
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assert persistentVars.getString('Employee Request') == null

        // Both Manager and HR have steps
        assert currentSteps.any{ it.stepId == LINE_MANAGER_DESCISION_STEP }
        assert currentSteps.any{ it.stepId == HUMAN_RESOURCES_DESCISION_STEP }

         // HR denies, which...
        wfEngine.doAction(piid, HUMAN_RESOURCES_DENIES);
        assert persistentVars.getString('Employee Request') == 'denied'

        // ...causes the manager descision step to be removed
        assert currentSteps.any{ it.stepId == LINE_MANAGER_DESCISION_STEP } == false

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assert persistentVars.getString('Employee Request') == 'denied'
                 
   }


    // P R I V A T E  ===============================================================================
    private void assertCounts(Map counts) {
        assertCounts(wfEngine, piid, counts.historySteps, counts.currentSteps, counts.actions);
    }
   

}
