package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.loaders.URLLoader;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Exceptions
import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

import java.util.Collections;
import java.util.HashMap;
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


public class ValidDescriptorTest {
    
    
    // C O N S T R U C T O R S  ---------------------------------------------------------------
    private OSWfEngine wfEngine;

    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("testuser");
    }

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Test whether an invalid default-result in an initial-actions is indeed rejected as it should.
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-130">Jira issue WF-130</a>
     * @throws Exception If error while executing testing
     */

    @Test
    public void checkResultInitialActionUnconditionalResult() throws Exception {
        
        try {
            URL url = getClass().getResource("/core/invalid/default-result.oswf.xml");
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(url.toString());
            descriptor.validate();

            fail("descriptor loaded successfully, even though default-result element is incorrect");
        } catch (WorkflowLoaderException e) {
            //the default-result is missing in descriptor, which is what we are testing here
            assertTrue(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of WorkflowLoaderException");
        }
    }

    @Test
    public void commonActionDuplicateID() throws Exception {
        try {
            URL url = getClass().getResource("/core/invalid/common-actions-dupid.oswf.xml");
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(url.toString());
            descriptor.validate();
            
            fail("Invalid common-actions not detected correctly");
        } catch (WorkflowLoaderException e) {
            assertTrue(true);
        }
    }

    /**
     * Tests whether common-actions are implemented correctly.
     *
     * @throws Exception If error while executing testing
     */
    @Test
    public void commonActions() throws Exception {

        try {
            URL url = getClass().getResource("/core/common-actions.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 50);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Descriptor did not recognized common-actions!");
        }

        try {
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(getClass().getResource("/core/invalid/common-actions-bad.oswf.xml").toString());
            descriptor.validate();
            fail("Invalid common-actions not detected correctly");
        } catch (WorkflowLoaderException e) {
            assertTrue(true);
        }
    }

    /**
     * Test whether a duplicate action is correctly marked as invalid
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-192">Jira issue WF-192</a>
     * @throws Exception If error while executing testing
     */

    @Test
    public void duplicateActionID() throws Exception {
        try {
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(getClass().getResource("/core/invalid/duplicate-action.oswf.xml").toString());
            descriptor.validate();
            fail("descriptor loaded successfully, even though duplicate action exists");
        } catch (WorkflowLoaderException e) {
            //the descriptor is invalid, which is correct
            assertTrue(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of WorkflowLoaderException");
        }
    }

    @Test
    public void finish() throws Exception {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");

        try {
            
            URL url = getClass().getResource("/core/finish-attribute.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 100);
            
            wfEngine.doAction(piid, 1);
            
        } catch (Exception e) {
            fail("finish attribute workflow should be valid, instead it failed with: " + e);
            return;
        }
    }

    /**
     * Test validator
     */
    @Test
    public void validator() throws Exception {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");

        try {
            
            URL url = getClass().getResource("/core/validator.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 1);
            
            fail("Did not get expected InvalidInputException");
        } catch (InvalidInputException e) {
            //the descriptor is invalid, which is correct
            assertTrue(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("descriptor failed to load as expected, but a " + ex.getClass() + " exception was caught instead of WorkflowLoaderException");
        }

    }
}
