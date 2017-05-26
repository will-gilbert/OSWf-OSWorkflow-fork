package propertyset;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;

import org.informagen.oswf.propertyset.util.XMLUtils;

import java.lang.StringBuffer;
import java.util.Date;
import java.util.Properties;
import org.w3c.dom.Document;

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


public abstract class PropertySetBase {

    protected PropertySet propertySet;

    @Test
    public void testBoolean() {
        propertySet.setBoolean("boolean-true", true);
        propertySet.setBoolean("boolean-false", false);
        assertTrue(propertySet.getBoolean("boolean-true"));
        assertFalse(propertySet.getBoolean("boolean-false"));
        assertEquals(Type.BOOLEAN, propertySet.getType("boolean-true"));
        assertFalse(propertySet.getBoolean("non.existent.key"));
    }

    @Test
    public void testString() {
        createStrings();
        assertEquals("A String value", propertySet.getString("a-string"));
        assertEquals(Type.STRING, propertySet.getType("string"));
        assertNull(propertySet.getString("non.existent.key"));
   }

    @Test
    public void testText() {
        createTexts(); 
        assertEquals(10000, propertySet.getText("text").length());
        assertEquals(Type.TEXT, propertySet.getType("text"));
        assertNull(propertySet.getText("non.existent.key"));
    }

    @Test
    public void testInt() {
        createInts();
        assertEquals(42, propertySet.getInt("int"));
        assertEquals(0, propertySet.getInt("int-zero"));
        assertEquals(Type.INT, propertySet.getType("int"));
        assertEquals(0, propertySet.getInt("non.existent.key"));
    }

    @Test
    public void testLong() {
        propertySet.setLong("long", 100l);
        assertEquals(100l, propertySet.getLong("long"));
        assertEquals(Type.LONG, propertySet.getType("long"));
        assertEquals(0l, propertySet.getLong("non.existent.key"));
    }

    @Test
    public void testDouble() {
        createDoubles();
        assertEquals(100.001, propertySet.getDouble("double-100.001"), 0.000001);
        assertEquals(Type.DOUBLE, propertySet.getType("double"));
        assertEquals(0.0, propertySet.getDouble("non.existent.key"), 0.000001);
    }

    @Test
    public void testDate() {
        createDates();
        assertEquals(Type.DATE, propertySet.getType("date"));
        assertNull(propertySet.getDate("non.existent.key"));
    }

    @Test
    public void testData() {
        createByteArray();
        assertEquals(6, propertySet.getData("data").length);
        assertEquals(Type.DATA, propertySet.getType("data"));
        assertNull(propertySet.getData("non.existent.key"));
    }

    @Test
    public void testObject() {
        createObject();
        Object object = propertySet.getObject("object");
        
        assertTrue(object instanceof StringBuffer);
        
        assertEquals("Hello, world", ((StringBuffer)object).toString());
        assertEquals(Type.OBJECT, propertySet.getType("object"));
        assertNull(propertySet.getObject("non.existent.key"));
    }

    @Test
    public void testProperties() {
        createProperties();
        // Did we really get back a Properties object
        assertTrue(propertySet.getProperties("properties") instanceof Properties);
        Properties fetched = propertySet.getProperties("properties");
        
        assertEquals(2, fetched.size());
        assertEquals("1", fetched.getProperty("one"));
        assertEquals("2", fetched.getProperty("two"));
        
        assertNull(propertySet.getProperties("non.existent.key"));
    }

    @Test
    public void testXML() throws Exception {
    
        createXML();  

        // Did we really get back a Document object
        assertTrue(propertySet.getXML("xml") instanceof Document);

        Document document = propertySet.getXML("xml");
        
        assertEquals(2, XMLUtils.xpathList(document, "/list/item").getLength());
        assertEquals("Books", XMLUtils.xpath(document, "/list/item[1]/text()").getNodeValue());
        assertEquals("Supplies", XMLUtils.xpath(document, "/list/item[2]/text()").getNodeValue());
        assertEquals("100", XMLUtils.xpath(document, "/list/item[1]/@cost").getNodeValue());
        assertEquals("200", XMLUtils.xpath(document, "/list/item[2]/@cost").getNodeValue());
        
        assertNull(propertySet.getXML("non.existent.key"));
    }

    @Test
    public void update() {
        propertySet.setInt("an integer", 42);
        assertEquals(42, propertySet.getInt("an integer"));
        propertySet.setInt("an integer", 24);
        assertEquals(24, propertySet.getInt("an integer"));
    }

    @Test
    public void exists() {
        propertySet.setInt("int", 42);
        assertTrue(propertySet.exists("int"));
        assertFalse(propertySet.exists("non.existent.key"));
    }

    
    @Test
    public void remove() {
        propertySet.setInt("int", 42);
        assertEquals(42, propertySet.getInt("int"));
        propertySet.remove("int");
        assertFalse(propertySet.exists("int"));
    }


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

    // Protected, used by subclasses ===========================================================

    protected void loadPropertySet() {
        createBooleans();
        createInts();
        createLongs();
        createDoubles();
        createStrings(); 
        createTexts();
        createDates();
        createProperties();
        createXML();
        createObject();
    }


    protected void createBooleans() {
        propertySet.setBoolean("boolean-true", true);
        propertySet.setBoolean("boolean-false", false);
    }

    protected void createInts() {
        propertySet.setInt("int", 42);
        propertySet.setInt("int-zero", 0);
    }

    protected void createLongs() {
        propertySet.setLong("long", 1L);
        propertySet.setLong("long-zero", 0L);
    }
    
    protected void createDoubles() {
        propertySet.setDouble("double-100.001", 100.001);
        propertySet.setDouble("double", 1.000);
        propertySet.setDouble("double-zero", 0.0);
    }

    protected void createStrings() {
        propertySet.setString("a-string", "A String value");
        propertySet.setString("string", "string");
        propertySet.setString("string-space", " ");
        propertySet.setString("string-empty", "");
        propertySet.setString("string-null", null);
        propertySet.setString("string-100", "100");
    }
    
    protected void createTexts() {
        final int size = 10000;
        StringBuffer buffer = new StringBuffer(size);
        
        for (int i=0; i<size; i++)
            buffer.append("A");
    
        propertySet.setText("text", buffer.toString());
        propertySet.setText("text-empty", "");
        propertySet.setText("text-null", null);
        propertySet.setText("text-100", "100");
    }
    
    protected void createDates() {
        propertySet.setDate("date", new Date(10000000L));
        propertySet.setDate("date-zero", new Date(0));
        propertySet.setDate("date-future", new Date(10000000000000L));
    }
    
    protected void createProperties() {
        Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        propertySet.setProperties("properties", properties);
    }

    protected void createByteArray() {
        byte[] byteArray = {1,2,3,4,5,6};
        propertySet.setData("data", byteArray);
    }

    protected void createXML() {
        String xml = "<?xml version=\"1.0\" ?>\n" +
                     "<list>\n" +
                     "  <item cost='100'>Books</item>\n" +
                     "  <item cost='200'>Supplies</item>\n" +
                     "</list>\n\n";
        try {
            propertySet.setXML("xml", XMLUtils.parse(xml));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }

    protected void createObject() {
        propertySet.setObject("object", new StringBuffer("Hello, world"));
    }






}
