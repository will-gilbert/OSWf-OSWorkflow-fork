package tests.propertyset;

import org.informagen.propertyset.PropertySet;
import org.informagen.propertyset.Type;
import org.informagen.propertyset.exceptions.InvalidPropertyTypeException;

import org.informagen.propertyset.BeanPropertySet;

// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BeanTest {

    PropertySet propertySet;

    @Before
    public void setUp() throws Exception {
        
        Object bean = createBean();
        propertySet = new BeanPropertySet(bean);
        
    }
    
    Object createBean() {
        TestBean bean = new TestBean();
        
        bean.setLong(42L);
        bean.setInteger(42);
        bean.setString("Hello, World!");
        
        return bean;
    }

    
    // T E S T S ------------------------------------------------------------------------------

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

    @Test
    public void longValue() {
        assertTrue(propertySet.exists("long"));
        assertEquals(42L, propertySet.getLong("long"));
    }

    @Test
    public void intValue() {
        assertTrue(propertySet.exists("integer"));
        assertEquals(42, propertySet.getInt("integer"));
    }

    @Test
    public void stringValue() {
        assertTrue(propertySet.exists("string"));
        assertEquals("Hello, World!", propertySet.getString("string"));
    }

    @Test
    public void doesNotExist() {
        assertFalse(propertySet.exists("nonExistentKey"));
    }
    

    @Test(expected=InvalidPropertyTypeException.class)
    public void invalidPropertyTypeException() {
        assertTrue(propertySet.exists("integer"));
        assertEquals(42, propertySet.getLong("integer"));
    }
    
    
}
