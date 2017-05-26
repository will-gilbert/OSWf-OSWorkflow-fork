package tests;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.JDBCPropertySet;

import org.informagen.oswf.functions.BeanShellFunctionProvider;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

//import org.apache.commons.lang.exception.ExceptionUtils;

// Java - Collections
import java.util.Collections;
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
 * Unit test to prove that the BeanShellFunctionProvider eats the key exception
 *  you need.  It tells you what line the script fails, but not the underlying
 *  solution.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
public class BeanShellFunctionProviderTest {

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Test that an exception that is thrown while processing a script is properly
     * dealt with.
     *
     * In this example, by not initializing a JDBC DataSource, the BeanShell script fails.
     * It should record the underlying lcoation of the NullPointerError JDBC error, but instead you get a null pointer
     * exception location of the script instead.
     </pre>
     *
     */

    @Test
    public void throwException() throws Exception {
        
        BeanShellFunctionProvider function = new BeanShellFunctionProvider();
        
        // Create a JDBC PropertySet without parameters succeeds but throws
        //     an exception when used
        PropertySet ps = new JDBCPropertySet(Collections.EMPTY_MAP, Collections.EMPTY_MAP);
        Map transientVars = new HashMap<String,Object>();
        Map args = new HashMap<String,String>();
        args.put(OSWfEngine.BSH_SCRIPT, "String actor = \"testactor\"; propertySet.setString(\"actor\", actor);");

        try {
            function.execute(transientVars, args, ps);
            fail();
        } catch (Exception e) {
        }
    }
}