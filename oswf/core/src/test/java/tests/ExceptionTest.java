package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;


import java.net.URL;

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

public class ExceptionTest  {


    // M E T H O D S  -------------------------------------------------------------------------

    @Ignore
    public void badWorkflowLoaderException() {

        // We expect an WorkflowLoaderException (can't throw a checked exception in constructor, otherwise the ejb provider
        //will break spec by having a constructor

        try {
            OSWfConfiguration config = new MemoryOSWfConfiguration();
            config.load(getClass().getResource("/oswf-badfactory.oswf.xml"));
        } catch (WorkflowLoaderException ex) {
            assertTrue("Expected WorkflowLoaderException, but instead got " + ex.getRootCause(), ex.getRootCause() instanceof WorkflowLoaderException);
        }

        fail("bad factory did not throw an error");
    }

    @Test
    public void initializeInvalidActionException() throws Exception {
        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");
        URL url = getClass().getResource("/core/auto1.oswf.xml");
        assertNotNull("Unable to find resource '/core/auto1.oswf.xml'", url);

        try {
            wfEngine.initialize(url.toString(), 2);
        } catch (InvalidActionException e) {
            return;
        }

        fail("Expected InvalidActionException but did not get one for a bad action in initialize");
    }

    @Test
    public void invalidActionException() throws Exception {
        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");
        URL url = getClass().getResource("/core/auto1.oswf.xml");
        assertNotNull("Unable to find resource '/core/auto1.oswf.xml'", url);

        long piid = wfEngine.initialize(url.toString(), 100);

        try {
            wfEngine.doAction(piid, 10, null);
        } catch (InvalidActionException e) {
            return;
        }

        fail("Expected InvalidActionException but did not get one for a bad action");
    }

    @Test
    public void testWorkflowStoreException() throws Exception {
        
        OSWfConfiguration config = new MemoryOSWfConfiguration();
        config.load(getClass().getResource("/core/invalid/invalid-datasource.oswf.xml"));

        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");
        wfEngine.setConfiguration(config);

        // Correct behaviour is to get a store exception since we can't look up the DS
        URL url = getClass().getResource("/core/auto1.oswf.xml");
        assertNotNull("Unable to find resource '/core/auto1.oswf.xml'", url);

        try {
            wfEngine.initialize(url.toString(), 100);
        } catch (WorkflowStoreException e) {
            return;
        } catch (Exception exception) {
            return;
        }

        fail("Expected WorkflowStoreException but was not thrown for a bad JDBC datasource");
    }
}
