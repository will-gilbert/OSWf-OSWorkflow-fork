package usage;

import org.informagen.oswf.testing.OSWfTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.impl.stores.MemoryStore;

import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.propertyset.PropertySet;


// Java Util
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map;
import java.util.HashMap;

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

public class ValidatorTest extends OSWfTestCase {

    private final static String workflowFilename = "Validator";
    
    final static int INITIAL_ACTION  =   1;
    final static int FIRST_STEP       = 1;
        
    // Process-Instance variable
    protected long bobId;

    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
    
        wfEngine = new DefaultOSWfEngine("Unit Testing");
        
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/oswf-core.xml"));
        wfEngine.setConfiguration(config);    
    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }


    @Test
    public void stub()  {
    }


}
