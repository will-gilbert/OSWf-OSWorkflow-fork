package tests

import org.informagen.oswf.PersistentVars
import org.informagen.oswf.MemoryPersistentVars

import org.informagen.oswf.VariableResolver
import org.informagen.oswf.impl.DefaultVariableResolver

// Java Collections
import java.util.Map
import java.util.HashMap

// JUnit 4.x testing
import org.junit.Before
import org.junit.Ignore
import org.junit.Test


/**
 * This testcase verifies that variable interpolation and resolver works
 */


class VariableResolverTest  {

    Map<String,Object> transientsVars
    PersistentVars persistentVars
    DefaultVariableResolver resolver
    
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    @Before
    void setup() {
                
        transientsVars = new HashMap<String,Object>()
        persistentVars = new MemoryPersistentVars()

        A a2 = new A("biff", new B(22, "Jack"))
        persistentVars.setObject("a2", a2)

        resolver = new DefaultVariableResolver()
    }




    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    void getVariableFromMaps() {
        
        A a = new A("aName", new B(100, "bName"))
        transientsVars.put("a", a)

        // Add to both transients and property set
        transientsVars.put("blah", "blah")
        
        // Transients are searched before property sets this 'blah' should
        //  be masked by the one in transients
        persistentVars.setString("blah", "NOT BLAH")
        
        // Add a string to the property set
        persistentVars.setString("foo", "bar")

        // Returns Object 'a' saved using the key "a" in transients
        assert a == resolver.getVariableFromMaps("a", transientsVars, persistentVars)
        
        // Returns getName() from Object 'a' saved using the key "a" in transients
        assert "aName" == resolver.getVariableFromMaps("a.name", transientsVars, persistentVars)

        // Returns the string, "blah" saved using the key "blah" in transients but not persistentVars
        assert "blah" == resolver.getVariableFromMaps("blah", transientsVars, persistentVars)

        // Returns the string, "bar" saved using the key "foo" in the property set
        assert "bar" == resolver.getVariableFromMaps("foo", transientsVars, persistentVars)

        // Returns getName() from Object return via getB() saved using the key "a2" in transients
        assert "Jack" == resolver.getVariableFromMaps("a2.b.name", transientsVars, persistentVars)

        // Returns getAge() from Object return via getB() saved using the key "a2" in transients
        assert 22 == resolver.getVariableFromMaps("a2.b.age", transientsVars, persistentVars)
    }

    @Test
    void translateVariables() {        

        // Variable string interpolation using bean properties
        assert 'Hello, Jack, what is your age? 22', 
                resolver.translateVariables('Hello, ${a2.b.name}, what is your age? ${a2.b.age}', 
                transientsVars, persistentVars)

        // Variable string interpolation with an interpolation which does not exist
        assert 'Hello, , what is your age? 22', 
                resolver.translateVariables('Hello, ${I.Dont.EXIST}, what is your age? ${a2.b.age}', 
                transientsVars, persistentVars)
    }

    //~ Inner Classes ========================================================================
    // These are Groovy class which have their getter & setter generated by the compiler
    
    class A {
        def B b
        def String name

        A(String name, B b) {
            this.name = name
            this.b = b
        }
    }

    class B {
        def String name
        def int age

        B(int age, String name) {
            this.age = age
            this.name = name
        }
    }
}
