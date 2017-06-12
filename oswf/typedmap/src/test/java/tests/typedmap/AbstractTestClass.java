package tests.typedmap;


// OSWf - Persistent Variables
import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.util.XMLUtils;
import org.informagen.oswf.exceptions.PersistentVarsException;

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

    protected PersistentVars persistentVars;


    @Test
    public void getKeys() {
        persistentVars.setString("test1", "value1");
        persistentVars.setString("test2", "value2");
        persistentVars.setString("test3", "value3");
        assertEquals(3, persistentVars.getKeys().size());
    }

    @Test
    public void getKeysOfType() {
        if (persistentVars.supportsTypes()) {
            persistentVars.setString("test1", "value1");
            persistentVars.setString("test2", "value2");
            persistentVars.setInt("testInt", 14);
            assertEquals(2, persistentVars.getKeys(Type.STRING).size());
            assertEquals(1, persistentVars.getKeys(Type.INT).size());
        }
    }

    @Test
    public void removeAllKeys() {
        persistentVars.setString("test1", "value1");
        assertEquals(1, persistentVars.getKeys().size());

        try {
            persistentVars.remove();
            assertEquals(0, persistentVars.getKeys().size());
        } catch (PersistentVarsException e) {
            // this is ok too for read only PropertySets
        }
    }

    @Test
    public void removeSingleKey() {
        persistentVars.setString("test1", "value1");
        persistentVars.setString("test2", "value2");
        assertEquals(2, persistentVars.getKeys().size());

        try {
            persistentVars.remove("test1");
            assertEquals(1, persistentVars.getKeys().size());
        } catch (PersistentVarsException e) {
            // this is ok too for read only PropertySets
        }
    }


    @Test
    public void getKeysWithPrefix() {
        persistentVars.setString("test1", "value1");
        persistentVars.setString("test2", "value2");
        persistentVars.setString("username", "user1");
        assertEquals(2, persistentVars.getKeys("test").size());
        assertEquals(1, persistentVars.getKeys("user").size());
    }

    @Test
    public void getKeysWithPrefixOfType() {
        if (persistentVars.supportsTypes()) {
            persistentVars.setString("test1", "value1");
            persistentVars.setString("test2", "value2");
            persistentVars.setString("username", "user1");
            persistentVars.setInt("testInt", 32);
            persistentVars.setInt("usernum", 18);
            assertEquals(2, persistentVars.getKeys("test", Type.STRING).size());
            assertEquals(1, persistentVars.getKeys("user", Type.STRING).size());
            assertEquals(1, persistentVars.getKeys("test", Type.INT).size());
            assertEquals(1, persistentVars.getKeys("user", Type.INT).size());
        }
    }


    @Test
    public void testBoolean() {
        
        persistentVars.setBoolean("boolean-true", true);
        persistentVars.setBoolean("boolean-false", false);
        assertTrue(persistentVars.getBoolean("boolean-true"));
        assertFalse(persistentVars.getBoolean("boolean-false"));
        assertFalse(persistentVars.getBoolean("non.existent.key"));

        if (persistentVars.supportsType(Type.BOOLEAN)) 
            assertEquals(Type.BOOLEAN, persistentVars.getType("boolean-true"));
    }

    @Test
    public void testString() {
        
        persistentVars.setString("a-string", "A String value");
        persistentVars.setString("string", "string");
        persistentVars.setString("string-space", " ");
        persistentVars.setString("string-empty", "");
        persistentVars.setString("string-null", null);
        persistentVars.setString("string-100", "100");

        assertEquals("A String value", persistentVars.getString("a-string"));
        assertNull(persistentVars.getString("non.existent.key"));

        if (persistentVars.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, persistentVars.getType("string"));
   }

    @Test
    public void testText() {
        
        final int size = 10000;
        StringBuffer buffer = new StringBuffer(size);
        
        for (int i=0; i<size; i++)
            buffer.append("A");
    
        persistentVars.setText("text", buffer.toString());
        persistentVars.setText("text-empty", "");
        persistentVars.setText("text-null", null);
        persistentVars.setText("text-100", "100");

        assertEquals(10000, persistentVars.getText("text").length());
        assertNull(persistentVars.getText("non.existent.key"));

        if (persistentVars.supportsType(Type.TEXT)) 
            assertEquals(Type.TEXT, persistentVars.getType("text"));
    }

    @Test
    public void testInt() {
        
        persistentVars.setInt("int", 42);
        persistentVars.setInt("int-zero", 0);

        assertEquals(42, persistentVars.getInt("int"));
        assertEquals(0, persistentVars.getInt("int-zero"));
        assertEquals(0, persistentVars.getInt("non.existent.key"));
        
        if (persistentVars.supportsType(Type.INT))
            assertEquals(Type.INT, persistentVars.getType("int"));
    }

    @Test
    public void testLong() {
        
        persistentVars.setLong("long", 100l);
        assertEquals(100l, persistentVars.getLong("long"));
        assertEquals(0l, persistentVars.getLong("non.existent.key"));

        if (persistentVars.supportsType(Type.LONG)) 
            assertEquals(Type.LONG, persistentVars.getType("long"));
    }

    @Test
    public void testDouble() {
        
        persistentVars.setDouble("double-100.001", 100.001);
        persistentVars.setDouble("double", 1.000);
        persistentVars.setDouble("double-zero", 0.0);

        assertEquals(100.001, persistentVars.getDouble("double-100.001"), 0.000001);
        assertEquals(0.0, persistentVars.getDouble("non.existent.key"), 0.000001);

        if (persistentVars.supportsType(Type.DOUBLE))
            assertEquals(Type.DOUBLE, persistentVars.getType("double"));
    }

    @Test
    public void testDate() {

        persistentVars.setDate("date", new Date(10000000L));
        persistentVars.setDate("date-zero", new Date(0));
        persistentVars.setDate("date-future", new Date(10000000000000L));

        assertNull(persistentVars.getDate("non.existent.key"));

        if (persistentVars.supportsType(Type.DATE)) 
            assertEquals(Type.DATE, persistentVars.getType("date"));
    }

    @Test
    public void testData() {

        byte[] byteArray = {1,2,3,4,5,6};
        persistentVars.setData("data", byteArray);

        assertEquals(6, persistentVars.getData("data").length);
        assertNull(persistentVars.getData("non.existent.key"));

        if (persistentVars.supportsType(Type.DATA)) 
            assertEquals(Type.DATA, persistentVars.getType("data"));
    }

    @Test
    public void testObject() {

        persistentVars.setObject("object", new StringBuffer("Hello, world"));
        Object object = persistentVars.getObject("object");

        assertTrue(object instanceof StringBuffer);
    
        assertEquals("Hello, world", ((StringBuffer)object).toString());
        assertNull(persistentVars.getObject("non.existent.key"));

        if (persistentVars.supportsType(Type.OBJECT)) 
            assertEquals(Type.OBJECT, persistentVars.getType("object"));
    }

    @Test
    public void testProperties() {
            
        Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        persistentVars.setProperties("properties", properties);
        
        // Did we really get back a Properties object
        assertTrue(persistentVars.getProperties("properties") instanceof Properties);
        Properties fetched = persistentVars.getProperties("properties");
    
        assertEquals(2, fetched.size());
        assertEquals("1", fetched.getProperty("one"));
        assertEquals("2", fetched.getProperty("two"));
    
        assertNull(persistentVars.getProperties("non.existent.key"));

        if (persistentVars.supportsType(Type.PROPERTIES)) 
            assertEquals(Type.PROPERTIES, persistentVars.getType("properties"));
    }

    @Test
    public void testXML() throws Exception {
    
        String xml = "<?xml version=\"1.0\" ?>\n" +
                     "<list>\n" +
                     "  <item cost='100'>Books</item>\n" +
                     "  <item cost='200'>Supplies</item>\n" +
                     "</list>\n\n";
        try {
            persistentVars.setXML("xml", XMLUtils.parse(xml));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }

        // Did we really get back a Document object
        assertTrue(persistentVars.getXML("xml") instanceof Document);

        Document document = persistentVars.getXML("xml");
    
        assertEquals(2, XMLUtils.xpathList(document, "/list/item").getLength());
        assertEquals("Books", XMLUtils.xpath(document, "/list/item[1]/text()").getNodeValue());
        assertEquals("Supplies", XMLUtils.xpath(document, "/list/item[2]/text()").getNodeValue());
        assertEquals("100", XMLUtils.xpath(document, "/list/item[1]/@cost").getNodeValue());
        assertEquals("200", XMLUtils.xpath(document, "/list/item[2]/@cost").getNodeValue());
    
        assertNull(persistentVars.getXML("non.existent.key"));

        if (persistentVars.supportsType(Type.XML)) 
            assertEquals(Type.XML, persistentVars.getType("xml"));
    }

    @Test
    public void set() {
        
        persistentVars.setInt("an integer", 42);
        assertEquals(42, persistentVars.getInt("an integer"));
        
        persistentVars.setInt("an integer", 24);
        assertEquals(24, persistentVars.getInt("an integer"));
    }

    @Test
    public void exists() {
        
        persistentVars.setInt("int", 42);
        assertTrue(persistentVars.exists("int"));
        assertFalse(persistentVars.exists("non.existent.key"));
        
    }

    
    @Test
    public void remove() {
        
        persistentVars.setInt("int", 42);
        assertEquals(42, persistentVars.getInt("int"));
        
        persistentVars.remove("int");
        assertFalse(persistentVars.exists("int"));
    }

}
