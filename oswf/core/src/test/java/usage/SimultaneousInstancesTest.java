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

import org.informagen.oswf.impl.stores.MemoryStore;

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
 * Basic workflow flow
 */


public class SimultaneousInstancesTest extends OSWfTestCase implements Constants {

    OSWfEngine wfEngine;
 
    
    @Before
    public void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
                
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));
                  
        wfEngine.setConfiguration(config);
    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }


    @Test
    public void multipleInstances() throws Exception {
    
        long aliceId = createProcessInstance(wfEngine, "LeaveRequest - Alt", INITIAL_ACTION);
        long bobId = createProcessInstance(wfEngine, "LeaveRequest - Alt", INITIAL_ACTION);
        long tedId = createProcessInstance(wfEngine, "LeaveRequest - Alt", INITIAL_ACTION);
        
        // Starting point ==============================================================
        //  All results are null
        //  All process instances are active
        //  All process instances have one current Step, one action
        
        assertProperty(wfEngine, aliceId, "Employee Request", null);
        assertProperty(wfEngine, bobId,   "Employee Request", null);
        assertProperty(wfEngine, tedId,   "Employee Request", null);
        
        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, bobId,   ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, tedId,   ProcessInstanceState.ACTIVE);
        
        assertCounts(wfEngine, aliceId, 0, 1, 2);
        assertCounts(wfEngine, bobId,   0, 1, 2);
        assertCounts(wfEngine, tedId,   0, 1, 2);

        // Start some processes ========================================================

        // Start Bob and Alice, but leave Ted behind

        wfEngine.doAction(aliceId, REQUEST_HOLIDAYS);
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, aliceId, 1, 2, 4);
        assertCounts(wfEngine, bobId,   1, 2, 4);
        assertCounts(wfEngine, tedId,   0, 1, 2);

        // Round 1 of approvals ========================================================

        // Human Resources denies Alice
        wfEngine.doAction(aliceId, HUMAN_RESOURCES_DENIES);

        // Line Manager approves Bob
        wfEngine.doAction(bobId, LINE_MANAGER_APPROVES);

        // Start Ted
        wfEngine.doAction(tedId, REQUEST_HOLIDAYS);

        assertCounts(wfEngine, aliceId, 3, 1, 1);
        assertCounts(wfEngine, bobId,   2, 1, 2);
        assertCounts(wfEngine, tedId,   1, 2, 4);

        // The AND-Join has not yet completed
        assertProperty(wfEngine, aliceId, "Employee Request", "denied");
        assertProperty(wfEngine, bobId,   "Employee Request", null);
        assertProperty(wfEngine, tedId,   "Employee Request", null);

        // Round 2 of approvals ========================================================

        // Line Manager denies Alice; Already denied
        //wfEngine.doAction(aliceId, LINE_MANAGER_DENIES);

        // Human Resources approves Bob
        wfEngine.doAction(bobId, HUMAN_RESOURCES_APPROVES);

        // Line Manager denies Ted
        wfEngine.doAction(tedId, LINE_MANAGER_DENIES);

        assertCounts(wfEngine, aliceId, 3, 1, 1);
        assertCounts(wfEngine, bobId,   3, 1, 1);
        assertCounts(wfEngine, tedId,   3, 1, 1);

        assertProperty(wfEngine, aliceId, "Employee Request", "denied");
        assertProperty(wfEngine, bobId,   "Employee Request", "approved");
        assertProperty(wfEngine, tedId,   "Employee Request", "denied");

        // Round 3 of Approval for Ted ===============================================

        // Human Resources approves Ted; Manager has already denied
        // wfEngine.doAction(tedId, HUMAN_RESOURCES_APPROVES);

        assertCounts(wfEngine, tedId, 3, 1, 1);

        assertProperty(wfEngine, aliceId, "Employee Request", "denied");
        assertProperty(wfEngine, bobId,   "Employee Request", "approved");
        assertProperty(wfEngine, tedId,   "Employee Request", "denied");

        wfEngine.doAction(aliceId, NOTIFY_EMPLOYEE);
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);
        wfEngine.doAction(tedId, NOTIFY_EMPLOYEE);


        // Alice and Bob are done, but Ted's process is still active

        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.COMPLETED);
        assertProcessInstanceState(wfEngine, bobId,   ProcessInstanceState.COMPLETED);
        assertProcessInstanceState(wfEngine, tedId,   ProcessInstanceState.COMPLETED);

    }
    

}
