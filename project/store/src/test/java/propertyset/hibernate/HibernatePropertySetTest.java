package propertyset.hibernate;


// Super class tests
import propertyset.PropertySetBase;

import org.informagen.oswf.propertyset.hibernate.HibernateConfigurationProvider;

import org.informagen.oswf.propertyset.HibernatePropertySet;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;


import java.lang.StringBuffer;
import java.util.Date;
import java.util.Random;

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


public class HibernatePropertySetTest extends PropertySetBase {


    private static final String RDBMS_CONFIGURATION = "H2.hibernate.xml";
    //private static final String RDBMS_CONFIGURATION = "MySQL.hibernate.xml";


    private SessionFactory sessionFactory = null;
    static final Random random = new Random();
    

    @Before
    @SuppressWarnings("unchecked")
    public void createPropertySet() {
    
        Map<String,Object> args = new HashMap<String,Object>();

        sessionFactory = createSessionFactory();

        args.put("processInstanceId",  random.nextLong()); 
        args.put("configurationProvider", new HibernateConfigurationProvider(sessionFactory));
    
        propertySet = new HibernatePropertySet(Collections.EMPTY_MAP, args);
    }

    @After
    public void closeSessionFactory() {
        if(sessionFactory != null)
            sessionFactory.close();
            
        sessionFactory = null;
    }

    @Test
    public void convertToBoolean() {
    
        loadPropertySet();
 
        assertTrue(propertySet.getBoolean("int"));
        assertFalse(propertySet.getBoolean("int-zero"));
        
        assertTrue(propertySet.getBoolean("double"));
        assertFalse(propertySet.getBoolean("double-zero"));

        assertTrue(propertySet.getBoolean("string"));
        assertFalse(propertySet.getBoolean("string-empty"));
        assertFalse(propertySet.getBoolean("string-null"));
        assertTrue(propertySet.getBoolean("string-100"));

        assertTrue(propertySet.getBoolean("text"));
        assertFalse(propertySet.getBoolean("text-empty"));
        assertFalse(propertySet.getBoolean("text-null"));
        assertTrue(propertySet.getBoolean("text-100"));

        // InFuture returns true
        assertFalse(propertySet.getBoolean("date"));    
        assertFalse(propertySet.getBoolean("date-zero"));
        assertTrue(propertySet.getBoolean("date-future"));
    }

    @Test
    public void convertToString() {
    
        loadPropertySet();
        
        assertEquals("true", propertySet.getString("boolean-true"));
        assertEquals("false", propertySet.getString("boolean-false"));
        
        assertEquals("42", propertySet.getString("int"));
        assertEquals("0", propertySet.getString("int-zero"));
        
        assertTrue(propertySet.getString("double").startsWith("1.0"));
        assertTrue(propertySet.getString("double-zero").startsWith("0.0"));
        
        assertEquals("1", propertySet.getString("long"));
        assertEquals("0", propertySet.getString("long-zero"));
        
        assertEquals(255, propertySet.getString("text").length());
        assertEquals("", propertySet.getString("text-empty"));
        assertEquals(null, propertySet.getString("text-null"));
        assertEquals("100", propertySet.getString("text-100"));

        assertEquals("Thu, 01 Jan 1970 02:46:40 +0000", propertySet.getString("date"));
        assertEquals("Thu, 01 Jan 1970 00:00:00 +0000", propertySet.getString("date-zero"));

        assertEquals("java.lang.StringBuffer", propertySet.getString("object"));

    }

    @Test
    public void convertToText() {
    
        loadPropertySet();

        assertEquals("true", propertySet.getText("boolean-true"));
        assertEquals("false", propertySet.getText("boolean-false"));
        
        assertEquals("42", propertySet.getText("int"));
        assertEquals("0", propertySet.getText("int-zero"));
        
        assertTrue(propertySet.getText("double").startsWith("1.0"));
        assertTrue(propertySet.getText("double-zero").startsWith("0.0"));
        
        assertEquals("1", propertySet.getText("long"));
        assertEquals("0", propertySet.getText("long-zero"));
        
        assertEquals("string", propertySet.getText("string"));
        assertEquals("", propertySet.getText("string-empty"));
        assertEquals(null, propertySet.getText("string-null"));
        assertEquals("100", propertySet.getText("string-100"));

        assertEquals("Thu, 01 Jan 1970 02:46:40 +0000", propertySet.getText("date"));
        assertEquals("Thu, 01 Jan 1970 00:00:00 +0000", propertySet.getText("date-zero"));

        assertTrue(propertySet.getText("properties").contains("one=1"));
        assertTrue(propertySet.getText("properties").contains("two=2"));

        assertTrue(propertySet.getText("xml").contains("<list>"));
        assertTrue(propertySet.getText("xml").contains("<item cost=\"100\">Books</item>"));
        assertTrue(propertySet.getText("xml").contains("<item cost=\"200\">Supplies</item>"));
        assertTrue(propertySet.getText("xml").contains("</list>"));
        
        assertEquals("Hello, world", propertySet.getText("object"));
      
    }


    @Test
    public void convertToInt() {
    
        loadPropertySet();

        assertEquals(1, propertySet.getInt("boolean-true"));
        assertEquals(0, propertySet.getInt("boolean-false"));

        assertEquals(42, propertySet.getLong("int"));
        assertEquals(0, propertySet.getLong("int-zero"));
        
        assertEquals(1, propertySet.getInt("double"));
        assertEquals(0, propertySet.getInt("double-zero"));
        
        assertEquals(1, propertySet.getInt("long"));
        assertEquals(0, propertySet.getInt("long-zero"));
        
        assertEquals(0, propertySet.getInt("string"));
        assertEquals(0, propertySet.getInt("string-empty"));
        assertEquals(0, propertySet.getInt("string-null"));
        assertEquals(100, propertySet.getInt("string-100"));

        assertEquals(0, propertySet.getInt("text"));
        assertEquals(0, propertySet.getInt("text-empty"));
        assertEquals(0, propertySet.getInt("text-null"));
        assertEquals(100, propertySet.getInt("text-100"));

        try {
            propertySet.getInt("date");
            fail("No conversion from date to int");
        } catch (PropertySetException pe) {
            assertEquals("Key 'date' of type 'DATE' does not have conversion to type 'INT'",
                        pe.getMessage());
        }
    }


    @Test
    public void convertToDouble() {
    
        loadPropertySet();

        assertEquals(1.0, propertySet.getDouble("boolean-true"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("boolean-false"), 0.0001);

        assertEquals(42.0, propertySet.getDouble("int"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("int-zero"), 0.0001);
        
        assertEquals(1.0, propertySet.getDouble("double"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("double-zero"), 0.0001);
        
        assertEquals(1.0, propertySet.getDouble("long"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("long-zero"), 0.0001);
        
        assertEquals(0.0, propertySet.getDouble("string"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("string-empty"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("string-null"), 0.0001);
        assertEquals(100.0, propertySet.getDouble("string-100"), 0.0001);

        assertEquals(0.0, propertySet.getDouble("text"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("text-empty"), 0.0001);
        assertEquals(0.0, propertySet.getDouble("text-null"), 0.0001);
        assertEquals(100.0, propertySet.getDouble("text-100"), 0.0001);

        try {
            propertySet.getDouble("date");
            fail("No conversion from date to double");
        } catch (PropertySetException pe) {
            assertEquals("Key 'date' of type 'DATE' does not have conversion to type 'DOUBLE'", 
                       pe.getMessage());
        }

    }

    @Test
    public void convertToLong() {
    
        loadPropertySet();

        assertEquals(1L, propertySet.getLong("boolean-true"));
        assertEquals(0L, propertySet.getLong("boolean-false"));
        
        assertEquals(42L, propertySet.getLong("int"));
        assertEquals(0L, propertySet.getLong("int-zero"));
        
        assertEquals(1L, propertySet.getLong("double"));
        assertEquals(0L, propertySet.getLong("double-zero"));
        
        assertEquals(1L, propertySet.getLong("long"));
        assertEquals(0L, propertySet.getLong("long-zero"));
        
        assertEquals(0L, propertySet.getLong("string"));
        assertEquals(0L, propertySet.getLong("string-empty"));
        assertEquals(0L, propertySet.getLong("string-null"));
        assertEquals(100L, propertySet.getLong("string-100"));

        assertEquals(0L, propertySet.getLong("text"));
        assertEquals(0L, propertySet.getLong("text-empty"));
        assertEquals(0L, propertySet.getLong("text-null"));
        assertEquals(100L, propertySet.getLong("text-100"));

        assertEquals(10000L, propertySet.getLong("date"));
        assertEquals(0L, propertySet.getLong("date-zero"));
    }

    
    private SessionFactory createSessionFactory() {
    
        closeSessionFactory();

        String[] resources = {"oswf-propertyset.cfg.xml", RDBMS_CONFIGURATION};
        
        Configuration configuration = new Configuration();

        for (String resource : resources) 
            configuration.configure(resource);

        return configuration.buildSessionFactory();
    }

}
