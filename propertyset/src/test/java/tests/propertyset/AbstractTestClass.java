package tests.propertyset;


// OSWf - PropertySet
import org.informagen.propertyset.PropertySet;
import org.informagen.propertyset.Type;
import org.informagen.propertyset.util.XMLUtils;
import org.informagen.propertyset.exceptions.PropertySetException;

import java.lang.StringBuffer;
import java.util.Date;
import java.util.Properties;
import org.w3c.dom.Document;

// JUnit 4.x testing
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


public abstract class AbstractTestClass {

    protected final String TEXT_VALUE = "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890";
    protected final String XML_STRING = "<property-set>" + "<property key=\"testBoolean\" type=\"boolean\">true</property>" + "<property key=\"testData\" type=\"data\">dmFsdWUx</property>" + "<property key=\"testDate\" type=\"date\">2004-05-23 10:06:00</property>" + "<property key=\"testDouble\" type=\"double\">10.245</property>" + "<property key=\"testInt\" type=\"int\">7</property>" + "<property key=\"testLong\" type=\"long\">100000</property>" + "<property key=\"testObject\" type=\"object\">rO0ABXNyAC5jb20ub3BlbnN5bXBob255Lm1vZHVsZS5wcm9wZXJ0eXNldC5UZXN0T2JqZWN0A6KYOgP1WoYCAAFKAAJpZHhwAAAAAAAAAAE=</property>" + "<property key=\"testString\" type=\"string\">value1</property>" + "<property key=\"testProperties\" type=\"properties\">" + "<properties><property key=\"prop1\" type=\"string\">value1</property></properties>" + "</property>" + "<property key=\"testText\" type=\"text\"><![CDATA[" + TEXT_VALUE + "]]></property>" + "<property key=\"testUnknown\" type=\"unknown\">unknown</property>" + "</property-set>";

    protected PropertySet propertySet;


    @Test
    public void getKeys() {
        propertySet.setString("test1", "value1");
        propertySet.setString("test2", "value2");
        propertySet.setString("test3", "value3");
        assertEquals(3, propertySet.getKeys().size());
    }

    @Test
    public void getKeysOfType() {
        if (propertySet.supportsTypes()) {
            propertySet.setString("test1", "value1");
            propertySet.setString("test2", "value2");
            propertySet.setInt("testInt", 14);
            assertEquals(2, propertySet.getKeys(Type.STRING).size());
            assertEquals(1, propertySet.getKeys(Type.INT).size());
        }
    }

    @Test
    public void removeAllKeys() {
        propertySet.setString("test1", "value1");
        assertEquals(1, propertySet.getKeys().size());

        try {
            propertySet.remove();
            assertEquals(0, propertySet.getKeys().size());
        } catch (PropertySetException e) {
            // this is ok too for read only PropertySets
        }
    }

    @Test
    public void removeSingleKey() {
        propertySet.setString("test1", "value1");
        propertySet.setString("test2", "value2");
        assertEquals(2, propertySet.getKeys().size());

        try {
            propertySet.remove("test1");
            assertEquals(1, propertySet.getKeys().size());
        } catch (PropertySetException e) {
            // this is ok too for read only PropertySets
        }
    }


    @Test
    public void getKeysWithPrefix() {
        propertySet.setString("test1", "value1");
        propertySet.setString("test2", "value2");
        propertySet.setString("username", "user1");
        assertEquals(2, propertySet.getKeys("test").size());
        assertEquals(1, propertySet.getKeys("user").size());
    }

    @Test
    public void getKeysWithPrefixOfType() {
        if (propertySet.supportsTypes()) {
            propertySet.setString("test1", "value1");
            propertySet.setString("test2", "value2");
            propertySet.setString("username", "user1");
            propertySet.setInt("testInt", 32);
            propertySet.setInt("usernum", 18);
            assertEquals(2, propertySet.getKeys("test", Type.STRING).size());
            assertEquals(1, propertySet.getKeys("user", Type.STRING).size());
            assertEquals(1, propertySet.getKeys("test", Type.INT).size());
            assertEquals(1, propertySet.getKeys("user", Type.INT).size());
        }
    }


    @Test
    public void testBoolean() {
        
        propertySet.setBoolean("boolean-true", true);
        propertySet.setBoolean("boolean-false", false);
        assertTrue(propertySet.getBoolean("boolean-true"));
        assertFalse(propertySet.getBoolean("boolean-false"));
        assertFalse(propertySet.getBoolean("non.existent.key"));

        if (propertySet.supportsType(Type.BOOLEAN)) 
            assertEquals(Type.BOOLEAN, propertySet.getType("boolean-true"));
    }

    @Test
    public void testString() {
        
        propertySet.setString("a-string", "A String value");
        propertySet.setString("string", "string");
        propertySet.setString("string-space", " ");
        propertySet.setString("string-empty", "");
        propertySet.setString("string-null", null);
        propertySet.setString("string-100", "100");

        assertEquals("A String value", propertySet.getString("a-string"));
        assertNull(propertySet.getString("non.existent.key"));

        if (propertySet.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, propertySet.getType("string"));
   }

    @Test
    public void testText() {
        
        final int size = 10000;
        StringBuffer buffer = new StringBuffer(size);
        
        for (int i=0; i<size; i++)
            buffer.append("A");
    
        propertySet.setText("text", buffer.toString());
        propertySet.setText("text-empty", "");
        propertySet.setText("text-null", null);
        propertySet.setText("text-100", "100");

        assertEquals(10000, propertySet.getText("text").length());
        assertNull(propertySet.getText("non.existent.key"));

        if (propertySet.supportsType(Type.TEXT)) 
            assertEquals(Type.TEXT, propertySet.getType("text"));
    }

    @Test
    public void testInt() {
        
        propertySet.setInt("int", 42);
        propertySet.setInt("int-zero", 0);

        assertEquals(42, propertySet.getInt("int"));
        assertEquals(0, propertySet.getInt("int-zero"));
        assertEquals(0, propertySet.getInt("non.existent.key"));
        
        if (propertySet.supportsType(Type.INT))
            assertEquals(Type.INT, propertySet.getType("int"));
    }

    @Test
    public void testLong() {
        
        propertySet.setLong("long", 100l);
        assertEquals(100l, propertySet.getLong("long"));
        assertEquals(0l, propertySet.getLong("non.existent.key"));

        if (propertySet.supportsType(Type.LONG)) 
            assertEquals(Type.LONG, propertySet.getType("long"));
    }

    @Test
    public void testDouble() {
        
        propertySet.setDouble("double-100.001", 100.001);
        propertySet.setDouble("double", 1.000);
        propertySet.setDouble("double-zero", 0.0);

        assertEquals(100.001, propertySet.getDouble("double-100.001"), 0.000001);
        assertEquals(0.0, propertySet.getDouble("non.existent.key"), 0.000001);

        if (propertySet.supportsType(Type.DOUBLE))
            assertEquals(Type.DOUBLE, propertySet.getType("double"));
    }

    @Test
    public void testDate() {

        propertySet.setDate("date", new Date(10000000L));
        propertySet.setDate("date-zero", new Date(0));
        propertySet.setDate("date-future", new Date(10000000000000L));

        assertNull(propertySet.getDate("non.existent.key"));

        if (propertySet.supportsType(Type.DATE)) 
            assertEquals(Type.DATE, propertySet.getType("date"));
    }

    @Test
    public void testData() {

        byte[] byteArray = {1,2,3,4,5,6};
        propertySet.setData("data", byteArray);

        assertEquals(6, propertySet.getData("data").length);
        assertNull(propertySet.getData("non.existent.key"));

        if (propertySet.supportsType(Type.DATA)) 
            assertEquals(Type.DATA, propertySet.getType("data"));
    }

    @Test
    public void testObject() {

        propertySet.setObject("object", new StringBuffer("Hello, world"));
        Object object = propertySet.getObject("object");

        assertTrue(object instanceof StringBuffer);
    
        assertEquals("Hello, world", ((StringBuffer)object).toString());
        assertNull(propertySet.getObject("non.existent.key"));

        if (propertySet.supportsType(Type.OBJECT)) 
            assertEquals(Type.OBJECT, propertySet.getType("object"));
    }

    @Test
    public void testProperties() {
            
        Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        propertySet.setProperties("properties", properties);
        
        // Did we really get back a Properties object
        assertTrue(propertySet.getProperties("properties") instanceof Properties);
        Properties fetched = propertySet.getProperties("properties");
    
        assertEquals(2, fetched.size());
        assertEquals("1", fetched.getProperty("one"));
        assertEquals("2", fetched.getProperty("two"));
    
        assertNull(propertySet.getProperties("non.existent.key"));

        if (propertySet.supportsType(Type.PROPERTIES)) 
            assertEquals(Type.PROPERTIES, propertySet.getType("properties"));
    }

    @Test
    public void testXML() throws Exception {
    
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

        // Did we really get back a Document object
        assertTrue(propertySet.getXML("xml") instanceof Document);

        Document document = propertySet.getXML("xml");
    
        assertEquals(2, XMLUtils.xpathList(document, "/list/item").getLength());
        assertEquals("Books", XMLUtils.xpath(document, "/list/item[1]/text()").getNodeValue());
        assertEquals("Supplies", XMLUtils.xpath(document, "/list/item[2]/text()").getNodeValue());
        assertEquals("100", XMLUtils.xpath(document, "/list/item[1]/@cost").getNodeValue());
        assertEquals("200", XMLUtils.xpath(document, "/list/item[2]/@cost").getNodeValue());
    
        assertNull(propertySet.getXML("non.existent.key"));

        if (propertySet.supportsType(Type.XML)) 
            assertEquals(Type.XML, propertySet.getType("xml"));
    }

    @Test
    public void set() {
        
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

}
