package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf Exceptions
import org.informagen.oswf.exceptions.InvalidActionException;
import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.exceptions.WorkflowLoaderException;



// JUnit 4.x testing
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

class ExceptionTest  {

    private static final int INVALID_INITIAL_ACTION = 2
    private static final int START_WORKFLOW         = 100
    private static final int INVALID_STEP_ACTION    = 10


    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    void initializeInvalidActionException() {

        def wfEngine = new DefaultOSWfEngine();
        def url = getClass().getResource("/core/auto1.oswf.xml");
        assert  url

        try {
            wfEngine.initialize(url.toString(), INVALID_INITIAL_ACTION);
            fail("Expected InvalidActionException but did not get one for a bad action in initialize");
        } catch (InvalidActionException invalidActionException) {
            assert invalidActionException.message.contains('Invalid initial-action = 2')
        }

    }

    @Test
    void invalidActionException() {

        def wfEngine = new DefaultOSWfEngine();
        def url = getClass().getResource("/core/auto1.oswf.xml");
        assert  url

        def piid = wfEngine.initialize(url.toString(), START_WORKFLOW);

        try {
            wfEngine.doAction(piid, INVALID_STEP_ACTION);
            fail("Expected InvalidActionException but did not get one for a bad step action");
        } catch (InvalidActionException invalidActionException) {
            assert invalidActionException.message.contains('Action 10 was not found')
        }

    }

    @Ignore
    void workflowStoreException() {
        
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/invalid/invalid-datasource.xml"));

        OSWfEngine wfEngine = new DefaultOSWfEngine();
        wfEngine.setConfiguration(config);

        // Correct behaviour is to get a store exception since we can't look up the DS
        def url = getClass().getResource("/core/auto1.oswf.xml");
        assert url

        try {
            wfEngine.initialize(url.toString(), START_WORKFLOW);
            fail("Expected WorkflowStoreException but was not thrown for a bad JDBC datasource");
        } catch (WorkflowStoreException workflowStoreException) {
            println workflowStoreException
        } catch (Exception exception) {
            println exception
        }

    }
}
