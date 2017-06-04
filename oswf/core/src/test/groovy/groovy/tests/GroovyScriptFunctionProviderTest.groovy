package groovy.tests

import org.informagen.typedmap.MemoryTypedMap

import org.informagen.oswf.functions.GroovyScript
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.OSWfEngine


// JUnit 4.x testing
import org.junit.Before
import org.junit.After
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.fail



/**
 * Unit test to prove that the GroovyScript Function Provider eats the key exception
 *   you need.  It tells you what line the script fails, but not the underlying
 *   solution.
 *
 * @author Will Gilbert(gilbert@informagen.com)
 */


public class GroovyScriptFunctionProviderTest {

    def function
    def transientVars
    def args
    def persistentVars

    @Before
    void setup() {
        function = new GroovyScript()
        
        // Create a Memory persistent variables 
        
        transientVars = [:]
        args = [:]
        persistentVars = new MemoryTypedMap()
    }

    @Test
    public void persistentArgs() {
        
        args.put(OSWfEngine.BSH_SCRIPT, """
            // Groovy Script test
            def actor = 'testactor'
            persistentVars.setString('actor', actor)
        """)

        try {
            function.execute(transientVars, args, persistentVars)
            assert 'testactor' == persistentVars.getString('actor')
        } catch (WorkflowException e) {
            fail();
        }
    }

    @Test
    public void invalidScriptExceptionTest() {
        
        args.put(OSWfEngine.BSH_SCRIPT, """
            println('Hello World' // Missing closing parenthesis
        """)

        try {
            function.execute(transientVars, args, persistentVars)
            fail('Should throw "GroovyScript Function Provider: Evaluation error while running Groovy function script"');
        } catch (WorkflowException e) {
            assert true
        }
    }

    @Test
    public void nullTransientVarsTest() {
        
        args.put(OSWfEngine.BSH_SCRIPT, """
            println('Hello World')
        """)

        transientVars = null

        try {
            function.execute(transientVars, args, persistentVars)
            fail('Should throw: "java.lang.NullPointerException"');
        } catch (Exception e) {
            assert true
        }
    }
}
