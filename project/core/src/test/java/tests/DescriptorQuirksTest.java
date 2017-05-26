package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

import java.net.URL;

import java.util.HashMap;
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


/**
 * DOCUMENT ME!
 */
public class DescriptorQuirksTest  {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    public int counter = 0;
    private OSWfEngine wfEngine;

    // C O N S T R U C T O R S  ---------------------------------------------------------------
    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine("testuser");
    }


    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Test if XML comments (<!-- -->) in args are correctly ignored by script processors
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-178">Jira issue WF-178</a>
     * @throws Exception If error while executing testing
     */
 
    @Test
    public void testArgComment() throws Exception {

        Map<String,Object> inputs = new HashMap<String,Object>();
        inputs.put("test", this);

        counter = 0;

        URL resource = getClass().getResource("/core/comment-arg.oswf.xml");
        long piid = wfEngine.initialize(resource.toString(), 1, inputs);
        assertEquals("beanshell script not parsed correctly", 2, counter);
    }

    /**
     * Test if multiline XML comments (<![CDATA[ ]]>) are correctly ignored by script processors
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-178">Jira issue WF-178</a>
     * @throws Exception If error while executing testing
     */

    @Test
    public void testArgMultiText() throws Exception {

        Map<String,Object> inputs = new HashMap<String,Object>();
        inputs.put("test", this);
        
        counter = 0;

        URL resource = getClass().getResource("/core/multitext-arg.oswf.xml");
        long piid = wfEngine.initialize(resource.toString(), 1, inputs);
        assertEquals("beanshell script not parsed correctly", 2, counter);
    }

    /**
     * Test if both pre and post functions are only executed once in an default-result
     * @see <a href="http://jira.opensymphony.com/secure/ViewIssue.jspa?key=WF-118">Jira issue WF-118</a>
     * @throws Exception If error while executing testing
     */

    @Test
    public void testDoubleFunctionExecution() throws Exception {

        Map<String,Object> inputs = new HashMap<String,Object>();
        inputs.put("test", this);

        counter = 0;

        URL url = getClass().getResource("/core/double.oswf.xml");
        long piid = wfEngine.initialize(url.toString(), 1);
        wfEngine.doAction(piid, 3, inputs);
        assertEquals("function executed unexpected number of times", 2, counter);
    }


    /**
     * Sets a property set variable in the initial actions, checks it and changes
     *   it in the Step.
     *
     * NB: The workflow involkes the 'VerifyArg' function provider which has an
     *   embedded equals assertion
     *
     */
     
    @Test
    public void variableMotify() throws Exception {
        long piid = wfEngine.initialize(getClass().getResource("/core/variable-modify.oswf.xml").toString(), 100, null);
    }

}
