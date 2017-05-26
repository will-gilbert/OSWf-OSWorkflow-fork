package usage;

import org.informagen.oswf.testing.OSWfTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.exceptions.InvalidInputException;

import org.informagen.oswf.impl.stores.MemoryStore;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

// Java Util
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *       
 *      To change the Logging levels edit the resource file 'log4j.properties'
 *         and set the following entry
 *      
 *          log4j.logger.usage.LoggingTest           ALL
 */

public class LoggingTest extends OSWfTestCase implements Constants {


    // Instance variable
    private long piid;
    private OSWfEngine wfEngine;

    public LoggingTest() {
        super(LoggerFactory.getLogger("usage.LoggingTest"));
    }


    @Before
    public void setup() throws Exception {
    
        // "Unit Testing" is the system which will owns all of the workflow instances
        //   created with 'createProcessInstance'
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
                
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));
        wfEngine.setConfiguration(config);
    
        logger.debug("Create 'LogRegister' process instance" + " =========================");
        piid = wfEngine.initialize("LogRegister", INITIAL_ACTION);

    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }


    @Test
    public void logger() {
       logger.debug("Log message from Unit test: Debug");
       logger.info("Log message from Unit test: Info");
       logger.error("Log message from Unit test: Error");
    }


    @Test
    public void allowRequest() throws Exception {

        // Verify intial state
        assertEquals(ProcessInstanceState.ACTIVE, wfEngine.getProcessInstanceState(piid));

        int action = REQUEST_HOLIDAYS;
        logger.debug("doAction: " + action + " =========================");
        wfEngine.doAction(piid, action);

        action = LINE_MANAGER_APPROVES;
        logger.debug("doAction: " + action + " =========================");
        wfEngine.doAction(piid, action);

        // Ensure successful completion
        assertProperty(wfEngine, piid, "result", "approved");
        assertEquals(ProcessInstanceState.COMPLETE, wfEngine.getProcessInstanceState(piid));
    }
    @Test
    public void denyRequest() throws Exception {

        // Verify intial state
        assertEquals(ProcessInstanceState.ACTIVE, wfEngine.getProcessInstanceState(piid));

        int action = REQUEST_HOLIDAYS;
        logger.debug("doAction: " + action + " =========================");
        wfEngine.doAction(piid, action);

        action = LINE_MANAGER_DENIES;
        logger.debug("doAction: " + action + " =========================");
        wfEngine.doAction(piid, action);

        // Ensure successful completion
        assertProperty(wfEngine, piid, "result", "denied");
        assertEquals(ProcessInstanceState.COMPLETE, wfEngine.getProcessInstanceState(piid));
    }

}
