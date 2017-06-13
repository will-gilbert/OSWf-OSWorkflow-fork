package tests

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
 * For these tests no configuration needs to be set. These tests will using the default
 *  configuration using in-memory process instance store and persistent variable store.
 *
 * <pre>
 *   <persistence>
 *       <workflow-store class="org.informagen.oswf.impl.stores.MemoryWorkflowStore"/>
 *       <propertyset-store class="org.informagen.oswf.impl.MemoryPeristentVarsStore"/>
 *  </persistence>
 * <pre>
 *
 *  The default URLLoader will be used to load the workflow definition using the
 *     URL string as the workflow name.
 */

public class PersistentVariablesTest {

    //  I N S T A N C E   F I E L D S  =========================================

    private def wfEngine
    private def url

    // B E F O R E  ============================================================

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("testuser")
        url = getClass().getResource("/core/PersistentVariables.oswf.xml")        
    }


    // T E S T S  ==============================================================

    @Test
    public void setPropertySet() throws Exception {
        def piid = wfEngine.initialize(url.toString(), 1)
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }

 
    // Testing 'HasProperyValue' condition using only a 'name' argument returning
    //   a boolean true if the 'name' exists, false otherwise.

    @Test
    public void propertySetValueExists() throws Exception {
        long piid = wfEngine.initialize(url.toString(), 2)
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }


    // Testing missing 'name' argument in 'HasProperyValue' condition See
    //  the logger for an error caught by 'OSWfEngine'

    @Test
    public void nameNotDefined() throws Exception {
        def piid = wfEngine.initialize(url.toString(), 3)

        // Workflow did not complete check log to see the error
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
    }
   
   
    // Testing 'HasPropertyValue' condition with both name and value checking
    //   if expected value mathes actual value See workflow XML 

    @Test
    public void propertySetValueEquals() throws Exception {
        def piid = wfEngine.initialize(url.toString(), 4)
        assert ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }

}
