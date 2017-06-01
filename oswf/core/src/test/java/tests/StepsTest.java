package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.Step;


import java.net.URL;

import java.util.List;

import static java.util.Collections.EMPTY_MAP;

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


public class StepsTest {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("StepsTest");
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void testEarlySplit() throws Exception {
    
        List<Step> currentSteps;
        List<Step> historySteps;

        URL url = getClass().getResource("/core/earlysplit.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100, null);

        currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());
 
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 0, historySteps.size());

        wfEngine.doAction(piid, 1);
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        wfEngine.doAction(piid, 2);
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 3, historySteps.size());
        
        // Most recent History Step
        Step step = (Step) historySteps.get(0);
        assertEquals("Unexpected last history step", 3, step.getStepId());
    }


    @Test
    public void testEarlyJoin() throws Exception {
        URL url = getClass().getResource("/core/earlyjoin.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());
        wfEngine.doAction(piid, 1, EMPTY_MAP);

        //we end up in step 3, with everything finished
        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 3, historySteps.size());

        Step step = (Step) historySteps.get(0);
        assertEquals("Unexpected last history step", 3, step.getStepId());
    }

    @Ignore
    public void testJoinNodesOrder() throws Exception {
        
        URL url = getClass().getResource("/core/joinorder.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1);
        
        // Also tests 'doAction' chaining
        wfEngine.doAction(piid, 2, null)
            .doAction(piid, 3, null)
            .doAction(piid, 2, null)
            .doAction(piid, 4, null)
            .doAction(piid, 6, null)
            .doAction(piid, 7, null)  // At some point we have to allow multiple join Step with the same StepId
       ;

    }

    @Test
    public void testSplitCompletedHistorySteps() throws Exception {
        URL url = getClass().getResource("/core/joinsplit.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100, null);
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        wfEngine.doAction(piid, 1, EMPTY_MAP);
        currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 2, currentSteps.size());

        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
        wfEngine.doAction(piid, 2, EMPTY_MAP);

        //check that the same action is no longer available
        List<Integer> actions = wfEngine.getAvailableActions(piid, EMPTY_MAP);
        assertEquals("Unexpected number of actions available", 1, actions.size());
        historySteps = wfEngine.getHistorySteps(piid);
        currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected number of history steps", 2, historySteps.size());
    }

    @Test
    public void testStepPostFunction() throws Exception {
        URL url = getClass().getResource("/core/step-post.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1, null);
        wfEngine.doAction(piid, 2, null);
        assertTrue("post-function was not called as expected", "postvalue".equals(wfEngine.getTypedMap(piid).getString("postkey")));
    }

    @Test
    public void testStepPreFunction() throws Exception {
        URL url = getClass().getResource("/core/step-pre.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1, null);
        assertTrue("pre-function was not called as expected", "prevalue".equals(wfEngine.getTypedMap(piid).getString("prekey")));
    }
}
