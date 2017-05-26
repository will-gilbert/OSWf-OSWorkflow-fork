package usage;

import org.informagen.oswf.testing.OSWfTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.impl.stores.MemoryStore;

import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;

// Java Util
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



public class VariableModifyTest extends OSWfTestCase {

    private final static String workflowFilename = "Variable-Modify";
    
    final static int INITIAL_ACTION  =   100;
    final static int FIRST_STEP      = 1;
        
    // Process-Instance variable
    protected long bobId;
    
    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
        
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-usage.xml"));

        wfEngine.setConfiguration(config);    
            

    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }

    /*
     * The assertion is invoked by the FunctionProvider: 'VerifyArg'
     */

    @Test
    public void variableModify()  {
        bobId = createProcessInstance(wfEngine, workflowFilename, INITIAL_ACTION);
        
        //Get final status
        assertProcessInstanceState(wfEngine, bobId, ProcessInstanceState.COMPLETE);
        
        
        //Get final value of the 'message' transient variable
        assertProperty(wfEngine, bobId, "message", "bar");
    }


}
