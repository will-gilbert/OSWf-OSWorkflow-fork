package tests;

import org.informagen.oswf.testing.OSWfTestCase;

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
 * For these tests no configuration is set. These tests will using the configuration
 *  found in the test classpath at /oswf.xml which uses memory base store and 
 *  property set.
 *
 * <pre>
 *   <persistence>
 *       <workflow-store class="org.informagen.oswf.impl.stores.MemoryStore"/>
 *       <propertyset-store class="org.informagen.oswf.impl.MemoryPropertySetStore"/>
 *  </persistence>
 * <pre>
 *
 *  The default URLLoader will be used to load the workflow definition using the
 *     URL string as the workflow name.
 */

public class PropertySetTest extends OSWfTestCase {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private OSWfEngine wfEngine;
    private URL url;

    //~ Setup //////////////////////////////////////////////////////////////////

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("testuser");
        url = getClass().getResource("/core/PropertySet.oswf.xml");        
    }

    //~ Tests ////////////////////////////////////////////////////////////////

    @Test
    public void setPropertySet() throws Exception {
        int initialAction = 1;
        long piid = wfEngine.initialize(url.toString(), initialAction);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

 
    // Testing 'HasProperyValue' condition using only a 'name' argument returning
    //   a boolean true if the 'name' exists, false otherwise.
    @Test
    public void propertySetValueExists() throws Exception {
        int initialAction = 2;
        long piid = wfEngine.initialize(url.toString(), initialAction);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }


    // Testing missing 'name' argument in 'HasProperyValue' condition; See
    //  the logger for an error caught by 'OSWfEngine'
    @Test
    public void nameNotDefined() throws Exception {
        
        int initialAction = 3;
        long piid = wfEngine.initialize(url.toString(), initialAction);

       // Workflow did not complete; check log to see the error
       assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);         
    }
   
   
    // Testing 'HasPropertyValue' condition with both name and value; checking
    //   if expected value mathes actual value; See workflow XML  
    @Test
    public void propertySetValueEquals() throws Exception {
        int initialAction = 4;
       long piid = wfEngine.initialize(url.toString(), initialAction);
       assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

}
