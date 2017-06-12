package tests.persistentvars;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.Type;
import org.informagen.oswf.JDBCPersistentVars;

import org.osjava.sj.loader.SJDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

// Java - Collections
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

// Java - SQL Extension
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;


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

public class JDBCTest extends AbstractTestClass  {


    static JDBCPersistentVars jdbcPersistentVars;

    @BeforeClass
    public static void setup() throws Exception {
 
        // Configure Simple-JNDI for testing via System Properties
        // System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory");
        // System.setProperty("org.osjava.sj.root", "target/resources/test/config/");
        // System.setProperty("org.osjava.sj.delimiter", "/");

                
        Map<String,String> config = new HashMap<String,String>();
        config.put("table.name","OS_PROPERTYENTRY");
        config.put("col.globalKey","GLOBAL_KEY");
        config.put("col.itemKey","ITEM_KEY");
        config.put("col.itemType","ITEM_TYPE");
        config.put("col.string","STRING_VALUE");
        config.put("col.date","DATE_VALUE");
        config.put("col.data","DATA_VALUE");
        config.put("col.float","FLOAT_VALUE");
        config.put("col.number","NUMBER_VALUE");

        Map<String,Object> args = new HashMap<String,Object>();
        args.put("globalKey","UnitTest");
        
        DataSource dataSource = new SJDataSource("org.h2.Driver", 
            "jdbc:h2:mem:workflow;DB_CLOSE_DELAY=-1",
            "sa", "", 
            new Properties());
            
        args.put("datasource", dataSource);

        jdbcPersistentVars = new JDBCPersistentVars(config, args);
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }


    @Before
    public void before() throws Exception {
        persistentVars = jdbcPersistentVars;

        Connection connection = jdbcPersistentVars.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(new StringBuilder()
            .append("CREATE TABLE OS_PROPERTYENTRY (")
            .append("GLOBAL_KEY varchar(255), ")
            .append("ITEM_KEY varchar(255), ")
            .append("ITEM_TYPE smallint, ")
            .append("STRING_VALUE varchar(255), ")
            .append("DATE_VALUE timestamp, ")
            .append("DATA_VALUE oid,")
            .append("FLOAT_VALUE float8, ")
            .append("NUMBER_VALUE numeric, ")
            .append("primary key (GLOBAL_KEY, ITEM_KEY)")
            .append(")")
            .toString()
        );

    }

     @After
    public void after() throws Exception {
        Connection connection = jdbcPersistentVars.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE OS_PROPERTYENTRY");
    }

 
    
    // T E S T S ------------------------------------------------------------------------------

    @Test
    public void supportsType() {
        assertTrue(persistentVars.supportsType(Type.BOOLEAN));
        assertTrue(persistentVars.supportsType(Type.DATA));
        assertTrue(persistentVars.supportsType(Type.DATE));
        assertTrue(persistentVars.supportsType(Type.DOUBLE));
        assertTrue(persistentVars.supportsType(Type.INT));
        assertTrue(persistentVars.supportsType(Type.LONG));
        assertTrue(persistentVars.supportsType(Type.OBJECT));
        assertFalse(persistentVars.supportsType(Type.PROPERTIES));
        assertTrue(persistentVars.supportsType(Type.STRING));
        assertTrue(persistentVars.supportsType(Type.TEXT));
        assertFalse(persistentVars.supportsType(Type.XML));
    }
    
 
 
    @Test
    @Override
    public void testProperties() {}
   
     @Test
    @Override
    public void testString() {
        
        persistentVars.setString("a-string", "A String value");
        persistentVars.setString("string", "string");
        persistentVars.setString("string-space", " ");
        persistentVars.setString("string-empty", "");
        persistentVars.setString("string-100", "100");

        assertEquals("A String value", persistentVars.getString("a-string"));
        assertNull(persistentVars.getString("non.existent.key"));

        if (persistentVars.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, persistentVars.getType("string"));
   }
   
    @Test
    @Override
    public void testText() {}
 
    @Test
    @Override
    public void testXML()  {}
          
}
