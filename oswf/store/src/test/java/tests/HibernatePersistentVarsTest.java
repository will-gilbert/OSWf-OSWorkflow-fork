package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.OSWfConfiguration;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.util.XMLUtils;


import org.informagen.oswf.hibernate.HibernateConfigurationProvider;
import org.informagen.oswf.HibernatePersistentVars;
import org.informagen.oswf.exceptions.PersistentVarsException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.lang.StringBuffer;
import java.util.Date;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
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


public class HibernatePersistentVarsTest implements usage.LeaveRequest {

    public static final String RDBMS_CONFIGURATION = System.getProperty("rdbms-configuration");

    private PersistentVars persistentVars;
    private SessionFactory sessionFactory = null;
    
    @Before
    @SuppressWarnings("unchecked")
    public void createPersistentVars() throws Exception {
        
        // Create a workflow process instance with Hibernate backing
        //  the workflow and persistent stores

        OSWfEngine wfEngine = new DefaultOSWfEngine("Test PersistentVars");
        sessionFactory = createSessionFactory();

        wfEngine.setConfiguration( new DefaultOSWfConfiguration()
            .load(getClass().getResource("/oswf-hibernate.xml"))
            .addPersistenceArg("sessionFactory", sessionFactory)
        );
                          
        long piid = wfEngine.initialize(SIMPLE, INITIAL_ACTION);

        persistentVars = wfEngine.getPersistentVars(piid);
    }

    @After
    public void closeSessionFactory() {
        if(sessionFactory != null)
            sessionFactory.close();
            
        sessionFactory = null;
    }

    @Test
    public void convertToBoolean() {
    
        loadPersistentVars();
 
        assertTrue(persistentVars.getBoolean("int"));
        assertFalse(persistentVars.getBoolean("int-zero"));
        
        assertTrue(persistentVars.getBoolean("double"));
        assertFalse(persistentVars.getBoolean("double-zero"));

        assertTrue(persistentVars.getBoolean("string"));
        assertFalse(persistentVars.getBoolean("string-empty"));
        assertFalse(persistentVars.getBoolean("string-null"));
        assertTrue(persistentVars.getBoolean("string-100"));

        assertTrue(persistentVars.getBoolean("text"));
        assertFalse(persistentVars.getBoolean("text-empty"));
        assertFalse(persistentVars.getBoolean("text-null"));
        assertTrue(persistentVars.getBoolean("text-100"));

        // InFuture returns true
        assertFalse(persistentVars.getBoolean("date"));    
        assertFalse(persistentVars.getBoolean("date-zero"));
        assertTrue(persistentVars.getBoolean("date-future"));
    }

    @Test
    public void convertToString() {
    
        loadPersistentVars();
        
        assertEquals("true", persistentVars.getString("boolean-true"));
        assertEquals("false", persistentVars.getString("boolean-false"));
        
        assertEquals("42", persistentVars.getString("int"));
        assertEquals("0", persistentVars.getString("int-zero"));
        
        assertTrue(persistentVars.getString("double").startsWith("1.0"));
        assertTrue(persistentVars.getString("double-zero").startsWith("0.0"));
        
        assertEquals("1", persistentVars.getString("long"));
        assertEquals("0", persistentVars.getString("long-zero"));
        
        assertEquals(255, persistentVars.getString("text").length());
        assertEquals("", persistentVars.getString("text-empty"));
        assertEquals(null, persistentVars.getString("text-null"));
        assertEquals("100", persistentVars.getString("text-100"));

        // assertEquals("Thu, 01 Jan 1970 02:46:40 +0000", persistentVars.getString("date"));
        // assertEquals("Thu, 01 Jan 1970 00:00:00 +0000", persistentVars.getString("date-zero"));

        assertEquals("java.lang.StringBuffer", persistentVars.getString("object"));

    }

    @Test
    public void convertToText() {
    
        loadPersistentVars();

        assertEquals("true", persistentVars.getText("boolean-true"));
        assertEquals("false", persistentVars.getText("boolean-false"));
        
        assertEquals("42", persistentVars.getText("int"));
        assertEquals("0", persistentVars.getText("int-zero"));
        
        assertTrue(persistentVars.getText("double").startsWith("1.0"));
        assertTrue(persistentVars.getText("double-zero").startsWith("0.0"));
        
        assertEquals("1", persistentVars.getText("long"));
        assertEquals("0", persistentVars.getText("long-zero"));
        
        assertEquals("string", persistentVars.getText("string"));
        assertEquals("", persistentVars.getText("string-empty"));
        assertEquals(null, persistentVars.getText("string-null"));
        assertEquals("100", persistentVars.getText("string-100"));

        // assertEquals("Thu, 01 Jan 1970 02:46:40 +0000", persistentVars.getText("date"));
        // assertEquals("Thu, 01 Jan 1970 00:00:00 +0000", persistentVars.getText("date-zero"));

        assertTrue(persistentVars.getText("properties").contains("one=1"));
        assertTrue(persistentVars.getText("properties").contains("two=2"));

        assertTrue(persistentVars.getText("xml").contains("<list>"));
        assertTrue(persistentVars.getText("xml").contains("<item cost=\"100\">Books</item>"));
        assertTrue(persistentVars.getText("xml").contains("<item cost=\"200\">Supplies</item>"));
        assertTrue(persistentVars.getText("xml").contains("</list>"));
        
        assertEquals("Hello, world", persistentVars.getText("object"));
      
    }


    @Test
    public void convertToInt() {
    
        loadPersistentVars();

        assertEquals(1, persistentVars.getInt("boolean-true"));
        assertEquals(0, persistentVars.getInt("boolean-false"));

        assertEquals(42, persistentVars.getLong("int"));
        assertEquals(0, persistentVars.getLong("int-zero"));
        
        assertEquals(1, persistentVars.getInt("double"));
        assertEquals(0, persistentVars.getInt("double-zero"));
        
        assertEquals(1, persistentVars.getInt("long"));
        assertEquals(0, persistentVars.getInt("long-zero"));
        
        assertEquals(0, persistentVars.getInt("string"));
        assertEquals(0, persistentVars.getInt("string-empty"));
        assertEquals(0, persistentVars.getInt("string-null"));
        assertEquals(100, persistentVars.getInt("string-100"));

        assertEquals(0, persistentVars.getInt("text"));
        assertEquals(0, persistentVars.getInt("text-empty"));
        assertEquals(0, persistentVars.getInt("text-null"));
        assertEquals(100, persistentVars.getInt("text-100"));

        try {
            persistentVars.getInt("date");
            fail("No conversion from date to int");
        } catch (PersistentVarsException pe) {
            assertEquals("Key 'date' of type 'DATE' does not have conversion to type 'INT'",
                        pe.getMessage());
        }
    }


    @Test
    public void convertToDouble() {
    
        loadPersistentVars();

        assertEquals(1.0, persistentVars.getDouble("boolean-true"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("boolean-false"), 0.0001);

        assertEquals(42.0, persistentVars.getDouble("int"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("int-zero"), 0.0001);
        
        assertEquals(1.0, persistentVars.getDouble("double"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("double-zero"), 0.0001);
        
        assertEquals(1.0, persistentVars.getDouble("long"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("long-zero"), 0.0001);
        
        assertEquals(0.0, persistentVars.getDouble("string"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("string-empty"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("string-null"), 0.0001);
        assertEquals(100.0, persistentVars.getDouble("string-100"), 0.0001);

        assertEquals(0.0, persistentVars.getDouble("text"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("text-empty"), 0.0001);
        assertEquals(0.0, persistentVars.getDouble("text-null"), 0.0001);
        assertEquals(100.0, persistentVars.getDouble("text-100"), 0.0001);

        try {
            persistentVars.getDouble("date");
            fail("No conversion from date to double");
        } catch (PersistentVarsException pe) {
            assertEquals("Key 'date' of type 'DATE' does not have conversion to type 'DOUBLE'", 
                       pe.getMessage());
        }

    }

    @Test
    public void convertToLong() {
    
        loadPersistentVars();

        assertEquals(1L, persistentVars.getLong("boolean-true"));
        assertEquals(0L, persistentVars.getLong("boolean-false"));
        
        assertEquals(42L, persistentVars.getLong("int"));
        assertEquals(0L, persistentVars.getLong("int-zero"));
        
        assertEquals(1L, persistentVars.getLong("double"));
        assertEquals(0L, persistentVars.getLong("double-zero"));
        
        assertEquals(1L, persistentVars.getLong("long"));
        assertEquals(0L, persistentVars.getLong("long-zero"));
        
        assertEquals(0L, persistentVars.getLong("string"));
        assertEquals(0L, persistentVars.getLong("string-empty"));
        assertEquals(0L, persistentVars.getLong("string-null"));
        assertEquals(100L, persistentVars.getLong("string-100"));

        assertEquals(0L, persistentVars.getLong("text"));
        assertEquals(0L, persistentVars.getLong("text-empty"));
        assertEquals(0L, persistentVars.getLong("text-null"));
        assertEquals(100L, persistentVars.getLong("text-100"));

        assertEquals(10000L, persistentVars.getLong("date"));
        assertEquals(0L, persistentVars.getLong("date-zero"));
    }


    @Test
    public void testBoolean() {
        persistentVars.setBoolean("boolean-true", true);
        persistentVars.setBoolean("boolean-false", false);
        assertTrue(persistentVars.getBoolean("boolean-true"));
        assertFalse(persistentVars.getBoolean("boolean-false"));
        assertEquals(Type.BOOLEAN, persistentVars.getType("boolean-true"));
        assertFalse(persistentVars.getBoolean("non.existent.key"));
    }

    @Test
    public void testString() {
        createStrings();
        assertEquals("A String value", persistentVars.getString("a-string"));
        assertEquals(Type.STRING, persistentVars.getType("string"));
        assertNull(persistentVars.getString("non.existent.key"));
   }

    @Test
    public void testText() {
        createTexts(); 
        assertEquals(10000, persistentVars.getText("text").length());
        assertEquals(Type.TEXT, persistentVars.getType("text"));
        assertNull(persistentVars.getText("non.existent.key"));
    }

    @Test
    public void testInt() {
        createInts();
        assertEquals(42, persistentVars.getInt("int"));
        assertEquals(0, persistentVars.getInt("int-zero"));
        assertEquals(Type.INT, persistentVars.getType("int"));
        assertEquals(0, persistentVars.getInt("non.existent.key"));
    }

    @Test
    public void testLong() {
        persistentVars.setLong("long", 100l);
        assertEquals(100l, persistentVars.getLong("long"));
        assertEquals(Type.LONG, persistentVars.getType("long"));
        assertEquals(0l, persistentVars.getLong("non.existent.key"));
    }

    @Test
    public void testDouble() {
        createDoubles();
        assertEquals(100.001, persistentVars.getDouble("double-100.001"), 0.000001);
        assertEquals(Type.DOUBLE, persistentVars.getType("double"));
        assertEquals(0.0, persistentVars.getDouble("non.existent.key"), 0.000001);
    }

    @Test
    public void testDate() {
        createDates();
        assertEquals(Type.DATE, persistentVars.getType("date"));
        assertNull(persistentVars.getDate("non.existent.key"));
    }

    @Test
    public void testData() {
        createByteArray();
        assertEquals(6, persistentVars.getData("data").length);
        assertEquals(Type.DATA, persistentVars.getType("data"));
        assertNull(persistentVars.getData("non.existent.key"));
    }

    @Test
    public void testObject() {
        createObject();
        Object object = persistentVars.getObject("object");
        
        assertTrue(object instanceof StringBuffer);
        
        assertEquals("Hello, world", ((StringBuffer)object).toString());
        assertEquals(Type.OBJECT, persistentVars.getType("object"));
        assertNull(persistentVars.getObject("non.existent.key"));
    }

    @Test
    public void testProperties() {
        createProperties();
        // Did we really get back a Properties object
        assertTrue(persistentVars.getProperties("properties") instanceof Properties);
        Properties fetched = persistentVars.getProperties("properties");
        
        assertEquals(2, fetched.size());
        assertEquals("1", fetched.getProperty("one"));
        assertEquals("2", fetched.getProperty("two"));
        
        assertNull(persistentVars.getProperties("non.existent.key"));
    }

    @Test
    public void testXML() throws Exception {
    
        createXML();  

        // Did we really get back a Document object
        assertTrue(persistentVars.getXML("xml") instanceof Document);        
        assertNull(persistentVars.getXML("non.existent.key"));
    }

    @Test
    public void update() {
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


    @Test
    public void supportsType() {
        assertTrue(persistentVars.supportsType(Type.BOOLEAN));
        assertTrue(persistentVars.supportsType(Type.DATA));
        assertTrue(persistentVars.supportsType(Type.DATE));
        assertTrue(persistentVars.supportsType(Type.DOUBLE));
        assertTrue(persistentVars.supportsType(Type.INT));
        assertTrue(persistentVars.supportsType(Type.LONG));
        assertTrue(persistentVars.supportsType(Type.OBJECT));
        assertTrue(persistentVars.supportsType(Type.PROPERTIES));
        assertTrue(persistentVars.supportsType(Type.STRING));
        assertTrue(persistentVars.supportsType(Type.TEXT));
        assertTrue(persistentVars.supportsType(Type.XML));
    }

    // Protected, used by subclasses ===========================================================

    protected void loadPersistentVars() {
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
        persistentVars.setBoolean("boolean-true", true);
        persistentVars.setBoolean("boolean-false", false);
    }

    protected void createInts() {
        persistentVars.setInt("int", 42);
        persistentVars.setInt("int-zero", 0);
    }

    protected void createLongs() {
        persistentVars.setLong("long", 1L);
        persistentVars.setLong("long-zero", 0L);
    }
    
    protected void createDoubles() {
        persistentVars.setDouble("double-100.001", 100.001);
        persistentVars.setDouble("double", 1.000);
        persistentVars.setDouble("double-zero", 0.0);
    }

    protected void createStrings() {
        persistentVars.setString("a-string", "A String value");
        persistentVars.setString("string", "string");
        persistentVars.setString("string-space", " ");
        persistentVars.setString("string-empty", "");
        persistentVars.setString("string-null", null);
        persistentVars.setString("string-100", "100");
    }
    
    protected void createTexts() {
        final int size = 10000;
        StringBuffer buffer = new StringBuffer(size);
        
        for (int i=0; i<size; i++)
            buffer.append("A");
    
        persistentVars.setText("text", buffer.toString());
        persistentVars.setText("text-empty", "");
        persistentVars.setText("text-null", null);
        persistentVars.setText("text-100", "100");
    }
    
    protected void createDates() {
        persistentVars.setDate("date", new Date(10000000L));
        persistentVars.setDate("date-zero", new Date(0));
        persistentVars.setDate("date-future", new Date(10000000000000L));
    }
    
    protected void createProperties() {
        Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        persistentVars.setProperties("properties", properties);
    }

    protected void createByteArray() {
        byte[] byteArray = {1,2,3,4,5,6};
        persistentVars.setData("data", byteArray);
    }

    protected void createXML() {
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
    }

    protected void createObject() {
        persistentVars.setObject("object", new StringBuffer("Hello, world"));
    }

    
    private SessionFactory createSessionFactory() {
    
        closeSessionFactory();

        String[] resources = {"oswf-store.cfg.xml", RDBMS_CONFIGURATION};
        
        Configuration configuration = new Configuration();

        for (String resource : resources) 
            configuration.configure(resource);

        return configuration.buildSessionFactory();
    }

}
