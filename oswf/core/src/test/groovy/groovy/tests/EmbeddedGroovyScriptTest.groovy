package groovy.tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.impl.DefaultOSWfEngine

import java.net.URL

import java.util.HashMap
import java.util.Map

// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail


/**
 * DOCUMENT ME!
 */
public class EmbeddedGroovScriptTest {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    public int counter = 0
    private OSWfEngine wfEngine


    // B E F O R E  ===========---------------------------------------------------------------
    @Before
    public void setup() throws Exception {
        counter = 0
        wfEngine = new DefaultOSWfEngine("test user name")
    }


    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Test if XML comments (<!-- -->) in args are correctly ignored by script processors
     */
 
    @Test
    public void xmlCommentTest()  {

        def url = getClass().getResource("/core/groovyscript/Embedded-Scripts.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 1, ["test" : this])

        assert 2 == counter
    }

    /**
     * Test if multiline XML comments (<![CDATA[ ]]>) are correctly handled by script processors.
     * Allow the scrpt to contain <, & and > unescaped character
     */

    @Test
    public void cdataEnclosedTest() {

        def url = getClass().getResource("/core/groovyscript/Embedded-Scripts.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 2, ["test" : this])

        assert 2 == counter
    }

    /**
     * Test if both pre and post functions are only executed once in an default-result
     * @throws Exception If error while executing testing
     */

    @Test
    public void prePostScriptTest() throws Exception {

        def url = getClass().getResource("/core/groovyscript/Embedded-Scripts.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 3, ["test" : this])
        
        assert  2 == counter
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
        def url = getClass().getResource("/core/groovyscript/Embedded-Scripts.oswf.xml")
        def piid = wfEngine.initialize(url.toString(), 4)
    }

}
