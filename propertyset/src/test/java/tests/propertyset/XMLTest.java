package tests.propertyset;

// This package - Super class tests
import tests.propertyset.AbstractTestClass;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.XMLPropertySet;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.lang.StringBuffer;
import java.util.Date;
import java.util.Properties;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;


public class XMLTest extends AbstractTestClass {

    @Before
    public void createPropertySet() {
        propertySet = new XMLPropertySet();
    }


    // All tests in the superclass 'AbstractTestClass' will run
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
    public void savePropertySetToDocument() throws Exception {
        propertySet.setInt("int", 42);
        
        assertTrue(propertySet instanceof XMLPropertySet);
        
        OutputStream out = new ByteArrayOutputStream();
        ((XMLPropertySet)propertySet).save(out);

        String expected = "<?xml version=\"1.0\" ?>\n" +
                          "<property-set>\n" +
                          "  <property key=\"int\" type=\"INT\">42</property>\n" +
                          "</property-set>\n\n";

        assertEquals(expected, out.toString());
    }
    
    
    public void testLoadWithReader() throws IOException, ParserConfigurationException, SAXException {
        StringReader reader = new StringReader(XML_STRING);
        ((XMLPropertySet) propertySet).load(reader);
        assertEquals(true, propertySet.getBoolean("testBoolean"));
        assertEquals(new String("value1".getBytes()), new String(propertySet.getData("testData")));
        assertEquals(10.245D, propertySet.getDouble("testDouble"), 0);
        assertEquals(7, propertySet.getInt("testInt"));
        assertEquals(100000, propertySet.getLong("testLong"));
        // assertEquals(new TestObject(1), ps.getObject("testObject"));
        assertEquals("value1", propertySet.getString("testString"));

        Properties props = new Properties();
        props.setProperty("prop1", "value1");
        assertEquals(props, propertySet.getProperties("testProperties"));
        assertEquals(TEXT_VALUE, propertySet.getText("testText"));
    }

    public void testSaveWithWriter() throws IOException, ParserConfigurationException {
        propertySet.setBoolean("testBoolean", true);
        propertySet.setData("testData", "value1".getBytes());
        propertySet.setDate("testDate", new Date());
        propertySet.setDouble("testDouble", 10.245D);
        propertySet.setInt("testInt", 7);
        propertySet.setLong("testLong", 100000);
        propertySet.setObject("testObject", new TestObject(1));

        Properties props = new Properties();
        props.setProperty("prop1", "value1");
        propertySet.setProperties("testProperties", props);
        propertySet.setString("testString", "value1");
        propertySet.setText("testText", TEXT_VALUE);

        StringWriter writer = new StringWriter();
        ((XMLPropertySet) propertySet).save(writer);
        assertNotNull(writer.toString());
    }

}
