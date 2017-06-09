package tests;


import org.informagen.oswf.testing.OSWfTestCase;


// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf - Store
import org.informagen.oswf.impl.stores.MemoryStore;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - IO
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

// Java - SQL
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Java - JNDI
import javax.naming.InitialContext;
import javax.naming.NamingException;


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


/**
 *
 */
 
@Ignore 
public class JDBCLoaderTest extends OSWfTestCase {

    private static final Logger testlogger = LoggerFactory.getLogger(JDBCLoaderTest.class);

    private final static String CREATE_DATABASE = "CREATE TABLE WORKFLOW (IDENTIFIER VARCHAR(200),  DEFINITION CLOB)";
    private final static String INSERT_WORKFLOW = "INSERT INTO WORKFLOW (IDENTIFIER, DEFINITION) VALUES (?,?)";


    static OSWfConfiguration configuration;
    
    // H2 keeps the database available in memory as long as there is at least one
    //   connection.  Create the following JDBC Connection when the database is 
    //   created and close it when these tests are complete i.e. 'AfterClass'
    
    static Connection connection;

    @BeforeClass
    public static void setupDatabase() throws Exception {
        connection = createDatabase();
        createConfiguration();
    }

    @AfterClass
    public static void closeDatabase() throws Exception {
        // This will relase all the memory resouces used by H2
        connection.close();
    }

    static void createConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(StubTypeResolverTest.class.getResource("/oswf-JDBCTest.xml"))
        ;
    }

    static Connection createDatabase() throws Exception {

            DataSource ds = (DataSource) new InitialContext().lookup("jdbc/H2");
            Connection connection = ds.getConnection();

            // Create a simple table, no surrogate key just Workflow 'identifier' and process definition
			connection.createStatement().execute(CREATE_DATABASE);
 
            // Read a processs definition (workflow) from the test resources
            File xmlWorkflow = new File("src/test/resources/usage/holiday.oswf.xml");
            BufferedReader reader = new BufferedReader(new FileReader(xmlWorkflow));
            String ls = System.getProperty("line.separator");
            StringBuilder  stringBuilder = new StringBuilder();

            String line = null;
            while( (line = reader.readLine() ) != null )
                stringBuilder.append(line).append(ls);
             
			// Insert the process definition into the database.
            //   NB: The workflow database 'NAME" does not have to be the same as the 
            //   workflow name.  See the configuration file 'oswf-JDBCTest.xml'
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WORKFLOW);
            preparedStatement.setString(1, "Holiday-0001");
            preparedStatement.setString(2, stringBuilder.toString());
            
            preparedStatement.executeUpdate();
            connection.commit();
            
            return connection;
	}
        


    // Constructors ///////////////////////////////////////////////////////////

    public JDBCLoaderTest() {
        super(testlogger);
    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }

    // Tests ====================================================================
    
    @Test
    public void readXMLasJDBC() {
        
        OSWfEngine wfEngine = new DefaultOSWfEngine("JDBCLoaderTest");
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize("Holiday as JDBC", 1));
    }
    


}
