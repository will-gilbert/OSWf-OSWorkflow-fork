package tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.ProcessInstanceState


import org.informagen.oswf.impl.DefaultOSWfEngine

import java.util.List

// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test


public class GroovyScriptConditionsTest {
    
    // M E T H O D S  -------------------------------------------------------------------------

    OSWfEngine wfEngine

    @Before
    public void setup() {
        wfEngine = new DefaultOSWfEngine("test user name")
    }


    @Test
    public void nestedCondition() throws Exception {
        
        def url = getClass().getResource("/core/groovyscript/nested-condition.oswf.xml")
        
        def piid = wfEngine.initialize(url.toString(), 1)

        List<Integer> availableActions = wfEngine.getAvailableActions(piid)

        assert 1 == availableActions.size()
        assert 2 == availableActions.get(0).intValue()
    }

    @Test
    public void orCondition() throws Exception {
        def url = getClass().getResource("/core/groovyscript/boolean-conditions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1)

        assert 'passed' == wfEngine.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }

    @Test
    public void failedOrCondition() throws Exception {
        def url = getClass().getResource("/core/groovyscript/boolean-conditions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 6)

        assert 'failed' == wfEngine.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
    }
    
    @Test
    public void andCondition() throws Exception {
        def url = getClass().getResource("/core/groovyscript/boolean-conditions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 2)

        assert 'passed' == wfEngine.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    } 
    
    @Test
    public void failedAndCondition() throws Exception {
        def url = getClass().getResource("/core/groovyscript/boolean-conditions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 7)

        assert 'failed' == wfEngine.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
    } 
    
    @Test
    public void notCondition() throws Exception {
        def url = getClass().getResource("/core/groovyscript/boolean-conditions.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 3)

        assert 'passed' == wfEngine.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
   }



}
