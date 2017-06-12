package tests;


import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.UserOSWfEngine;

import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.exceptions.QueryNotSupportedException;
import org.informagen.oswf.exceptions.WorkflowException;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf - Store
import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
import java.util.Collections;
import java.util.Date;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 */
 
 
public class DefaultLoaderTest extends OSWfTestCase {

    private static final Logger testlogger = LoggerFactory.getLogger(DefaultLoaderTest.class);

    static OSWfConfiguration configuration;    

    @BeforeClass
    public static void createConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(StubTypeResolverTest.class.getResource("/oswf-core.xml"))
        ;
    }


    // Constructors ///////////////////////////////////////////////////////////

    public DefaultLoaderTest() {
        super(testlogger);
    }

    @After
    public void teardown() {
        MemoryWorkflowStore.reset();
    }

    // Tests ====================================================================
    
    @Test
    public void readXMLasResource() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("XMLLoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday as resource", 1));
    }
    
    @Test
    public void readXMLasURL() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("XMLLoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday as URL", 1));
    }

    
    @Ignore
    public void readXMLasFile() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("XMLLoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday as file", 1));
    }


}
