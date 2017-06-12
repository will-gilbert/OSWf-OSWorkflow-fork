/**
 * 
 */package tests;

import org.informagen.oswf.testing.OSWfTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.TypeResolver;
import org.informagen.oswf.OSWfConfiguration;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.exceptions.WorkflowLoaderException;

import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;
import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

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

public class TypeResolverTest extends OSWfTestCase {

    private final static String workflowFilename = "TypeResolver Test";
     
    @After
    public void teardown() {
        MemoryWorkflowStore.reset();
    }

    @Test
    public void fullPackagePath() throws WorkflowLoaderException  {

        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-core.xml"))
            )
        ;

        long piid = createProcessInstance(wfEngine, workflowFilename, 1);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

    @Test
    public void addNullFunctionProgramatically() throws WorkflowLoaderException {

        OSWfConfiguration configuration = new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-core.xml"))
        ;
        
        configuration.getTypeResolver().addFunctionAlias("NullFunction", "tests.util.NullFunction");
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(configuration)
        ;
        
        long piid = createProcessInstance(wfEngine, workflowFilename, 2);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

    @Test
    public void useTypeResolverAPI_NullFunction() throws WorkflowLoaderException {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-TypeResolver.xml"))
            )
        ;

        //config.getTypeResolver().addFunction("NullFunction", "tests.util.NullFunction");
        
        long piid = createProcessInstance(wfEngine, workflowFilename, 2);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

    @Test
    public void useTypeResolverAPI_X() throws WorkflowLoaderException {

        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-TypeResolver.xml"))
            )
        ;
        
        //TypeResolver.getInstance().addFunction("X", "tests.util.NullFunction");
        
        long piid = createProcessInstance(wfEngine,workflowFilename, 3);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

    @Test
    public void verifyArg() throws WorkflowLoaderException {

        
        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-TypeResolver.xml"))
            )
        ;
                
        long piid = createProcessInstance(wfEngine, workflowFilename, 4);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }

    @Test
    public void fakeVerifyArgWithNullFunction() throws WorkflowLoaderException {

        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-TypeResolver.xml"))
            )
        ;
                
        long piid = createProcessInstance(wfEngine, workflowFilename, 4);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }


    @Test
    public void hasStatusOf() throws WorkflowLoaderException {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-core.xml"))
            )
        ;

        long piid = createProcessInstance(wfEngine, workflowFilename, 5);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }


    @Test
    public void hasStatusOf_MultipleImplementations() throws WorkflowLoaderException {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-core.xml"))
            )
        ;

        long piid = createProcessInstance(wfEngine, workflowFilename, 6);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }


    // Stub out built-in Functions and Condtions
    // This technique is very useful for testing workflows!

    @Test
    public void hasStatusOf_AlwaysTrue() throws WorkflowLoaderException {

        OSWfEngine wfEngine = new DefaultOSWfEngine("TypeResolverTest")
            .setConfiguration(new MemoryOSWfConfiguration()
                .load(getClass().getResource("/oswf-TypeResolver.xml"))
            )
        ;

        long piid = createProcessInstance(wfEngine, workflowFilename, 7);
        assertProcessInstanceState(wfEngine, piid, ProcessInstanceState.COMPLETED);        
    }



}
