package usage;

import org.informagen.oswf.testing.OSWfHibernateTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.PropertySetStore;

import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.exceptions.InvalidInputException;

import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;


import org.informagen.oswf.PropertySetStore;

// OSWf PropertySet delegate which installs a Custom PropertySet mapping
import org.informagen.oswf.impl.HibernatePropertySetStore;


import org.informagen.oswf.propertyset.PropertySet;

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


public class MultipleWorkflowsTest extends OSWfHibernateTestCase implements LeaveRequest {

    private static final String DBCONFIG = "H2.hibernate.xml";
//    private static final String DBCONFIG = "MySQL.hibernate.xml";

    private static final String LEAVE_REQUEST_WORKFLOW = COMPLEX;
    private static final String HOLIDAY_WORKFLOW = SIMPLE;

    private OSWfEngine wfEngine;
 
     public MultipleWorkflowsTest() {
        super("oswf-store.cfg.xml",
              "oswf-propertyset.cfg.xml",
              DBCONFIG);
    }
   
    
    @Before
    public void setUp() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("MultipleWorkflowsTest")
            .setConfiguration(new DefaultOSWfConfiguration()
                .load(getClass().getResource("/oswf.xml"))
                .addPersistenceArg("sessionFactory", getSessionFactory())
            )
        ;
    }

    @After
    public void teardown() {
        closeSessionFactory();
    }


    @Test
    public void multipleInstances() throws Exception {
    
        long bobId = createProcessInstance(wfEngine, LEAVE_REQUEST_WORKFLOW,INITIAL_ACTION);
        long aliceId= createProcessInstance(wfEngine, LEAVE_REQUEST_WORKFLOW, INITIAL_ACTION);
        long tedId = createProcessInstance(wfEngine, HOLIDAY_WORKFLOW, INITIAL_ACTION);
        
        // Starting point ==============================================================
        
        assertProperty(wfEngine, aliceId, "result", null);
        assertProperty(wfEngine, bobId,   "result", null);
        assertProperty(wfEngine, tedId,   "result", null);
        
        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, bobId,   ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, tedId,   ProcessInstanceState.ACTIVE);
        
        assertCounts(wfEngine, aliceId, 0, 1, 1);
        assertCounts(wfEngine, bobId,   0, 1, 1);
        assertCounts(wfEngine, tedId,   0, 1, 1);

        // Start some processes ========================================================


        // Start Bob and Alice, but leave Ted behind
        wfEngine.doAction(aliceId, REQUEST_HOLIDAYS);
        wfEngine.doAction(bobId, REQUEST_HOLIDAYS);
        assertCounts(wfEngine, aliceId, 1, 2, 4);
        assertCounts(wfEngine, bobId,   1, 2, 4);
        assertCounts(wfEngine, tedId,   0, 1, 1);

        // Round 1 of approvals ========================================================

        // Line Manager approves Bob
        wfEngine.doAction(bobId, LINE_MANAGER_APPROVES);

        // Human Resources denies Alice
        wfEngine.doAction(aliceId, HUMAN_RESOURCES_DENIES);

        // Start Ted
        wfEngine.doAction(tedId, REQUEST_HOLIDAYS);

        assertCounts(wfEngine, aliceId, 2, 1, 2);
        assertCounts(wfEngine, bobId,   2, 1, 2);
        assertCounts(wfEngine, tedId,   1, 1, 2);

        // Both are 'denied' because the wfEngine uses AND logic to achieve 'approved'
        assertProperty(wfEngine, aliceId, "result", "denied");
        assertProperty(wfEngine, bobId,   "result", "denied");
        assertProperty(wfEngine, tedId,   "result", null);

        // Round 2 of approvals ========================================================

        // Line Manager denies Alice
        wfEngine.doAction(aliceId, LINE_MANAGER_DENIES);

        // Human Resources approves Bob
        wfEngine.doAction(bobId, HUMAN_RESOURCES_APPROVES);

        // Manager approves Ted
        wfEngine.doAction(tedId, LINE_MANAGER_APPROVES);

        assertCounts(wfEngine, aliceId, 3, 1, 1);
        assertCounts(wfEngine, bobId,   3, 1, 1);
        assertCounts(wfEngine, tedId,   2, 0, 0);

        assertProperty(wfEngine, aliceId, "result", "denied");
        assertProperty(wfEngine, bobId,   "result", "approved");
        assertProperty(wfEngine, tedId,   "result", "approved");

        // Ted is done, he used a simpler workflow
        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.ACTIVE);
        assertProcessInstanceState(wfEngine, tedId, ProcessInstanceState.COMPLETE);

        // Round 3 of Approvals and Notifications ======================================

        // Notify Alice and Bob of the outcomes
        wfEngine.doAction(aliceId, NOTIFY_EMPLOYEE);
        wfEngine.doAction(bobId, NOTIFY_EMPLOYEE);


        assertCounts(wfEngine, aliceId, 4, 0, 0);
        assertCounts(wfEngine, bobId,   4, 0, 0);
        assertCounts(wfEngine, tedId,   2, 0, 0);

        assertProperty(wfEngine, aliceId, "result", "denied");
        assertProperty(wfEngine, bobId,   "result", "approved");
        assertProperty(wfEngine, tedId,   "result", "approved");


        // All workflows are finished

        assertProcessInstanceState(wfEngine, aliceId, ProcessInstanceState.COMPLETE);
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETE);
        assertProcessInstanceState(wfEngine, tedId, ProcessInstanceState.COMPLETE);


        // Determine Ted's last step status.
        //  NB: With this workflow we can't determine the final outcome of
        //      this workflow!!!
        // We must use a persistant variable
        
        List<Step> historySteps = wfEngine.getHistorySteps(tedId);

        int last = historySteps.size() -1;
        assertEquals("Revised", historySteps.get(last).getStatus());
        assertProperty(wfEngine, tedId,   "result", "approved");

    }
    

}