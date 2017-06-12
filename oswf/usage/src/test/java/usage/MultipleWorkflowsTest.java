package usage;

import org.informagen.oswf.testing.OSWfTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

import org.informagen.oswf.exceptions.InvalidInputException;


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
 *   assertCount(Long piid, int historySteps, int currentSteps, in actions);
 */


public class MultipleWorkflowsTest extends OSWfTestCase implements Constants {


    private static final String LEAVE_REQUEST_WORKFLOW = "LeaveRequest - Alt";
    private static final String HOLIDAY_WORKFLOW = "Holiday";
    
    private OSWfEngine wfEngine;
    
    @Before
    public void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
                
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));                  
        wfEngine.setConfiguration(config);
    }

    @After
    public void teardown() {
        MemoryWorkflowStore.reset();
    }


    @Test
    public void multipleInstances() throws Exception {
    
        long aliceId = createProcessInstance(wfEngine, LEAVE_REQUEST_WORKFLOW, INITIAL_ACTION);
        long bobId = createProcessInstance(wfEngine, LEAVE_REQUEST_WORKFLOW,  INITIAL_ACTION);
        long tedId = createProcessInstance(wfEngine, HOLIDAY_WORKFLOW, INITIAL_ACTION);
        
        // Starting point ==============================================================
        
        assertProperty(wfEngine, aliceId, "Employee Request", null);
        assertProperty(wfEngine, bobId,   "Employee Request", null);
        assertProperty(wfEngine, tedId,   "result", null);
        
        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, bobId,   ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, tedId,   ProcessInstanceState.ACTIVE);
        
        assertCounts(wfEngine, aliceId, 0, 1, 2);
        assertCounts(wfEngine, bobId,   0, 1, 2);
        assertCounts(wfEngine, tedId,   0, 1, 1);

        // Start some processes ========================================================


        // Start Bob and Alice, but leave Ted behind
        wfEngine.doAction(aliceId, REQUEST_HOLIDAYS);
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, aliceId, 1, 2, 4);
        assertCounts(wfEngine, bobId,   1, 2, 4);
        assertCounts(wfEngine, tedId,   0, 1, 1);

        // Round 1 of approvals ========================================================

        // Human Resources denies Alice
        wfEngine.doAction(aliceId, HUMAN_RESOURCES_DENIES);


        // Line Manager approves Bob
        wfEngine.doAction(bobId, LINE_MANAGER_APPROVES);

        // Start Ted
        wfEngine.doAction(tedId, REQUEST_HOLIDAYS);

        assertCounts(wfEngine, aliceId, 3, 1, 1);
        assertCounts(wfEngine, bobId,   2, 1, 2);
        assertCounts(wfEngine, tedId,   1, 1, 2);

        // The AND-Join has yet to complete, 'result' not yet set
        assertProperty(wfEngine, aliceId, "Employee Request", "denied");
        assertProperty(wfEngine, bobId,   "Employee Request", null);
        
        // Ted only requires Line Manager approval but has not yet recieved it
        assertProperty(wfEngine, tedId,   "result", null);

        // Line Manager approves Ted; only approval needed ============================
        
        wfEngine.doAction(tedId, LINE_MANAGER_APPROVES);
        assertCounts(wfEngine, tedId,   2, 0, 0);
        assertProcessInstanceState(wfEngine, tedId, ProcessInstanceState.COMPLETED);
        assertProperty(wfEngine, tedId,   "result", "approved");


        // Round 2 of approvals for Alice and Bob =====================================

        // Line Manager has no work regarding Alice because HR has already denied
        // wfEngine.doAction(aliceId, LINE_MANAGER_DENIES);

        // Human Resources approves Bob
        wfEngine.doAction(bobId, HUMAN_RESOURCES_APPROVES);

        assertCounts(wfEngine, aliceId, 3, 1, 1);
        assertCounts(wfEngine, bobId,   3, 1, 1);
        
        // The e-mail system fires immediately or is scheduled later to deliver notification
        //   but it will have to reference the each piid
        wfEngine.doAction(aliceId, NOTIFY_EMPLOYEE);
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);

        assertCounts(wfEngine, aliceId, 4, 0, 0);
        assertCounts(wfEngine, bobId,   4, 0, 0);

        assertProperty(wfEngine, aliceId, "Employee Request", "denied");
        assertProperty(wfEngine, bobId,   "Employee Request", "approved");

        // Both two approval workflows are finished
        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.COMPLETED);
        assertProcessInstanceState(wfEngine, bobId,   ProcessInstanceState.COMPLETED);

    }
    

}
