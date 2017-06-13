package tests;

import org.informagen.oswf.testing.OSWfTestCase;

//OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf - Store
import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 */
 
 
public class StubTypeResolverTest extends OSWfTestCase {

    // Initial actions
    final static int ACTION_CREATE_ISSUE   = 1;
    
    // Common actions
    final static int ACTION_CLOSE_ISSUE    = 2;
    final static int ACTION_REOPEN_ISSUE   = 3;
    final static int ACTION_START_PROGRESS = 4;
    final static int ACTION_RESOLVE_ISSUE  = 5;
    
    // Step actions
    final static int ACTION_STOP_PROGRESS        = 301;
    final static int ACTION_CLOSE_RESOLVED_ISSUE = 701;
    

    // Static fields/initializers /////////////////////////////////////////////
    
    // Create a static logger to be used by the @BeforeClass method(s), once
    //   the test class is instance pass a reference to the OSWfTestCase logger

    private static final Logger testlogger = LoggerFactory.getLogger(StubTypeResolverTest.class);

    private static final String WORKFLOW_NAME = "JIRA-Default";  // workflows/resources/core/JIRA-Default.oswf.xml"

    static OSWfConfiguration configuration;    

    @BeforeClass
    public static void createConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(StubTypeResolverTest.class.getResource("/oswf-StubTypeResolver.xml"))
        ;
    }


    // Constructors ///////////////////////////////////////////////////////////

    public StubTypeResolverTest() {
        super(testlogger);
    }

    @After
    public void teardown() {
        MemoryWorkflowStore.reset();
    }

    // Tests ====================================================================
    
    @Test
    public void createIssue() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("SimulatorTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize(WORKFLOW_NAME, ACTION_CREATE_ISSUE));
    }

    @Test
    public void executeWorkflow() throws Exception {
 
        OSWfEngine wfEngine = new DefaultOSWfEngine("SimulatorTest");
        wfEngine.setConfiguration(configuration);
        assertNotNull(wfEngine);
       
        long piid = wfEngine.initialize(WORKFLOW_NAME, ACTION_CREATE_ISSUE);
    
        // Counts: History Steps, Current Steps, Actions

        // Step 1: Open
        assertCounts(wfEngine, piid, 0, 1, 3);       
        chooseAction(wfEngine, piid, ACTION_START_PROGRESS);
    
        // Step 3: In Progress
        assertCounts(wfEngine, piid, 1, 1, 3);       
        chooseAction(wfEngine, piid, ACTION_STOP_PROGRESS);

        // Step 1: Open
        assertCounts(wfEngine, piid, 2, 1, 3);       
        chooseAction(wfEngine, piid, ACTION_CLOSE_ISSUE);

        // Step 6: Closed
        assertCounts(wfEngine, piid, 3, 1, 1);       
        chooseAction(wfEngine, piid, ACTION_REOPEN_ISSUE);

        // Step 5: Reopened
        assertCounts(wfEngine, piid, 4, 1, 3);       
        chooseAction(wfEngine, piid, ACTION_RESOLVE_ISSUE);

        // Step 4: Resolved
        assertCounts(wfEngine, piid, 5, 1, 2);       
        chooseAction(wfEngine, piid, ACTION_CLOSE_RESOLVED_ISSUE);

        // Step 6: Closed
        assertCounts(wfEngine, piid, 6, 1, 1);       
        
    }



}
