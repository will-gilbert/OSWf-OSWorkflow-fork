package tests

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.functions.BeanShell
import org.informagen.oswf.JDBCPersistentVars


// Java - Collections
import java.util.Collections

// JUnit 4.x testing
import org.junit.Test

import static org.junit.Assert.fail


class BeanShellFunctionProviderTest {

    /**
     * Test that an exception that is thrown while processing a script is properly
     * dealt with.
     *
     * In this example, by not initializing a JDBC DataSource, the BeanShell script fails.
     * It should record the underlying lcoation of the NullPointerError JDBC error, but instead you get a null pointer
     * exception location of the script instead.
     *
     */

    @Test
    void throwException() throws Exception {
        
        def function = new BeanShell()
        
        // Create a JDBC PersistentVars without parameters succeeds but throws
        //     an exception when used
        
        def transientVars = [:]
        def args = [:]
        def persistentVars = new JDBCPersistentVars(Collections.EMPTY_MAP, Collections.EMPTY_MAP)

        args.put(OSWfEngine.BSH_SCRIPT, 'String actor = "testactor" persistentVars.setString("actor", actor)')

        try {
            function.execute(transientVars, args, persistentVars)
            fail()
        } catch (Exception exception) {
            assert exception.message.contains('Evaluation error while running BSH function script')
        }
    }
}
