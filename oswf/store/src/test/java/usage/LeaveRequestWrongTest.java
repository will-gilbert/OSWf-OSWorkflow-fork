package usage;

import support.OSWfHibernateTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.exceptions.InvalidInputException;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.Step;

import org.informagen.oswf.TypedMapStore;


// OSWf TypedMap delegate which installs a Custom TypedMap mapping
import org.informagen.oswf.impl.HibernateTypedMapStore;

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


/**
 * Basic workflow flow
 */


public class LeaveRequestWrongTest extends OSWfHibernateTestCase implements LeaveRequest {

    public static final String RDBMS_CONFIGURATION = System.getProperty("rdbms-configuration");


    // Instance variable
    private long bobId;
    private OSWfEngine wfEngine;

    public LeaveRequestWrongTest() {
        super("oswf-store.cfg.xml", RDBMS_CONFIGURATION);
    }


    @Before
    public void setUp() throws Exception {
            
        wfEngine = new DefaultOSWfEngine("Unit Testing");
                
        OSWfConfiguration config = new DefaultOSWfConfiguration()
            .load(getClass().getResource("/oswf-hibernate.xml"))
            .addPersistenceArg("sessionFactory", getSessionFactory())
        ;
                          
        wfEngine.setConfiguration(config);
    
        // "Unit Testing" is the system which will owns all of the workflow instances
        //   created with 'createProcessInstance'
    
        bobId = createProcessInstance(wfEngine, WRONG, INITIAL_ACTION);

    }

    @After
    public void teardown() {
        closeSessionFactory();
    }


    @Test
    public void initialState()  {
    
        assertCounts(wfEngine, bobId, 0, 1, 1);
        
        List<Integer> actions = wfEngine.getAvailableActions(bobId);
        WorkflowDescriptor wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(bobId));

        // Available actions at the beginning
        assertEquals(1, actions.size());
        assertEquals(REQUEST_HOLIDAYS, actions.get(0).intValue());
        assertEquals("request holidays", wd.getAction(actions.get(0)).getName());


        // Current steps at the beginning
        List<Step> currentSteps = wfEngine.getCurrentSteps(bobId);
        assertEquals(1, currentSteps.size());
        
        Step currentStep = currentSteps.get(0);
        
        assertStepState(currentStep, "Underway");      
        
        assertEquals(EMPLOYEE_REQUEST_STEP, currentStep.getStepId());

        // History steps at the beginning
        List historySteps = wfEngine.getHistorySteps(bobId);
        assertEquals(0, historySteps.size());

    }


    @Test
    public void requestHolidy() throws Exception {

        assertCounts(wfEngine, bobId, 0, 1, 1);
        
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);
        
        List<Integer> actions = wfEngine.getAvailableActions(bobId);

        WorkflowDescriptor wd = wfEngine.getWorkflowDescriptor(wfEngine.getWorkflowName(bobId));
        
        
        // ============================== Split ========================================
        
        // Available actions at the split
        assertEquals(LINE_MANAGER_APPROVES,    actions.get(0).intValue());
        assertEquals(LINE_MANAGER_DENIES,      actions.get(1).intValue());
        assertEquals(HUMAN_RESOURCES_APPROVES, actions.get(2).intValue());
        assertEquals(HUMAN_RESOURCES_DENIES,   actions.get(3).intValue());

        assertEquals("approve",    wd.getAction(actions.get(0)).getName());
        assertEquals("deny",       wd.getAction(actions.get(1)).getName());
        assertEquals("approve", wd.getAction(actions.get(2)).getName());
        assertEquals("deny",    wd.getAction(actions.get(3)).getName());
        
        // Available steps at the split
        List currentSteps = wfEngine.getCurrentSteps(bobId);
        
        assertEquals(2, currentSteps.size());

        assertEquals(LINE_MANAGER_DESCISION_STEP, ((Step)currentSteps.get(0)).getStepId());
        assertEquals(HUMAN_RESOURCES_DESCISION_STEP, ((Step)currentSteps.get(1)).getStepId());

        // Verify the Step ids
        assertEquals("Manager Revision", wd.getStep(((Step)currentSteps.get(0)).getStepId()).getName());
        assertEquals("HR Revision",  wd.getStep(((Step)currentSteps.get(1)).getStepId()).getName());

        // Verify the Step names
        assertEquals("Manager Revision", wd.getStep(LINE_MANAGER_DESCISION_STEP).getName());
        assertEquals("HR Revision", wd.getStep(HUMAN_RESOURCES_DESCISION_STEP).getName());

        assertStepState((Step)currentSteps.get(0), "Underway");      
        assertStepState((Step)currentSteps.get(1), "Underway");      
 
 
        // History steps at the split
        List historySteps = wfEngine.getHistorySteps(bobId);

        assertEquals(1, historySteps.size());

        assertEquals(EMPLOYEE_REQUEST_STEP, ((Step)historySteps.get(0)).getStepId());
        assertStepState((Step)historySteps.get(0), "Finished");      

    }


    @Test
    public void approved() throws Exception {
 
        // Starting point
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, bobId, 0, 1, 1);

        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);

        wfEngine.doAction(bobId, LINE_MANAGER_APPROVES);
        assertCounts(wfEngine, bobId, 2, 1, 2);

        wfEngine.doAction(bobId, HUMAN_RESOURCES_APPROVES);
        assertCounts(wfEngine, bobId, 3, 1, 1);

        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, bobId, 4, 0, 0);
        
        // No available steps at the end
        List currentSteps = wfEngine.getCurrentSteps(bobId);
        assertEquals(0, currentSteps.size());

        // History steps at the end
        List historySteps = wfEngine.getHistorySteps(bobId);
        assertEquals(4, historySteps.size());


        // Ordered as they occur
        assertEquals(EMPLOYEE_REQUEST_STEP,         ((Step)historySteps.get(0)).getStepId());
        assertEquals(LINE_MANAGER_DESCISION_STEP,    ((Step)historySteps.get(1)).getStepId());
        assertEquals(HUMAN_RESOURCES_DESCISION_STEP, ((Step)historySteps.get(2)).getStepId());
        assertEquals(NOTIFY_EMPLOYEE_STEP,          ((Step)historySteps.get(3)).getStepId());
        
        assertStepState((Step)historySteps.get(0), "Finished");      
        assertStepState((Step)historySteps.get(1), "Manager approved");      
        assertStepState((Step)historySteps.get(2), "HR approved");      
        assertStepState((Step)historySteps.get(3), "Finished");      
        
        //Get final status
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETED);
        
        TypedMap propertySet = wfEngine.getTypedMap(bobId);

        //Get final result value
        assertProperty(wfEngine, bobId, "result", "approved");
    }
    
    @Test
    public void deniedByLineManager() throws Exception {
 
        // Starting point
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, bobId, 0, 1, 1);

        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);

        wfEngine.doAction(bobId, LINE_MANAGER_DENIES);
        assertCounts(wfEngine, bobId, 2, 2, 3);

        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, bobId, 4, 0, 0);
        
        //Get final status

        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETED);
        
        //Get final result value
        assertProperty(wfEngine, bobId, "result", "denied");

    }

    @Test
    public void overrideApprovalByHumanResources() throws Exception {
 
        // Starting point
        assertProperty(wfEngine, bobId, "result", null);
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, bobId, 0, 1, 1);

        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);

        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, LINE_MANAGER_APPROVES);
        assertCounts(wfEngine, bobId, 2, 1, 2);

        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, HUMAN_RESOURCES_DENIES);
        assertCounts(wfEngine, bobId, 3, 1, 1);

        assertProperty(wfEngine, bobId, "result", "denied");
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, bobId, 4, 0, 0);
        
        //Get final status
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETED);
        
        //Get final result value
        assertProperty(wfEngine, bobId, "result", "denied");

    }

    @Test
    public void deniedByHumanResources() throws Exception {
 
        // Starting point
        assertProperty(wfEngine, bobId, "result", null);
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, bobId, 0, 1, 1);

        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);

        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, HUMAN_RESOURCES_DENIES);
        assertCounts(wfEngine, bobId, 2, 2, 3);

        assertProperty(wfEngine, bobId, "result", "denied");
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, bobId, 4, 0, 0);
        
        //Get final status
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETED);
        
        //Get final result value
        assertProperty(wfEngine, bobId, "result", "denied");

    }

    // NB: In this scenario, after the line manager denies the HR Director's
    //   approval or denial is mute; However, there are now two current steps
    //   at the 'Notify employee' step. Also the HR Director was given a meaningless
    //   work task.
    //
    //   This violates Petri Net...
    //
    
    @Test
    public void deniedByBoth() throws Exception {

 
        // Starting point
        assertProperty(wfEngine, bobId, "result", null);
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertCounts(wfEngine, bobId, 0, 1, 1);

        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, bobId, 1, 2, 4);

        // ===================== Split ==========================

        // Line Manager denies
        assertProperty(wfEngine, bobId, "result", null);
        wfEngine.doAction(bobId, LINE_MANAGER_DENIES);
        assertCounts(wfEngine, bobId, 2, 2, 3);

        // Human Resources denies
        assertProperty(wfEngine, bobId, "result", "denied");
        wfEngine.doAction(bobId, HUMAN_RESOURCES_DENIES);
        assertCounts(wfEngine, bobId, 3, 2, 2);

        // ===================== Join ===========================
        
        // Notify Employee
        assertProperty(wfEngine, bobId, "result", "denied");
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        assertCounts(wfEngine, bobId, 5, 0, 0);

        //Get final status

        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETED);
        
        //Get final result value
        assertProperty(wfEngine, bobId, "result", "denied");

    }

    

}
