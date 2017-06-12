package tests.typedmap;


// OSWf - TypedMap
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

    protected PersistentVars typedMap;


    @Test
    public void getKeys() {
        typedMap.setString("test1", "value1");
        typedMap.setString("test2", "value2");
        typedMap.setString("test3", "value3");
        assertEquals(3, typedMap.getKeys().size());
    }

    @Test
    public void getKeysOfType() {
        if (typedMap.supportsTypes()) {
            typedMap.setString("test1", "value1");
            typedMap.setString("test2", "value2");
            typedMap.setInt("testInt", 14);
            assertEquals(2, typedMap.getKeys(Type.STRING).size());
            assertEquals(1, typedMap.getKeys(Type.INT).size());
        }
    }

    @Test
    public void removeAllKeys() {
        typedMap.setString("test1", "value1");
        assertEquals(1, typedMap.getKeys().size());

        try {
            typedMap.remove();
            assertEquals(0, typedMap.getKeys().size());
        } catch (PersistentVarsException e) {
            // this is ok too for read only PropertySets
        }
    }

    @Test
    public void removeSingleKey() {
        typedMap.setString("test1", "value1");
        typedMap.setString("test2", "value2");
        assertEquals(2, typedMap.getKeys().size());

        try {
            typedMap.remove("test1");
            assertEquals(1, typedMap.getKeys().size());
        } catch (PersistentVarsException e) {
            // this is ok too for read only PropertySets
        }
    }


    @Test
    public void getKeysWithPrefix() {
        typedMap.setString("test1", "value1");
        typedMap.setString("test2", "value2");
        typedMap.setString("username", "user1");
        assertEquals(2, typedMap.getKeys("test").size());
        assertEquals(1, typedMap.getKeys("user").size());
    }

    @Test
    public void getKeysWithPrefixOfType() {
        if (typedMap.supportsTypes()) {
            typedMap.setString("test1", "value1");
            typedMap.setString("test2", "value2");
            typedMap.setString("username", "user1");
            typedMap.setInt("testInt", 32);
            typedMap.setInt("usernum", 18);
            assertEquals(2, typedMap.getKeys("test", Type.STRING).size());
            assertEquals(1, typedMap.getKeys("user", Type.STRING).size());
            assertEquals(1, typedMap.getKeys("test", Type.INT).size());
            assertEquals(1, typedMap.getKeys("user", Type.INT).size());
        }
    }


    @Test
    public void testBoolean() {
        
        typedMap.setBoolean("boolean-true", true);
        typedMap.setBoolean("boolean-false", false);
        assertTrue(typedMap.getBoolean("boolean-true"));
        assertFalse(typedMap.getBoolean("boolean-false"));
        assertFalse(typedMap.getBoolean("non.existent.key"));

        if (typedMap.supportsType(Type.BOOLEAN)) 
            assertEquals(Type.BOOLEAN, typedMap.getType("boolean-true"));
    }

    @Test
    public void testString() {
        
        typedMap.setString("a-string", "A String value");
        typedMap.setString("string", "string");
        typedMap.setString("string-space", " ");
        typedMap.setString("string-empty", "");
        typedMap.setString("string-null", null);
        typedMap.setString("string-100", "100");

        assertEquals("A String value", typedMap.getString("a-string"));
        assertNull(typedMap.getString("non.existent.key"));

        if (typedMap.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, typedMap.getType("string"));
   }

    @Test
    public void testText() {
        
        final int size = 10000;
        StringBuffer buffer = new StringBuffer(size);
        
        for (int i=0; i<size; i++)
            buffer.append("A");
    
        typedMap.setText("text", buffer.toString());
        typedMap.setText("text-empty", "");
        typedMap.setText("text-null", null);
        typedMap.setText("text-100", "100");

        assertEquals(10000, typedMap.getText("text").length());
        assertNull(typedMap.getText("non.existent.key"));

        if (typedMap.supportsType(Type.TEXT)) 
            assertEquals(Type.TEXT, typedMap.getType("text"));
    }

    @Test
    public void testInt() {
        
        typedMap.setInt("int", 42);
        typedMap.setInt("int-zero", 0);

        assertEquals(42, typedMap.getInt("int"));
        assertEquals(0, typedMap.getInt("int-zero"));
        assertEquals(0, typedMap.getInt("non.existent.key"));
        
        if (typedMap.supportsType(Type.INT))
            assertEquals(Type.INT, typedMap.getType("int"));
    }

    @Test
    public void testLong() {
        
        typedMap.setLong("long", 100l);
        assertEquals(100l, typedMap.getLong("long"));
        assertEquals(0l, typedMap.getLong("non.existent.key"));

        if (typedMap.supportsType(Type.LONG)) 
            assertEquals(Type.LONG, typedMap.getType("long"));
    }

    @Test
    public void testDouble() {
        
        typedMap.setDouble("double-100.001", 100.001);
        typedMap.setDouble("double", 1.000);
        typedMap.setDouble("double-zero", 0.0);

        assertEquals(100.001, typedMap.getDouble("double-100.001"), 0.000001);
        assertEquals(0.0, typedMap.getDouble("non.existent.key"), 0.000001);

        if (typedMap.supportsType(Type.DOUBLE))
            assertEquals(Type.DOUBLE, typedMap.getType("double"));
    }

    @Test
    public void testDate() {

        typedMap.setDate("date", new Date(10000000L));
        typedMap.setDate("date-zero", new Date(0));
        typedMap.setDate("date-future", new Date(10000000000000L));

        assertNull(typedMap.getDate("non.existent.key"));

        if (typedMap.supportsType(Type.DATE)) 
            assertEquals(Type.DATE, typedMap.getType("date"));
    }

    @Test
    public void testData() {

        byte[] byteArray = {1,2,3,4,5,6};
        typedMap.setData("data", byteArray);

        assertEquals(6, typedMap.getData("data").length);
        assertNull(typedMap.getData("non.existent.key"));

        if (typedMap.supportsType(Type.DATA)) 
            assertEquals(Type.DATA, typedMap.getType("data"));
    }

    @Test
    public void testObject() {

        typedMap.setObject("object", new StringBuffer("Hello, world"));
        Object object = typedMap.getObject("object");

        assertTrue(object instanceof StringBuffer);
    
        assertEquals("Hello, world", ((StringBuffer)object).toString());
        assertNull(typedMap.getObject("non.existent.key"));

        if (typedMap.supportsType(Type.OBJECT)) 
            assertEquals(Type.OBJECT, typedMap.getType("object"));
    }

    @Test
    public void testProperties() {
            
        Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        typedMap.setProperties("properties", properties);
        
        // Did we really get back a Properties object
        assertTrue(typedMap.getProperties("properties") instanceof Properties);
        Properties fetched = typedMap.getProperties("properties");
    
        assertEquals(2, fetched.size());
        assertEquals("1", fetched.getProperty("one"));
        assertEquals("2", fetched.getProperty("two"));
    
        assertNull(typedMap.getProperties("non.existent.key"));

        if (typedMap.supportsType(Type.PROPERTIES)) 
            assertEquals(Type.PROPERTIES, typedMap.getType("properties"));
    }

    @Test
    public void testXML() throws Exception {
    
        String xml = "<?xml version=\"1.0\" ?>\n" +
                     "<list>\n" +
                     "  <item cost='100'>Books</item>\n" +
                     "  <item cost='200'>Supplies</item>\n" +
                     "</list>\n\n";
        try {
            typedMap.setXML("xml", XMLUtils.parse(xml));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }

        // Did we really get back a Document object
        assertTrue(typedMap.getXML("xml") instanceof Document);

        Document document = typedMap.getXML("xml");
    
        assertEquals(2, XMLUtils.xpathList(document, "/list/item").getLength());
        assertEquals("Books", XMLUtils.xpath(document, "/list/item[1]/text()").getNodeValue());
        assertEquals("Supplies", XMLUtils.xpath(document, "/list/item[2]/text()").getNodeValue());
        assertEquals("100", XMLUtils.xpath(document, "/list/item[1]/@cost").getNodeValue());
        assertEquals("200", XMLUtils.xpath(document, "/list/item[2]/@cost").getNodeValue());
    
        assertNull(typedMap.getXML("non.existent.key"));

        if (typedMap.supportsType(Type.XML)) 
            assertEquals(Type.XML, typedMap.getType("xml"));
    }

    @Test
    public void set() {
        
        typedMap.setInt("an integer", 42);
        assertEquals(42, typedMap.getInt("an integer"));
        
        typedMap.setInt("an integer", 24);
        assertEquals(24, typedMap.getInt("an integer"));
    }

    @Test
    public void exists() {
        
        typedMap.setInt("int", 42);
        assertTrue(typedMap.exists("int"));
        assertFalse(typedMap.exists("non.existent.key"));
        
    }

    
    @Test
    public void remove() {
        
        typedMap.setInt("int", 42);
        assertEquals(42, typedMap.getInt("int"));
        
        typedMap.remove("int");
        assertFalse(typedMap.exists("int"));
    }

}
