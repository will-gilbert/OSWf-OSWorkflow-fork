package tests.typedmap;


// This package - Super class tests
import tests.typedmap.AbstractTestClass;

import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.typedmap.Type;
import org.informagen.oswf.typedmap.MemoryTypedMap;


// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class MemoryTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        typedMap = new MemoryTypedMap();
    }

    // All other tests are in the superclass 'AbstractTestClass' 

    @Test
    public void supportsType() {
        assertTrue(typedMap.supportsType(Type.BOOLEAN));
        assertTrue(typedMap.supportsType(Type.DATA));
        assertTrue(typedMap.supportsType(Type.DATE));
        assertTrue(typedMap.supportsType(Type.DOUBLE));
        assertTrue(typedMap.supportsType(Type.INT));
        assertTrue(typedMap.supportsType(Type.LONG));
        assertTrue(typedMap.supportsType(Type.OBJECT));
        assertTrue(typedMap.supportsType(Type.PROPERTIES));
        assertTrue(typedMap.supportsType(Type.STRING));
        assertTrue(typedMap.supportsType(Type.TEXT));
        assertTrue(typedMap.supportsType(Type.XML));
    }

}
