package propertyset.hibernate;


import org.informagen.oswf.impl.HibernateTypedMapStore;

// OSWf - core
import org.informagen.oswf.TypedMapStore;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowStoreException;


// OSWf - Typed Map
import org.informagen.typedmap.TypedMap;
import org.informagen.typedmap.TypedMapFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.lang.StringBuffer;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import java.text.SimpleDateFormat;
import java.text.ParseException;

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


public class HibernateTypedMapStoreTest  {

    public static final String RDBMS_CONFIGURATION = "H2.hibernate.xml"; //System.getProperty("rdbms-configuration");

    // To get this to work with MySQL we need to either remove the 'processvariable.piid'
    //   foreign key to 'processinstance.piid' OR
    // Create and initialize a process instance and then do the tests

    long piid = 1L;

    @BeforeClass
    public static void createConfiguration() throws Exception {
        // Parse 'oswf.xml' resource file
        new DefaultOSWfConfiguration().load();
    }

    @Test
    public void configureWithSessionFactory()  {

        SessionFactory sessionFactory = createSessionFactory("oswf-propertyset.cfg.xml", RDBMS_CONFIGURATION);
        
        // Usings the default TypedMap name, 'hibernate'
        TypedMapStore store = new HibernateTypedMapStore(sessionFactory);

        TypedMap propertySet = store.getTypedMap(piid);
        checkTypedMap(propertySet);
        
        sessionFactory.close();
    }


    @Test
    public void configureWithTypedMapNameAndSessionFactory() throws ParseException  {
    
        SessionFactory sessionFactory = createSessionFactory("oswf-propertyset.cfg.xml", RDBMS_CONFIGURATION);
        
        // Inject 'SessionFactory'
        TypedMapStore store = new HibernateTypedMapStore("US-Eastern", sessionFactory);

        TypedMap propertySet = store.getTypedMap(piid);
        checkTypedMap(propertySet);
        
        // February 22, 2013 3:23pm on European continent expressed as 
        //   day.Month.year 24 hour clock
        
        Date testDate = new SimpleDateFormat("dd.MM.yy HH:mm Z").parse("22.02.13 15:23 +0100");        
        propertySet.setDate("test-date", testDate);
        assertEquals("Fri, 22-Feb-2013 09:23 -0500", propertySet.getString("test-date"));
        
        sessionFactory.close();
        
    }

    @Test
    public void configureWithTypedMapName()  {

        // SessionFactory is configured and created via XML parameters
        TypedMapStore store = new HibernateTypedMapStore("H2-Hibernate-cfg");

        TypedMap propertySet = store.getTypedMap(piid);
        checkTypedMap(propertySet);
 
        // Extract and close from the TypedMapStore
        ((HibernateTypedMapStore)store).getSessionFactory().close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private SessionFactory createSessionFactory(String... resources) {
        Configuration configuration = new Configuration();

        for (String resource : resources) 
            configuration.configure(resource);

        return configuration.buildSessionFactory();
    }


    private void checkTypedMap(TypedMap propertySet) {
        propertySet.setString("string", "Hello, World");
        assertEquals("Hello, World", propertySet.getString("string"));

        propertySet.setInt("int", 42);
        assertEquals(42, propertySet.getInt("int"));
        assertEquals("42", propertySet.getString("int"));
    }


}
