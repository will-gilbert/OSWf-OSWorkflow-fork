package tests;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.MemoryPersistentVars;

import org.informagen.oswf.VariableResolver;
import org.informagen.oswf.impl.DefaultVariableResolver;

// Java Collections
import java.util.Map;
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


/**
 * This testcase verifies that an AbstractWorkflow provided methods work properly.
 */
public class VariableResolverTest  {

        Map<String,Object> transients;
        PersistentVars ps;
        DefaultVariableResolver resolver;
    
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    @Before
    public void setup() {
                
        transients = new HashMap<String,Object>();
        ps = new MemoryPersistentVars();

        A a2 = new A("biff", new B(-1, "Jack"));
        ps.setObject("a2", a2);

        resolver = new DefaultVariableResolver();
    }




    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void getVariableFromMaps() {
        
        A a = new A("aName", new B(100, "bName"));
        transients.put("a", a);

        // Add to both transients and property set
        transients.put("blah", "blah");
        
        // Transients are searched before property sets; this 'blah' should
        //  be masked by the one in transients
        ps.setString("blah", "NOT BLAH");
        
        // Add a string to the property set
        ps.setString("foo", "bar");

        // Returns Object 'a' saved using the key "a" in transients
        assertEquals(a, resolver.getVariableFromMaps("a", transients, ps));
        
        // Returns getName() from Object 'a' saved using the key "a" in transients
        assertEquals("aName", resolver.getVariableFromMaps("a.name", transients, ps));

        // Returns the string, "blah" saved using the key "blah" in transients but not ps
        assertEquals("blah", resolver.getVariableFromMaps("blah", transients, ps));

        // Returns the string, "bar" saved using the key "foo" in the property set
        assertEquals("bar", resolver.getVariableFromMaps("foo", transients, ps));

        // Returns getName() from Object return via getB() saved using the key "a2" in transients
        assertEquals("Jack", resolver.getVariableFromMaps("a2.b.name", transients, ps));

        // Returns getAge() from Object return via getB() saved using the key "a2" in transients
        assertEquals(new Integer(-1), resolver.getVariableFromMaps("a2.b.age", transients, ps));
    }

    @Test
    public void translateVariables() {        

        // Variable string interpolation using bean properties
        assertEquals("hello, Jack, what is your age? -1", resolver.translateVariables("hello, ${a2.b.name}, what is your age? ${a2.b.age}", transients, ps));

        // Variable string interpolation with an interpolation which does not exist
        assertEquals("hello, , what is your age? -1", resolver.translateVariables("hello, ${I.Don't.EXIST}, what is your age? ${a2.b.age}", transients, ps));
    }

    //~ Inner Classes ========================================================================

    public class A {
        private B b;
        private String name;

        public A(String name, B b) {
            this.name = name;
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public String getName() {
            return name;
        }
    }

    public class B {
        private String name;
        private int age;

        public B(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }
    }
}
