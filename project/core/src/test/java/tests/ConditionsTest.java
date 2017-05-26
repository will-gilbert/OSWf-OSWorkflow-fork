package tests;

import org.informagen.oswf.testing.OSWfTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;


import org.informagen.oswf.impl.DefaultOSWfEngine;

import java.util.List;

import java.net.URL;

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
 * @author hani Date: May 5, 2004 Time: 8:47:45 PM
 */
public class ConditionsTest extends OSWfTestCase {
    
    // M E T H O D S  -------------------------------------------------------------------------

    OSWfEngine wfEngine;

    @Before
    public void setup() {
        wfEngine = new DefaultOSWfEngine("test");
    }


    @Test
    public void nestedCondition() throws Exception {
        
        URL url = getClass().getResource("/core/nested-condition.oswf.xml");
        
        long piid = wfEngine.initialize(url.toString(), 1, null);

        List<Integer> availableActions = wfEngine.getAvailableActions(piid, null);

        assertEquals("Unexpected number of available actions", 1, availableActions.size());
        assertEquals("Unexpected available action", 2, availableActions.get(0).intValue());

    }

    @Test
    public void orCondition() throws Exception {
        URL url = getClass().getResource("/core/boolean-conditions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1, null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);        

    }

    @Test
    public void failedOrCondition() throws Exception {
        URL url = getClass().getResource("/core/boolean-conditions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 6, null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);        
    }
    
    @Test
    public void andCondition() throws Exception {
        URL url = getClass().getResource("/core/boolean-conditions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 2, null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);        
    } 
    
    @Test
    public void failedAndCondition() throws Exception {
        URL url = getClass().getResource("/core/boolean-conditions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 7, null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.ACTIVE);        
    } 
    
    @Test
    public void notCondition() throws Exception {
        URL url = getClass().getResource("/core/boolean-conditions.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 3, null);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETE);        
    }



}
