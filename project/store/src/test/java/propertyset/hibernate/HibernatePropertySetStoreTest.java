package propertyset.hibernate;


import org.informagen.oswf.impl.HibernatePropertySetStore;

// OSWf - core
import org.informagen.oswf.PropertySetStore;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowStoreException;


// OpenSymphony PropertySet 1.4
import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.PropertySetFactory;

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


public class HibernatePropertySetStoreTest  {

    private static final String RDBMS_CONFIGURATION = "H2.hibernate.xml";
    //private static final String RDBMS_CONFIGURATION = "MySQL.hibernate.xml";


    long piid = 1L;

    @BeforeClass
    public static void createConfiguration() throws Exception {
        // Parse 'oswf.xml' resource file
        new DefaultOSWfConfiguration().load();
    }

    @Test
    public void configureWithSessionFactory()  {

        SessionFactory sessionFactory = createSessionFactory("oswf-propertyset.cfg.xml", RDBMS_CONFIGURATION);
        
        // Usings the default PropertySet name, 'hibernate'
        PropertySetStore store = new HibernatePropertySetStore(sessionFactory);

        PropertySet propertySet = store.getPropertySet(piid);
        checkPropertySet(propertySet);
        
        sessionFactory.close();
    }


    @Test
    public void configureWithPropertySetNameAndSessionFactory() throws ParseException  {
    
        SessionFactory sessionFactory = createSessionFactory("oswf-propertyset.cfg.xml", RDBMS_CONFIGURATION);
        
        // Inject 'SessionFactory'
        PropertySetStore store = new HibernatePropertySetStore("US-Eastern", sessionFactory);

        PropertySet propertySet = store.getPropertySet(piid);
        checkPropertySet(propertySet);
        
        // February 22, 2013 3:23pm on European continent expressed as 
        //   day.Month.year 24 hour clock
        
        Date testDate = new SimpleDateFormat("dd.MM.yy HH:mm Z").parse("22.02.13 15:23 +0100");        
        propertySet.setDate("test-date", testDate);
        assertEquals("Fri, 22-Feb-2013 09:23 -0500", propertySet.getString("test-date"));
        
        sessionFactory.close();
        
    }

    @Test
    public void configureWithPropertySetName()  {

        // SessionFactory is configured and created via XML parameters
        PropertySetStore store = new HibernatePropertySetStore("H2-Hibernate-cfg");

        PropertySet propertySet = store.getPropertySet(piid);
        checkPropertySet(propertySet);
 
        // Extract and close from the PropertySetStore
        ((HibernatePropertySetStore)store).getSessionFactory().close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private SessionFactory createSessionFactory(String... resources) {
        Configuration configuration = new Configuration();

        for (String resource : resources) 
            configuration.configure(resource);

        return configuration.buildSessionFactory();
    }


    private void checkPropertySet(PropertySet propertySet) {
        propertySet.setString("string", "Hello, World");
        assertEquals("Hello, World", propertySet.getString("string"));

        propertySet.setInt("int", 42);
        assertEquals(42, propertySet.getInt("int"));
        assertEquals("42", propertySet.getString("int"));
    }


}
