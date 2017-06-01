package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



/**
 * Test that when an auto execute action happens, the correct actions occur.
 *
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 9, 2003
 * Time: 10:26:48 PM
 */

public class AutoActionsTest  {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine;

    //~ Setup //////////////////////////////////////////////////////////////////

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("testuser");
    }

    //~ Tests ////////////////////////////////////////////////////////////////

    @Test
    public void autoWithSplit() throws Exception {
        
        URL url = getClass().getResource("/core/auto-split.oswf.xml");        
        long piid = wfEngine.initialize(url.toString(), 1);
        
        assertEquals("Unexpected number of current steps", 2, wfEngine.getCurrentSteps(piid).size());
        assertEquals("Unexpected number of history steps", 0, wfEngine.getHistorySteps(piid).size());

        wfEngine.doAction(piid, 21, EMPTY_MAP);
        wfEngine.doAction(piid, 11, EMPTY_MAP);

        assertEquals("Unexpected number of current steps", 0, wfEngine.getCurrentSteps(piid).size());
        assertEquals("Unexpected number of history steps", 4, wfEngine.getHistorySteps(piid).size());
    }


    @Test
    public void simpleAuto() throws Exception {

        URL url = getClass().getResource("/core/auto1.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);

        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 2, currentSteps.get(0).getStepId());
    }


    @Test
    public void execOnlyOne() throws Exception {
        
        URL url = getClass().getResource("/core/auto2.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);
        List<Step> historySteps = wfEngine.getHistorySteps(piid);
        
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 4, currentSteps.get(0).getStepId());

        
        assertEquals("Expected to have one history step", 1, historySteps.size());
        assertEquals("Unexpected history step", 2, historySteps.get(0).getStepId());
    }


    @Test
    public void execTwoActions() throws Exception {
        
        URL url = getClass().getResource("/core/auto3.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);
        List<Step> historySteps = wfEngine.getHistorySteps(piid);
        
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 3, currentSteps.get(0).getStepId());

        assertEquals("Expected to have two history steps", 2, historySteps.size());
        assertEquals("Unexpected first history step", 2, historySteps.get(0).getStepId());
        assertEquals("Unexpected second history step", 1, historySteps.get(1).getStepId());
    }

    @Test
    public void conditionCheck() throws Exception {
        
        URL url = getClass().getResource("/core/auto4.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);

        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 3, currentSteps.get(0).getStepId());
    }

    @Test
    public void propertySetCreated() throws Exception {
        
        Map<String,Object> inputs = new HashMap<String,Object>();
        List<String> list = new ArrayList<String>();
        inputs.put("list", list);

        URL url = getClass().getResource("/core/propertyset-create.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1, inputs);
        
        assertEquals("Unexpected property set value for key myvar", "anything", list.get(0));

        List<Step> currentSteps = wfEngine.getCurrentSteps(piid);
        
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, currentSteps.get(0).getStepId());
    }

    @Test
    public void simpleFinishWithRestriction() throws Exception {
        
        URL url = getClass().getResource("/core/finish1.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        wfEngine.doAction(piid, 1, null);
        
        assertTrue("Finished workflow should have no current steps", wfEngine.getCurrentSteps(piid).size() == 0);
        assertEquals("Unexpected workflow entry state", ProcessInstanceState.COMPLETE, wfEngine.getProcessInstanceState(piid));

        List historySteps = wfEngine.getHistorySteps(piid);

        //last history step should have status of LastFinished
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
    }

    @Test
    public void simpleFinishWithoutRestriction() throws Exception {
        
        URL url = getClass().getResource("/core/finish2.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 100);
        wfEngine.doAction(piid, 1, null);
        
        assertTrue("Finished workflow should have no current steps", wfEngine.getCurrentSteps(piid).size() == 0);
        assertEquals("Unexpected workflow entry state", ProcessInstanceState.COMPLETE, wfEngine.getProcessInstanceState(piid));

        List<Step> historySteps = wfEngine.getHistorySteps(piid);

        // last history step should have status of 'Last Finished'
        assertEquals("Unexpected number of history steps", 1, historySteps.size());
        assertEquals("Unexpected first history step", "Last Finished", historySteps.get(0).getStatus());
        
        
    }
}
