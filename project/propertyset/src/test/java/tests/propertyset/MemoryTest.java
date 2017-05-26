package tests.propertyset;


// This package - Super class tests
import tests.propertyset.AbstractTestClass;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.MemoryPropertySet;


// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class MemoryTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        propertySet = new MemoryPropertySet();
    }

    // All other tests are in the superclass 'AbstractTestClass' 

    @Test
    public void supportsType() {
        assertTrue(propertySet.supportsType(Type.BOOLEAN));
        assertTrue(propertySet.supportsType(Type.DATA));
        assertTrue(propertySet.supportsType(Type.DATE));
        assertTrue(propertySet.supportsType(Type.DOUBLE));
        assertTrue(propertySet.supportsType(Type.INT));
        assertTrue(propertySet.supportsType(Type.LONG));
        assertTrue(propertySet.supportsType(Type.OBJECT));
        assertTrue(propertySet.supportsType(Type.PROPERTIES));
        assertTrue(propertySet.supportsType(Type.STRING));
        assertTrue(propertySet.supportsType(Type.TEXT));
        assertTrue(propertySet.supportsType(Type.XML));
    }

}
