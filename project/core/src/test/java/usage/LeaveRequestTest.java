package usage;

import org.informagen.oswf.testing.OSWfTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.exceptions.InvalidInputException;

import org.informagen.oswf.impl.stores.MemoryStore;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;


// Java Util
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static java.util.Collections.EMPTY_MAP;


/**
 *   assertCount(Long piid, int historySteps, int currentSteps, in actions);
 */


public class LeaveRequestTest extends OSWfTestCase implements Constants {



    // Process Instance identifier
    protected long piid;
    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
                
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));
                          
        wfEngine.setConfiguration(config);
    
        // "Unit Testing" is the user who will be the actor of all of the workflow
        //   actions.  This workflow has no action restrictions.
        //
        // RE: ActionRestrictionsTest, StepOwnershipTest, and StepConditionsTest

        // Create a 'Leave Request' PI for 'Bob BobbleHead'
        Map<String, Object> inputVariables = new HashMap<String, Object>();
        inputVariables.put("firstName", "Bob");
        inputVariables.put("lastName", "Bobblehead");
        
        piid = createProcessInstance(wfEngine, "LeaveRequest - Alt", INITIAL_ACTION, inputVariables);

    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }


    @Test
    public void initialState()  {
    
        assertCounts(wfEngine, piid, 0, 1, 2);
        
        List<Integer> actions = wfEngine.getAvailableActions(piid);
        WorkflowDescriptor wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));

        // Available actions at the beginning
        assertEquals(2, actions.size());
        assertEquals(REQUEST_HOLIDAYS, actions.get(0).intValue());
        assertEquals("Request day off", wd.getAction(actions.get(0)).getName());


        // Current steps at the beginning
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals(1, currentSteps.size());
        
        Step currentStep = (Step)currentSteps.get(0);
        
        assertStepState((Step)currentStep, "Pending");      
        
        assertEquals(EMPLOYEE_REQUEST_STEP, currentStep.getStepId());

        // History steps at the beginning
        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals(0, historySteps.size());

    }


    @Test
    public void requestHoliday() throws Exception {

        assertCounts(wfEngine, piid, 0, 1, 2);
        
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);
        
        List<Integer> actions = wfEngine.getAvailableActions(piid);

        WorkflowDescriptor wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(piid));
        
        
        // ============================== Split ========================================
        
        // Available actions at the split
        assertEquals(LINE_MANAGER_APPROVES,    actions.get(0).intValue());
        assertEquals(LINE_MANAGER_DENIES,      actions.get(1).intValue());
        assertEquals(HUMAN_RESOURCES_APPROVES, actions.get(2).intValue());
        assertEquals(HUMAN_RESOURCES_DENIES,   actions.get(3).intValue());

        assertEquals("Manager approves", wd.getAction(actions.get(0)).getName());
        assertEquals("Manager denies",   wd.getAction(actions.get(1)).getName());
        assertEquals("HR approves",      wd.getAction(actions.get(2)).getName());
        assertEquals("HR denies",        wd.getAction(actions.get(3)).getName());
        
        // Available steps at the split
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);
        
        assertEquals(2, currentSteps.size());

        assertEquals(LINE_MANAGER_REVISION_STEP, currentSteps.get(0).getStepId());
        assertEquals(HUMAN_RESOURCES_REVISION_STEP, ((Step)currentSteps.get(1)).getStepId());

        // Verify the Step ids
        assertEquals("Manager Revision", wd.getStep(((Step)currentSteps.get(0)).getStepId()).getName());
        assertEquals("HR Revision",  wd.getStep(((Step)currentSteps.get(1)).getStepId()).getName());

        // Verify the Step names
        assertEquals("Manager Revision", wd.getStep(LINE_MANAGER_REVISION_STEP).getName());
        assertEquals("HR Revision", wd.getStep(HUMAN_RESOURCES_REVISION_STEP).getName());

        assertStepState((Step)currentSteps.get(0), "Pending");      
        assertStepState((Step)currentSteps.get(1), "Pending");      
 
        // History steps at the split
        List historySteps = wfEngine.getHistorySteps(piid);

        assertEquals(1, historySteps.size());

        assertEquals(EMPLOYEE_REQUEST_STEP, ((Step)historySteps.get(0)).getStepId());
        assertStepState((Step)historySteps.get(0), "Finished");      

    }


    @Test
    public void bothApproved_1() throws Exception {
 
         // HR approves then Manager approves
               
        // assertCount(historySteps, currentSteps, actions)
        // Starting point
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, piid, 0, 1, 2);

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);

        wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES);
        assertCounts(wfEngine, piid, 2, 1, 2);

        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);
       assertCounts(wfEngine, piid, 3, 1, 1);
 
        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, piid, 4, 0, 0);
       
        // No available steps at the end
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals(0, currentSteps.size());

        // History steps at the end
        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals(4, historySteps.size());

        // Reverse ordered as they occurred (OSWf v3.0)
        // Fails if using MySQL, not reversed; Find out why
        // assertEquals(EMPLOYEE_REQUEST_STEP,         ((Step)historySteps.get(3)).getStepId());
        // assertEquals(HUMAN_RESOURCES_REVISION_STEP, ((Step)historySteps.get(2)).getStepId());
        // assertEquals(LINE_MANAGER_REVISION_STEP,    ((Step)historySteps.get(1)).getStepId());
        // assertEquals(NOTIFY_EMPLOYEE_STEP,          ((Step)historySteps.get(0)).getStepId());
        // 
        // assertStepState((Step)historySteps.get(3), "Finished");      
        // assertStepState((Step)historySteps.get(2), "Approved");      
        // assertStepState((Step)historySteps.get(1), "Approved");      
        // assertStepState((Step)historySteps.get(0), "Finished");      
        
        //Get final status
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);
        
        PropertySet propertySet = wfEngine.getPropertySet(piid);
        
        //Get final result value
        assertProperty(wfEngine, piid, "Employee Request", "approved");
    }

    @Test
    public void bothApproved_2() throws Exception {

        // Manager approves then HR approves
 
        // Starting point
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, piid, 0, 1, 2);

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);

        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);
        assertCounts(wfEngine, piid, 2, 1, 2);

        wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES);
        assertCounts(wfEngine, piid, 3, 1, 1);

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, piid, 4, 0, 0);
        
        // No available steps at the end
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals(0, currentSteps.size());

        // History steps at the end
        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals(4, historySteps.size());

        // Reverse ordered as they occurred (OSWf v3.0)
        // assertEquals(EMPLOYEE_REQUEST_STEP,         ((Step)historySteps.get(3)).getStepId());
        // assertEquals(LINE_MANAGER_REVISION_STEP,    ((Step)historySteps.get(2)).getStepId());
        // assertEquals(HUMAN_RESOURCES_REVISION_STEP, ((Step)historySteps.get(1)).getStepId());
        // assertEquals(NOTIFY_EMPLOYEE_STEP,          ((Step)historySteps.get(0)).getStepId());
        // 
        // assertStepState((Step)historySteps.get(3), "Finished");      
        // assertStepState((Step)historySteps.get(2), "Approved");      
        // assertStepState((Step)historySteps.get(1), "Approved");      
        // assertStepState((Step)historySteps.get(0), "Finished");      
        
        //Get final status
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);
        
        PropertySet propertySet = wfEngine.getPropertySet(piid);
        
        //Get final result value
        assertProperty(wfEngine, piid, "Employee Request", "approved");
    }
    
    @Test
    public void deniedByLineManager() throws Exception {
 
        // Starting point
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, piid, 0, 1, 2);

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);

        wfEngine.doAction(piid, LINE_MANAGER_DENIES);
        assertCounts(wfEngine, piid, 3, 1, 1);

        // wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES);
        // assertCounts(wfEngine, piid, 3, 1, 1);

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, piid, 4, 0, 0);
        
        //Get final status
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);
        
        //Get final result value
        assertProperty(wfEngine, piid, "Employee Request", "denied");

    }

    @Test
    public void deniedByHumanResources() throws Exception {
 
        // Starting point
        assertProperty(wfEngine, piid, "result", null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, piid, 0, 1, 2);

        assertProperty(wfEngine, piid, "result", null);
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);

        assertProperty(wfEngine, piid, "Employee Request", null);
        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);
        assertCounts(wfEngine, piid, 2, 1, 2);

        assertProperty(wfEngine, piid, "Employee Request", null);
        wfEngine.doAction(piid, HUMAN_RESOURCES_DENIES);
        assertCounts(wfEngine, piid, 3, 1, 1);

        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, piid, 4, 0, 0);
        
        //Get final status
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);
         
        //Get final result value
        assertProperty(wfEngine, piid, "Employee Request", "denied");
   }

    @Test
    public void deniedByBoth() throws Exception {
 
        // Starting point
        assertProperty(wfEngine, piid, "Employee Request", null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, piid, 0, 1, 2);

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, piid, 1, 2, 4);

        // ===================== Split ==========================

        // Line Manager denies
        assertProperty(wfEngine, piid, "Employee Request", null);
        wfEngine.doAction(piid, LINE_MANAGER_DENIES);
        assertCounts(wfEngine, piid, 3, 1, 1);

        // Human Resources has no pending tasks
        assertProperty(wfEngine, piid, "Employee Request", "denied");
        //wfEngine.doAction(piid, HUMAN_RESOURCES_DENIES);
        //assertCounts(wfEngine, piid, 3, 1, 1);

        // ===================== Join ===========================
        
        wfEngine.doAction(piid, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, piid, 4, 0, 0);

        //Get final status

        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);
        
        //Get final result value
        assertProperty(wfEngine, piid, "Employee Request", "denied");

    }

    

}
