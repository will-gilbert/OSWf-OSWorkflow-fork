package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf Assertions
import static org.informagen.oswf.testing.OSWfAssertions.assertRoute;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

public class JoinNodesTest {

    private OSWfEngine wfEngine;
    private Logger logger;

    public JoinNodesTest() {
        logger = LoggerFactory.getLogger("tests.JoinNodesTest");
    }

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("JoinNodesTest");
    }

    // T E S T S  ----------------------------------------------------------------------------


    @Test
    public void route_LM_Approve__HR_Approve()  {
        logger.debug("LM_Approve__HR_Approve");
        checkRoute(new int[] {101, 201, 301, 401});
    }

    @Test
    public void route_LM_Deny__HR_Deny()  {
        logger.debug("LM_Deny__HR_Deny");
        checkRoute(new int[] {101, 202, 302, 401});
    }

    @Test
    public void route_LM_Approve__HR_Deny()  {
        logger.debug("LM_Approve__HR_Deny");
        checkRoute(new int[] {101, 201, 302, 401});
    }
    
    @Test
    public void route_LM_Deny__HR_Approve()  {
        logger.debug("LM_Deny__HR_Approve");
        checkRoute(new int[] {101, 202, 301, 401});
    }
    
    @Test
    public void route_HR_Approve__LM_Approve()  {
        logger.debug("HR_Approve__LM_Approve");
        checkRoute(new int[] {101, 301, 201, 401});
    }
    
    @Test
    public void route_HR_Deny__LM_Deny()  {
        logger.debug("HR_Deny__LM_Deny");
        checkRoute(new int[] {101, 302, 202, 401});
    }
    
    @Test
    public void route_HR_Approve__LM_Deny()  {
        logger.debug("LMHR_Approve__LM_Deny");
        checkRoute(new int[] {101, 301, 202, 401});
    }
    
    @Test
    public void route_HR_Deny__LM_Approve()  {
        logger.debug("HR_Deny__LM_Approve");
        checkRoute(new int[] {101, 302, 201, 401});
    }


    private void checkRoute(int[] actions)  {
        

        try {
            long workflowId = wfEngine.initialize(getClass().getResource("/core/joinnodes.oswf.xml").toString(), 1);
            assertRoute(wfEngine, workflowId, actions);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
}
