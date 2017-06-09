package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.testing.OSWfAssertions;

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
 * @author will gilbert
 *
 * When a token exits and reenter a split they should or should not
 *  create an additional current step?
 *
 * What if more than one token exits the split?
 *
 * Or can a token jump over the join? In this case the join should be
 * an OR join or else the workflow creates a dead state
 */
public class WF373Test {

    private static final Logger logger = LoggerFactory.getLogger(WF373Test.class);

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("WF373Test");
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void noLoopback() throws Exception {
        checkRoute(new int[] {2, 4, 6});
    }
    
    @Test
    public void noLoopback_ReverseJoinOrder() throws Exception {
        checkRoute(new int[] {2, 6, 4});
    }


    @Test
    public void loopback_Via_3() throws Exception {
        checkRoute(new int[] {2, 3, 2, 4, 6});
    }
    
    @Test
    public void loopback_Via_5() throws Exception {
        checkRoute(new int[] {2, 5, 2, 6, 4});
    }
 
    //@Ignore("This works only if we allow the split to create two tokens in Step 3 (B)")
    @Ignore
    public void Loopback_Via_3_ReverseJoinOrder() throws Exception {
        checkRoute(new int[] {2, 3, 2, 6, 4, 6});
    }
 
    //@Ignore("This should work but doesn't")
    @Ignore
    public void Loopback_Via_5_ReverseJoinOrder() throws Exception {
        checkRoute(new int[] {2, 5, 2, 4, 6, 4});
    }
 
    //@Ignore("This should work but doesn't")
    @Ignore
    public void loopback_Both() throws Exception {
        checkRoute(new int[] {2, 3, 5, 2, 2, 4, 6, 4, 6}, true);
    }
   
    @Test
    public void lookback_ClearJoin_Then_RenterSplit() throws Exception {
        checkRoute(new int[] {2, 3, 6, 2, 6, 4});
    }


    // This threw an exception for OSWorkflow v2.8
    
    private void checkRoute(int[] actions) throws Exception {
        checkRoute(actions, false);
    }
    
    private void checkRoute(int[] actions, boolean logSteps) throws Exception {
        
        long piid = wfEngine.initialize(getClass().getResource("/core/WF-373.oswf.xml").toString(), 1, null);

        for (int i = 0; i < actions.length; i++) {
            
            if(logSteps) {
                logger.debug("---------------------------");
                OSWfAssertions.logHistorySteps(logger, piid, wfEngine);
                logger.debug("");
                OSWfAssertions.logCurrentSteps(logger, piid, wfEngine);
                logger.debug("");
                OSWfAssertions.logAvailableActions(logger, piid, wfEngine);
                logger.debug("");
                logger.debug("doAction: " + actions[i]);
            }
            
            wfEngine.doAction(piid, actions[i]);
            
        }
    }
}
