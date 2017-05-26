package tests;

import org.informagen.oswf.testing.OSWfHibernateTestCase;

// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

// OSWf - Loaders
import org.informagen.oswf.impl.loaders.HibernateProcessDescription;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - IO
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

// Hibernate
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Criteria;

// Java - JNDI
import javax.naming.InitialContext;
import javax.naming.NamingException;

// Java - Collections
import java.util.List;

// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
 
 
public class LoaderTest extends OSWfHibernateTestCase {

    private static final String RDBMS_CONFIGURATION = "H2.hibernate.xml";

    private static String holiday;

    OSWfConfiguration configuration;


    @BeforeClass
    public static void readProcessDescription() throws Exception {

        // Read a processs definition (workflow) from the test resources
        File xmlWorkflow = new File("src/test/resources/usage/holiday.xml");
        BufferedReader reader = new BufferedReader(new FileReader(xmlWorkflow));
        String ls = System.getProperty("line.separator");
        StringBuilder  stringBuilder = new StringBuilder();

        String line = null;
        while( (line = reader.readLine() ) != null )
            stringBuilder.append(line).append(ls);

        holiday = stringBuilder.toString();
    }


    public LoaderTest() {
        super("oswf-store.cfg.xml",
              "oswf-propertyset.cfg.xml",
              RDBMS_CONFIGURATION);
    }

    @Before
    public void setup() throws Exception {
        loadDatabase();
        createConfiguration();
    }

    @After
    public void teardown() {
        closeSession();
        closeSessionFactory();
    }

    void loadDatabase() throws Exception {
            
            Session session = null;
            try {
                session = getSession();
                HibernateProcessDescription pd = new HibernateProcessDescription();
                pd.setWorkflowName("Holiday-0001");
                pd.setContent(holiday);
                session.save(pd);
            } finally {
                if(session != null)
                    session.close();
            }  
	}
        
    void createConfiguration() throws Exception {
        configuration = new DefaultOSWfConfiguration()
            .addPersistenceArg("sessionFactory", getSessionFactory())
            .load(LoaderTest.class.getResource("/oswf-LoaderTest.xml"))
        ;
    }

    // Tests ====================================================================

    @Test
    public void verifyDatabaseLoaded() throws Exception {
        
        Session session = null;
        try {
            session = getSession();
            Criteria criteria = session.createCriteria(HibernateProcessDescription.class);
            List list = criteria.list();
            assertNotNull(list);
            assertTrue(list.size() == 1);

        } finally {
            if(session != null)
                session.close();
        }  
        
    }
    
    @Test
    public void loadHoliday() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("LoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday", 1));
    }
    
    
    @Test
    public void loadUnregisteredWorkflow() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("LoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday-0001", 1));
    }
    


}
