package tests.persistentvars;


import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.MemoryPersistentVars;

// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;


public class MemoryTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        persistentVars = new MemoryPersistentVars();
    }

    // All other tests are in the superclass 'AbstractTestClass' 

    @Test
    public void supportsType() {
        assert persistentVars.supportsType(Type.BOOLEAN)
        assert persistentVars.supportsType(Type.DATA)
        assert persistentVars.supportsType(Type.DATE)
        assert persistentVars.supportsType(Type.DOUBLE)
        assert persistentVars.supportsType(Type.INT)
        assert persistentVars.supportsType(Type.LONG)
        assert persistentVars.supportsType(Type.OBJECT)
        assert persistentVars.supportsType(Type.PROPERTIES)
        assert persistentVars.supportsType(Type.STRING)
        assert persistentVars.supportsType(Type.TEXT)
        assert persistentVars.supportsType(Type.XML)
    }

}
