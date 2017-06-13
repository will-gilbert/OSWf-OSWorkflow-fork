package tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.impl.DefaultOSWfEngine
import org.informagen.oswf.Step


import java.net.URL

import java.util.List

import static java.util.Collections.EMPTY_MAP

// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail


public class StepsTest {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("StepsTest")
        assert wfEngine
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void andJoin() throws Exception {
    
        def url = getClass().getResource("/core/And-Join.oswf.xml")

        def piid = wfEngine.initialize(url.toString(), 100)

        // Initial action steps into a split and creates two steps and no history
        assert 2 == wfEngine.getCurrentSteps(piid).size()
        assert 0 == wfEngine.getHistorySteps(piid).size()

        // Execute one of the two split steps; It waits at the AND Join
        wfEngine.doAction(piid, 1)
        assert 1 == wfEngine.getCurrentSteps(piid).size()
        assert 1 == wfEngine.getHistorySteps(piid).size()

        // Execute the other the split step. The join is satisfied and will
        //  procced to step 3, which has no results so the workflow finished
        wfEngine.doAction(piid, 2)
        assert 0 == wfEngine.getCurrentSteps(piid).size()
        assert 3 == wfEngine.getHistorySteps(piid).size()
        
        // Most recent History Step's Id and this process instance (piid) final state
        def step = wfEngine.getHistorySteps(piid)[0]
        assert 3 == step.stepId
        assert 'Finished' == wfEngine.getWorkflowDescriptor(url.toString()).getStep(step.stepId).name
        
        // The final state for this process instance
        assert 'COMPLETED' == wfEngine.getProcessInstanceState(piid).name()
    }


    @Test
    public void orJoin() throws Exception {

        def url = getClass().getResource("/core/Or-Join.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 100)

        // Initial action steps into a split and creates two steps and no history
        assert 2 == wfEngine.getCurrentSteps(piid).size()
        assert 0 == wfEngine.getHistorySteps(piid).size()

        // Execute one of the two split steps; It arrives at the OR Join and proceeds
        wfEngine.doAction(piid, 1)

        // We end up in step 3, with everything finished; The remaining step in the 
        //  split was removed by the engine
        assert 0 == wfEngine.getCurrentSteps(piid).size()
        assert 3 == wfEngine.getHistorySteps(piid).size()

        // Most recent History Step's Id and this process instance (piid) final state
        def step = wfEngine.getHistorySteps(piid)[0]
        assert 3 == step.stepId
        assert 'Finished' == wfEngine.getWorkflowDescriptor(url.toString()).getStep(step.stepId).name
        
        // The final state for this process instance
        assert 'COMPLETED' == wfEngine.getProcessInstanceState(piid).name()

    }

    @Test
    public void reenterSplitWitoutClear() throws Exception {

        // Currently, OSWf will allow duplicate currentSteps.  Is this correct?
        // Poposal: When a creates a duplicate current step it replace any existing current steps.
        
        def url = getClass().getResource("/core/ReenterSplitWitoutClear.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)
        
        // Run the workflow to the end
        wfEngine.with {
            doAction piid, 2   // Finish First Part; Split Steps 2 & 3
            doAction piid, 3   // Step 2, back to Step 1
            doAction piid, 2   // Finished First Part; Split; 3 current steps: 2, 3, 3
            doAction piid, 4   // Finish Step 2
            doAction piid, 6   // Finish Step 3, one of the steps
            doAction piid, 6   // Finish Step 3, the other step
        }

        assert 2 == wfEngine.getCurrentSteps(piid).size()
        assert 6 == wfEngine.getHistorySteps(piid).size()

        // The final state for this process instance
        assert 'ACTIVE' == wfEngine.getProcessInstanceState(piid).name()
        
    }

    @Test
    public void reenterSplitWithClear() throws Exception {
        
        def url = getClass().getResource("/core/ReenterSplitWithClear.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)
        
        // Run the workflow to the end
        wfEngine.with {
            doAction piid, 2   // Entry Step; Split to Steps 2 & 3
            doAction piid, 3   // Step 2, repeat back to Step 1
            doAction piid, 2   // Split again; Only 2 current steps: 2, 3
            doAction piid, 4   // Finish Step 2
            doAction piid, 6   // Finish Step 3, one of the steps
        }

        assert 0 == wfEngine.getCurrentSteps(piid).size()
        assert 6 == wfEngine.getHistorySteps(piid).size()

        // The final state for this process instance
        assert 'COMPLETED' == wfEngine.getProcessInstanceState(piid).name() 
    }

    /** 
    ** This workflow will set a persistent variable 'prekey' to 'prevalue'.
    **
    ** The variable will be set BEFORE the first step is entered therefore
    **   there will be one current step, the only one in the workflow and
    **   no history steps.
    */

    @Test
    public void stepPreFunction() throws Exception {


        def url = getClass().getResource("/core/step-pre.oswf.xml")

        def piid = wfEngine.initialize(url.toString(), 1, null)

        assert 1 == wfEngine.getCurrentSteps(piid).size()
        assert 0 == wfEngine.getHistorySteps(piid).size()

        assert 'prevalue' ==  wfEngine.getPersistentVars(piid).getString('prekey')
    }

    /**
    ** This workflow will set a persistent variable 'postkey' to 'postvalue'
    **
    ** The variable will be set BEFORE the step has been exited but before
    **   the second step has been entered. The second step terminates the workflow.
    **   Therefore, there will be no current steps and two history steps.
    */

    @Test
    public void stepPostFunction() throws Exception {

        // 
        def url = getClass().getResource("/core/step-post.oswf.xml")

        def piid = wfEngine.initialize(url.toString(), 1, null)
        wfEngine.doAction(piid, 2, null)

        assert 0 == wfEngine.getCurrentSteps(piid).size()
        assert 2 == wfEngine.getHistorySteps(piid).size()

        assert 'postvalue' == wfEngine.getPersistentVars(piid).getString('postkey')
    }
}
