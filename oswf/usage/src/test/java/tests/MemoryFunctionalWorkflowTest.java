package tests;

import org.informagen.oswf.impl.stores.MemoryWorkflowStore;
import tests.AbstractFunctionalWorkflow;


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
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.
 *
 * Uses in-memory persistance
 *
 */

public class MemoryFunctionalWorkflowTest extends AbstractFunctionalWorkflow {


    // M E T H O D S  -------------------------------------------------------------------------
    
    @Before
    public void setup() throws Exception {
        MemoryWorkflowStore.reset();
        super.setup();
    }
}
