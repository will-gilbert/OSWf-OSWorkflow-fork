package groovy.tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.Step
import org.informagen.oswf.ProcessInstance
import org.informagen.oswf.ProcessInstanceState

import org.informagen.oswf.impl.DefaultOSWfEngine

import java.net.URL

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map

import static java.util.Collections.EMPTY_MAP

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
 * Test that when an auto execute action happens, the correct actions occur.
 */

public class AutoActionsTest  {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine

    //~ Setup //////////////////////////////////////////////////////////////////

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("test user name")
    }

    //~ Tests ////////////////////////////////////////////////////////////////

    /**
    *  This workflow split and then runs to completion using the action 
    *   'auto' attribute set to true
    */

    @Test
    public void autoWithSplit() throws Exception {
        
        def url = getClass().getResource("/core/auto-split.oswf.xml")        
        def piid = wfEngine.initialize(url.toString(), 1)
        
        assert 0 == wfEngine.getCurrentSteps(piid).size()
        assert 4 == wfEngine.getHistorySteps(piid).size()
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }

    /**
    *   This workflow has a step with multiple actions with one of them (actionId=103) 
    *     with its auto attribute set to 'true', hence the other actions will be ignored.
    */

    @Test
    public void autoActionPriority() throws Exception {
        def url = getClass().getResource("/core/Auto-Action-Priority.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)

        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)

        def historySteps = wfEngine.getHistorySteps(piid)
        assert 2 == historySteps.size()
        assert 103 == historySteps[0].getActionId()
    }

    /**
    *  Step 200 has multiple actions all with their auto attribute 
    *   set to 'true', the first one in XML order will be executed.
    *
    *  In this case, action id=201 is executed the others are ignored.
    */


    @Test
    public void firstAutoActionOnly() throws Exception {
        
        def url = getClass().getResource("/core/Multiple-Auto-Actions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)

        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
       
        def historySteps = wfEngine.getHistorySteps(piid)
        assert   3 == historySteps.size()
        assert 201 == historySteps[0].getActionId()
    }


    @Test
    public void allAutoActions() throws Exception {
        
        URL url = getClass().getResource("/core/All-Auto-Actions.oswf.xml")

        long piid = wfEngine.initialize(url.toString(), 1)

        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
        assert  3 == wfEngine.getHistorySteps(piid).size()

    }

    @Test
    public void conditionCheck_Foo() throws Exception {
        
        URL url = getClass().getResource("/core/Conditional-Auto.oswf.xml")

        def inputs = [path:'Foo']
        def piid = wfEngine.initialize(url.toString(), 1, inputs)

        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)

        def lastStep = wfEngine.getHistorySteps(piid)[0]
        assert 101 == lastStep.getActionId()
        assert 'Foo' == wfEngine.getWorkflowDescriptor(url.toString()).getStep(lastStep.stepId).name
    }

    @Test
    public void conditionCheck_Bar() throws Exception {
        
        URL url = getClass().getResource("/core/Conditional-Auto.oswf.xml")

        def inputs = [path:'Bar']
        long piid = wfEngine.initialize(url.toString(), 1, inputs)

        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)

        def lastStep = wfEngine.getHistorySteps(piid)[0]
        assert 102 == lastStep.getActionId()
        assert 'Bar' == wfEngine.getWorkflowDescriptor(url.toString()).getStep(lastStep.stepId).name
    }

    @Test
    public void copyToTransient() throws Exception {
        
        // Pass in an ArrayList as the input variable, 'list'
        def list = []
        def inputs = [:]
        inputs.put("list", list)

        def url = getClass().getResource("/core/Copy-To-Transient.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1, inputs)

        // The process has all automatic actions and saves some variables to the 'list'
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
        assert 3 == list.size()
        assert 'anything' ==  list[0]
        assert 'foo' ==  list[1]
        assert 'bar' ==  list[2]
    }

    @Test
    public void simpleFinish() throws Exception {
        
        def url = getClass().getResource("/core/Simple-Finish.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)
        wfEngine.doAction(piid, 101)
        
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)

        // The final history step should have status of 'Last Finished'
        def historySteps = wfEngine.getHistorySteps(piid)
        assert  1 == historySteps.size()
        assert 'Last Finished' == historySteps[0].status
    }
        

    @Test
    public void finishWithRestriction() throws Exception {
        
        def url = getClass().getResource("/core/Finish-With-Restriction.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)
        wfEngine.doAction(piid, 101)
        
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)

        List historySteps = wfEngine.getHistorySteps(piid)

        // The final history step should have status of 'LastFinished'
        assert  1 == historySteps.size()
        assert 'Last Finished' == historySteps[0].status
      
    }
}
