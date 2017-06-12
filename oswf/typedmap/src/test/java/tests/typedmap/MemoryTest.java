package tests.typedmap;


// This package - Super class tests
import tests.typedmap.AbstractTestClass;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.MemoryPersistentVars;


// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class MemoryTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        persistentVars = new MemoryPersistentVars();
    }

    // All other tests are in the superclass 'AbstractTestClass' 

    @Test
    public void supportsType() {
        assertTrue(persistentVars.supportsType(Type.BOOLEAN));
        assertTrue(persistentVars.supportsType(Type.DATA));
        assertTrue(persistentVars.supportsType(Type.DATE));
        assertTrue(persistentVars.supportsType(Type.DOUBLE));
        assertTrue(persistentVars.supportsType(Type.INT));
        assertTrue(persistentVars.supportsType(Type.LONG));
        assertTrue(persistentVars.supportsType(Type.OBJECT));
        assertTrue(persistentVars.supportsType(Type.PROPERTIES));
        assertTrue(persistentVars.supportsType(Type.STRING));
        assertTrue(persistentVars.supportsType(Type.TEXT));
        assertTrue(persistentVars.supportsType(Type.XML));
    }

}
