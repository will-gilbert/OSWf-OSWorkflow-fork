package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;

// OSWf - SPI
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf Exception
import org.informagen.oswf.exceptions.InvalidActionException;


import java.net.URL;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
 * @author hani Date: Apr 24, 2004 Time: 12:35:01 PM
 */
public class CommonAndGlobalActionsTest  {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("test");
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void testBasicCommonAction() throws Exception {
        
        URL url = getClass().getResource("/core/common-actions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 50, null);
        
        assertEquals("Unexpected workflow state", ProcessInstanceState.ACTIVE, wfEngine.getProcessInstanceState(piid));

        // Verify that a common action (Restart Workflow) can be called
        wfEngine.doAction(piid, 100, null);

        List<Step> historySteps = wfEngine.getHistorySteps(piid);
        Step historyStep = historySteps.get(0);
        assertEquals("Unexpected exit status set", "Restarted", historyStep.getStatus());

        // Now let's move to step 2
        wfEngine.doAction(piid, 1, null);

        // Now let's check if we can call a non-specified common action (100)
        try {
            wfEngine.doAction(piid, 100, null);
            fail("Should not be able to call non-explicitly specified common-action");
        } catch (InvalidActionException e) {
            assertTrue(true);
        }

        // Now test -1 stepId stuff. What is the -1 stuff?

        wfEngine.doAction(piid, 101, null);
        historySteps = wfEngine.getHistorySteps(piid);
        historyStep = historySteps.get(0);
        assertEquals("Unexpected old status set", "Finished", historyStep.getStatus());
    }

    @Test
    public void testBasicGlobalAction() throws Exception {
        
        URL url = getClass().getResource("/core/global-actions.oswf.xml");
        
        long piid = wfEngine.initialize(url.toString(), 50, null);

        List<Integer> availableActions = wfEngine.getAvailableActions(piid, null);
        List<Integer> expectedActions = Arrays.asList(100, 101, 1);
        assertEquals("Unexpected available actions ", expectedActions, availableActions );

        //verify that a global action can be called
        wfEngine.doAction(piid, 100, null);

        List<Step> historySteps = wfEngine.getHistorySteps(piid);
        Step historyStep = historySteps.get(0);
        assertEquals("Unexpected old status set", "Restarted", historyStep.getStatus());

        //now let's move to step 2
        wfEngine.doAction(piid, 1, null);

        // now test -1 stepId stuff.
        wfEngine.doAction(piid, 101, null);
        historySteps = wfEngine.getHistorySteps(piid);
        historyStep = (Step) historySteps.get(0);
        assertEquals("Unexpected old status set", "Finished", historyStep.getStatus());
    }

}
