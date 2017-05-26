package tests.propertyset;

import tests.propertyset.AbstractTestClass;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.MapPropertySet;

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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MapTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        propertySet = new MapPropertySet();
    }

    
    @Test
    public void supportsType() {
        assertFalse(propertySet.supportsType(Type.BOOLEAN));
        assertFalse(propertySet.supportsType(Type.DATA));
        assertFalse(propertySet.supportsType(Type.DATE));
        assertFalse(propertySet.supportsType(Type.DOUBLE));
        assertFalse(propertySet.supportsType(Type.INT));
        assertFalse(propertySet.supportsType(Type.LONG));
        assertFalse(propertySet.supportsType(Type.OBJECT));
        assertFalse(propertySet.supportsType(Type.PROPERTIES));
        assertFalse(propertySet.supportsType(Type.STRING));
        assertFalse(propertySet.supportsType(Type.TEXT));
        assertFalse(propertySet.supportsType(Type.XML));
    }


}
