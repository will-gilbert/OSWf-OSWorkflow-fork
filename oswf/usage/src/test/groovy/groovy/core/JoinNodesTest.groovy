package groovy.core

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.PersistentVars
import org.informagen.oswf.impl.DefaultOSWfEngine

// OSWf Assertions
import static org.informagen.oswf.testing.OSWfAssertions.assertRoute

// Logging
import org.slf4j.Logger
import org.slf4j.LoggerFactory


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


/**
 * Method 'checkroute' creates a workflow engine and applies an order list of actions
 *
 *  This test is designed to look at the order in which a Join is entered. There
 *    should be no difference in when the Join proceeds to its 'default-result'
 *    or exiting Step.
 *
 *
 *  Step 100 action: 101 - 'Employee request' into split 10000
 *  Split 10000 into Steps 200 &amp; 300
 *  Step 200 action 201 - 'LM Approve' into Join 10000
 *  Step 200 action 202 - 'LM Deny' into Join 10000
 *  Step 300 action 301 - 'HR Approve' into Join 10000
 *  Step 300 action 302 - 'HR Deny' into Join 10000
 *  Join 10000 into Step 400
 *  Step 400 action 401 - 'Notify Employee' and finish
 *
 *     
 *  To turn on Logging edit the resource file 'log4j.properties'
 *     and set the following entry to the DEBUG level
 *      
 *    log4j.logger.tests.JoinNodesTest              DEBUG
 *
 */

class JoinNodesTest {

    static private String workflow

    static private final int START_WORKFLOW   =   1
    static private final int EMPLOYEE_REQUEST = 101
    static private final int MANAGER_APPROVES = 201
    static private final int MANAGER_DENIES   = 202
    static private final int HR_APPROVES      = 301
    static private final int HR_DENIES        = 302
    static private final int NOTIFY_EMPLOYEE  = 401

    private OSWfEngine wfEngine
    private Logger logger

    JoinNodesTest() {
        logger = LoggerFactory.getLogger('tests.JoinNodesTest')
    }

    @BeforeClass
    static void getWorkflow() {
        workflow = JoinNodesTest.class.getResource('/core/joinnodes.oswf.xml') as String
    }


    @Before
    void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine('Join Nodes Test')
    }

    // T E S T S  ----------------------------------------------------------------------------


    @Test
    void route_LM_Approve__HR_Approve()  {
        logger.debug('LM_Approve__HR_Approve')
        checkRoute([EMPLOYEE_REQUEST, MANAGER_APPROVES, HR_APPROVES, NOTIFY_EMPLOYEE], true, true, true)
    }

    @Test
    void route_LM_Deny__HR_Deny()  {
        logger.debug('LM_Deny__HR_Deny')
        checkRoute([EMPLOYEE_REQUEST, MANAGER_DENIES, HR_DENIES, NOTIFY_EMPLOYEE], false, false, false)
    }

    @Test
    void route_LM_Approve__HR_Deny()  {
        logger.debug('LM_Approve__HR_Deny')
        checkRoute([EMPLOYEE_REQUEST, MANAGER_APPROVES, HR_DENIES, NOTIFY_EMPLOYEE], true, false, false)
    }
    
    @Test
    void route_LM_Deny__HR_Approve()  {
        logger.debug('LM_Deny__HR_Approve')
        checkRoute([EMPLOYEE_REQUEST, MANAGER_DENIES, HR_APPROVES, NOTIFY_EMPLOYEE], false, true, false)
    }
    
    @Test
    void route_HR_Approve__LM_Approve()  {
        logger.debug('HR_Approve__LM_Approve')
        checkRoute([EMPLOYEE_REQUEST, HR_APPROVES, MANAGER_APPROVES, NOTIFY_EMPLOYEE], true, true, true)
    }
    
    @Test
    void route_HR_Deny__LM_Deny()  {
        logger.debug('HR_Deny__LM_Deny')
        checkRoute([EMPLOYEE_REQUEST, HR_DENIES, MANAGER_DENIES, NOTIFY_EMPLOYEE], false, false, false)
    }
    
    @Test
    void route_HR_Approve__LM_Deny()  {
        logger.debug('LMHR_Approve__LM_Deny')
        checkRoute([EMPLOYEE_REQUEST, HR_APPROVES, MANAGER_DENIES, NOTIFY_EMPLOYEE], false, true, false)
    }
    
    @Test
    void route_HR_Deny__LM_Approve()  {
        logger.debug('HR_Deny__LM_Approve')
        checkRoute([EMPLOYEE_REQUEST, HR_DENIES, MANAGER_APPROVES, NOTIFY_EMPLOYEE], true, false, false)
    }


    private void checkRoute(actions, boolean manager, boolean hr, boolean requestApproved)  {
        
        try {
            long piid = wfEngine.initialize(workflow, START_WORKFLOW)
            assertRoute(wfEngine, piid, actions as int[])

            PersistentVars persistentVars = wfEngine.getPersistentVars(piid)
            assert manager == persistentVars.getBoolean('Manager Result')
            assert hr == persistentVars.getBoolean('HR Result')
            assert requestApproved == persistentVars.getBoolean('Request Approved')

        } catch (Exception exception) {
            fail(exception.getMessage())
        }
    }
}
