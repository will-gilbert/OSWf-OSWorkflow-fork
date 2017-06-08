package propertyset.hibernate;


import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.typedmap.hibernate.HibernateConfigurationProvider;
import org.informagen.typedmap.HibernateTypedMap;
import org.informagen.oswf.exceptions.WorkflowStoreException;


// OSWf Typed Map
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
import java.util.Random;

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


/**
 *  This set of test demonstrates through Unit tests the many ways to configure the
 *      Hibernate 3.x PropertySet
 *
 *      - Build and supply a Hibernate SessionFactory
 *      - Create a Configuration Provider object; pass arguments via a Map
 *      - Create with arguments pass via a Map; Default Configuration Provider is created
 *      - Pass classname and arguments via a Map; Configuration Provider is instance via reflection
 *      - Pass all Hibernate arguments via a Map
 *      - Configure via the 'propertyset.xml' file by looking up a named configuration
 */


public class ConfigureHibernatePropertySetTest  {


    public static final String RDBMS_CONFIGURATION = "H2.hibernate.xml"; //System.getProperty("rdbms-configuration");

    static final Random random = new Random();

    /**
     * A set of Hibernate configuration files; the first file contains only the HBM(s) which
     *      define the ORM mappings.  In the case of PropertySet there is only one mapping.
     *      The subsequent files provide the parameters for setting up the database connection
     *      and/or connection pool for the SessionFactory.
     */

     // NB: Use a single comma as the resource separator, no extra spacing please.
     //     (HBM mapping file, database configuration)

    static final String resources = "oswf-propertyset.cfg.xml," + RDBMS_CONFIGURATION;
//    static final String resources = "oswf-propertyset.cfg.xml,MySQL.hibernate.xml";
        

    TypedMap propertySet;

    @BeforeClass
    public static void createConfiguration() throws Exception {
        new DefaultOSWfConfiguration().load();
    }
    

    /**
     * After each test close the SessionFactoy to be sure we really are creating a new
     *      SessionFactory with each test configuration
     */

    @After
    public void closeSessionFactory() {
        HibernateConfigurationProvider provider = ((HibernateTypedMap)propertySet).getConfigurationProvider(); 
        provider.getSessionFactory().close();
    }


    @Test
    public void configureWithSessionFactory() {
    
        String[] resourceList = resources.split(",");
        
        // Hibernate Configurations
        Configuration configuration = new Configuration();

        for (String resource : resourceList) 
            configuration.configure(resource);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        
        Map<String,Object> args = new HashMap<String,Object>();

        args.put("processInstanceId", random.nextLong()); 
        args.put("configurationProvider", new HibernateConfigurationProvider(sessionFactory));
    
        propertySet = new HibernateTypedMap(null, args);

        assertTypedMap(propertySet);
    }

    @Test
    public void configureWithConfigurationProvider() {
            
        Map<String,Object> args = new HashMap<String,Object>();

        args.put("processInstanceId", random.nextLong()); 
        args.put("configurationProvider", new HibernateConfigurationProvider());

        Map<String,String> config = new HashMap<String,String>();
        config.put("resources", resources);
    
        propertySet = new HibernateTypedMap(config, args);

        assertTypedMap(propertySet);
    }


    @Test
    public void configureWithResources() {
            
        Map<String,Object> args = new HashMap<String,Object>();

        args.put("processInstanceId", random.nextLong()); 

        Map<String,String> config = new HashMap<String,String>();
        config.put("resources", resources);
    
        propertySet = new HibernateTypedMap(config, args);

        assertTypedMap(propertySet);
    }

    @Test
    public void configureWithReferencedProviderClass() {
            
        Map<String,Object> args = new HashMap<String,Object>();

        args.put("processInstanceId", random.nextLong()); 

        Map<String,String> config = new HashMap<String,String>();
        config.put("configuration.provider.class","org.informagen.typedmap.hibernate.HibernateConfigurationProvider");
        config.put("resources", resources);
    
        propertySet = new HibernateTypedMap(config, args);

        assertTypedMap(propertySet);
    }

    @Test
    public void configureWithConfigurationMap() {
            
        Map<String,Object> args = new HashMap<String,Object>();

        args.put("processInstanceId", random.nextLong()); 

        Map<String,String> config = new HashMap<String,String>();

        // All Hibernate properties; no external files
        config.put("hibernate.connection.driver_class", "org.h2.Driver");
        config.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.put("hibernate.connection.url", "jdbc:h2:mem:propertyset;DB_CLOSE_DELAY=-1");
        config.put("hibernate.connection.username", "sa");
        config.put("hibernate.connection.password", "");
        config.put("hibernate.hbm2ddl.auto", "create-drop");
        config.put("hibernate.connection.autocommit", "true");
        config.put("hibernate.connection.pool_size", "1");
        
        // Add Hibernate Mapping file(s)
        config.put("mapping-TypedMap", "org/informagen/oswf/hbm/HibernateTypedMapItem.hbm.xml");

        propertySet = new HibernateTypedMap(config, args);

        assertTypedMap(propertySet);

    }

    @Test
    public void configureWithTypedMapName()  {

        Map<String,Object> args = new HashMap<String,Object>();
        args.put("processInstanceId", random.nextLong()); 
    
        propertySet = TypedMapFactory.getInstance().createTypedMap("H2-Hibernate-cfg", args);    
        assertTypedMap(propertySet);
    }

    //=========================================================================================

    /*
     *  Just assert that the TypedMap works; See 'HibernateTypedMapTest.java' for
     *      an exhaustive set of tests on this TypedMap Implementation.
     */

    private void assertTypedMap(TypedMap propertySet) {
        propertySet.setString("string", "Hello, World");
        assertEquals("Hello, World", propertySet.getString("string"));

        propertySet.setInt("int", 42);
        assertEquals(42, propertySet.getInt("int"));
        assertEquals("42", propertySet.getString("int"));
    }


}
