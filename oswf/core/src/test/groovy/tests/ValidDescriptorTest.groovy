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


class ValidDescriptorTest {
    
    
    // C O N S T R U C T O R S  ---------------------------------------------------------------
    private OSWfEngine wfEngine;

    @Before
    void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine();
    }

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Test whether an invalid default-result in an initial-actions is indeed rejected as it should.
     *
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-130">Jira issue WF-130</a>
     * @throws Exception If error while executing testing
     */

    @Test
    void checkResultInitialActionUnconditionalResult() throws Exception {
        
        try {

            URL url = getClass().getResource("/invalid/default-result.oswf.xml");
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(url.toString());
            descriptor.validate();

            fail("Descriptor loaded successfully, even though default-result element has missing step");

        } catch (WorkflowLoaderException workflowLoaderException) {
            assert  workflowLoaderException.message.contains('Error loading workflow: Result  is not a split or join, and has no next step')
        } catch (Exception exception) {
            fail("Descriptor failed to load as expected, but a ${exception.getClass()} exception was caught instead of WorkflowLoaderException");
        }
    }

    @Ignore
    void commonActionDuplicateID() throws Exception {

        try {
            URL url = getClass().getResource("/invalid/common-actions-dupid.oswf.xml");
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(url.toString());
            descriptor.validate();
            
            fail("Invalid common-actions not detected correctly");
        } catch (WorkflowLoaderException workflowLoaderException) {
            println workflowLoaderException
        }
    }

    /**
     * Tests whether common-actions are implemented correctly.
     *
     * @throws Exception If error while executing testing
     */

    @Ignore
    void commonActions() throws Exception {

        try {
            URL url = getClass().getResource("/core/common-actions.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 50);
        } catch (Exception e) {
            fail("Descriptor did not recognized common-actions!");
        }

        try {
            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(getClass().getResource("/invalid/common-actions-bad.oswf.xml").toString());
            descriptor.validate();
            fail("Invalid common-actions not detected correctly");
        } catch (WorkflowLoaderException workflowLoaderException) {
            println workflowLoaderException.message
        }
    }

    /**
     * Test whether a duplicate action is correctly marked as invalid
     *
     * @throws Exception If error while executing testing
     */

    @Test
    void duplicateActionID() throws Exception {

        try {

            WorkflowDescriptor descriptor = new URLLoader().getWorkflow(getClass().getResource("/invalid/duplicate-action.oswf.xml").toString());
            descriptor.validate();
            fail("descriptor loaded successfully, even though duplicate action exists");

        } catch (WorkflowLoaderException workflowLoaderException) {
            assert workflowLoaderException.message.contains('Duplicate occurance of action ID \'3\' found in step 3')
        } catch (Exception exception) {
            fail("descriptor failed to load as expected, but a ${exception.getClass()} exception was caught instead of WorkflowLoaderException");
        }
    }

    @Test
    void finish() throws Exception {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine();

        try {
            
            URL url = getClass().getResource("/core/finish-attribute.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 100);
            
            wfEngine.doAction(piid, 1);
            
        } catch (Exception exception) {
            fail("Finish attribute workflow should be valid, instead it failed with: ${exception}");
        }
    }

    /**
     * Test validator
     */
    @Test
    void validator() throws Exception {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("testuser");

        try {
            
            URL url = getClass().getResource("/invalid/validator.oswf.xml");
            long piid = wfEngine.initialize(url.toString(), 1);
            
            fail("Did not get expected InvalidInputException");

        } catch (InvalidInputException invalidInputException) {
            assert invalidInputException.message == 'Missing arg input1'
        } catch (Exception exception) {
            fail("descriptor failed to load as expected, but a ${exception.getClass()} exception was caught instead of WorkflowLoaderException");
        }

    }
}
