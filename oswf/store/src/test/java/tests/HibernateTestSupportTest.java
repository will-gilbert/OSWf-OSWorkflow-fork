package tests;

import support.HibernateTestSupport;

// Hibernate
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


// JUnit 4.4
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

// Java Reflection
import java.lang.reflect.Field;


import java.lang.NoSuchFieldException;
import java.util.List;

public class HibernateTestSupportTest {

    public static final String RDBMS_CONFIGURATION = System.getProperty("rdbms-configuration");

    HibernateTestSupport hibernateTestSupport;
    
    @Before
    public void createHibernateTestCase() {
        hibernateTestSupport = new HibernateTestSupport(RDBMS_CONFIGURATION);
    }


    @Test
    // 'getSession' will create a session if one does not exist
    public void getSession() {
        assertNull(getPrivateSession());
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSession());
    }


    @Test
    public void closeSession() {
    
        assertNull(getPrivateSession());
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSession());
        
        hibernateTestSupport.closeSession();
        assertNull(getPrivateSession());
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSession());
    }

    @Test
    public void closeSessionFactory() {
    
        // Nothig exists yet
        assertNull(getPrivateSessionFactory());
        assertNull(getPrivateSession());
        
        // Create a SessionFactory and a Session
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSession());
        
        hibernateTestSupport.closeSessionFactory();
        assertNull(getPrivateSession());
        assertNull(getPrivateSessionFactory());
    }

    @Ignore
    public void reopenSessionFactory() {
    
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSessionFactory());

        hibernateTestSupport.closeSessionFactory();
        assertNull(getPrivateSessionFactory());
        
        hibernateTestSupport.getSession();
        assertNotNull(getPrivateSessionFactory());
    
    }


    // Use reflection to get the private variable 'session' -----------------------------------
    private Session getPrivateSession() {
    
        Session session = null;
        try {
            session = (Session)getField(hibernateTestSupport, "session");    
        } catch (NoSuchFieldException noSuchFieldException) {
            fail(noSuchFieldException.getMessage());
        }

        return session;        
    }

    // Use reflection to access the private field 'sessionFactory' ----------------------------
    private SessionFactory getPrivateSessionFactory() {
    
        SessionFactory sessionFactory = null;
        try {
            sessionFactory = (SessionFactory)getField(hibernateTestSupport, "sessionFactory");    
        } catch (NoSuchFieldException noSuchFieldException) {
            fail(noSuchFieldException.getMessage());
        }

        return sessionFactory;        
    }


    // Use reflection to access the private fields from an object  ============================

    private Object getField(Object object, String name) throws NoSuchFieldException {
    
        if (object == null) 
            throw new IllegalArgumentException("Invalid null object argument");

        for (Class cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
                 
            try {
                Field field = cls.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception ex) {
                // in case of an exception, we will throw a new
                // NoSuchFieldException object below
                ;
            }
        }

        // Arrived here because we couldn't find the field or an exception
        //   was thrown during field access.
        
        throw new NoSuchFieldException("Could get value for field " +
                object.getClass().getName() + "." + name);
    }


}
