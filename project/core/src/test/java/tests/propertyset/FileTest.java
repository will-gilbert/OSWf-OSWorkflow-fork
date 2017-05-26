package tests.propertyset;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.FilePropertySet;
import org.informagen.oswf.propertyset.MemoryPropertySet;
import org.informagen.oswf.propertyset.util.PropertySetCloner;

import org.informagen.oswf.propertyset.util.XMLUtils;


import junit.framework.TestCase;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;


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

public class FileTest  {

    protected static final String PROPERTIES_FILE = "build/FilePropertySet.properties";
    protected static final String TEXT_VALUE = "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890";

    private static PropertySet propertySet;
    private static Date now;
    private static Document doc;
    private static Properties embeddedProps;

    /**
     ** The 'PropertiesFile' PropertySet can save the state of a PropertySet as a  
     **     PropertiesFile which later be reloaded into a PropertySet.
     **
     ** User Class Before and After to minimize the file I/O
     */

    @BeforeClass
    public static void setUp() throws Exception {
        
        propertySet = new FilePropertySet(PROPERTIES_FILE);
        
        // and fill it
        populatePropertiesFile();

        // Save to a file
        FilePropertySet filePropertySet = (FilePropertySet)propertySet;
        filePropertySet.store();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        File file = new File(PROPERTIES_FILE);

        if (file.exists()) 
            file.delete();
    }

    public static void populatePropertiesFile() throws IOException, ParserConfigurationException {

        // Populate the a propertyset
        now = new Date();
        doc = XMLUtils.newDocument("test-propertiesfile");
        embeddedProps = new Properties();
        embeddedProps.put("prop1", "value1");

        propertySet.setBoolean("testBoolean", true);
        propertySet.setData("testData", "value1".getBytes());
        propertySet.setDate("testDate", now);
        propertySet.setDouble("testDouble", 10.245);
        propertySet.setInt("testInt", 7);
        propertySet.setLong("testLong", 100000);
        propertySet.setObject("testObject", new TestObject(1));
        propertySet.setProperties("testProperties", embeddedProps);
        propertySet.setString("testString", "value1");
        propertySet.setText("testText", TEXT_VALUE);
        propertySet.setXML("testXml", doc);

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
    public void loadPropertiesFile() throws IOException, ClassNotFoundException {

        // Create a new FileProperties PropertySet and reload
        propertySet = new FilePropertySet(PROPERTIES_FILE);

        verifyTheProperties(propertySet);
    }
    
    @Test
    public void reloadIntoAnotherPropertySet() throws IOException, ClassNotFoundException {

        // Read in the two PropertySets: Properties File and Memory
        PropertySet src = new FilePropertySet(PROPERTIES_FILE);

        // Create a new PropertySet
        PropertySet dest = new MemoryPropertySet();

        // Copy the PropertySet contents from the 'src' to 'dest'
        PropertySetCloner.clone(src, dest);

        verifyTheProperties(dest);
    }
    
    private void verifyTheProperties(PropertySet propertySet) {

        DateFormat df = DateFormat.getInstance();
        
        assertEquals(true, propertySet.getBoolean("testBoolean"));
        assertEquals(new String("value1".getBytes()), new String(propertySet.getData("testData")));
        assertEquals(df.format(now), df.format(propertySet.getDate("testDate")));
        assertEquals(10.245, propertySet.getDouble("testDouble"), 0);
        assertEquals(7, propertySet.getInt("testInt"));
        assertEquals(100000, propertySet.getLong("testLong"));
        assertEquals(new TestObject(1), propertySet.getObject("testObject"));
        assertEquals(embeddedProps, propertySet.getProperties("testProperties"));
        assertEquals("value1", propertySet.getString("testString"));
        assertEquals(TEXT_VALUE, propertySet.getText("testText"));
        assertNotNull(propertySet.getXML("testXml"));
        
    }
    
    
    
}
