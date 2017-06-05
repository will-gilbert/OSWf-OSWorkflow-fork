package tests.typedmap;

import org.informagen.typedmap.TypedMap;
import org.informagen.typedmap.Type;
import org.informagen.typedmap.JDBCTypedMap;

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


    static JDBCTypedMap jdbcTypedMap;

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

        jdbcTypedMap = new JDBCTypedMap(config, args);
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }


    @Before
    public void before() throws Exception {
        typedMap = jdbcTypedMap;

        Connection connection = jdbcTypedMap.getConnection();
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
        Connection connection = jdbcTypedMap.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE OS_PROPERTYENTRY");
    }

 
    
    // T E S T S ------------------------------------------------------------------------------

    @Test
    public void supportsType() {
        assertTrue(typedMap.supportsType(Type.BOOLEAN));
        assertTrue(typedMap.supportsType(Type.DATA));
        assertTrue(typedMap.supportsType(Type.DATE));
        assertTrue(typedMap.supportsType(Type.DOUBLE));
        assertTrue(typedMap.supportsType(Type.INT));
        assertTrue(typedMap.supportsType(Type.LONG));
        assertTrue(typedMap.supportsType(Type.OBJECT));
        assertFalse(typedMap.supportsType(Type.PROPERTIES));
        assertTrue(typedMap.supportsType(Type.STRING));
        assertTrue(typedMap.supportsType(Type.TEXT));
        assertFalse(typedMap.supportsType(Type.XML));
    }
    
 
 
    @Test
    @Override
    public void testProperties() {}
   
     @Test
    @Override
    public void testString() {
        
        typedMap.setString("a-string", "A String value");
        typedMap.setString("string", "string");
        typedMap.setString("string-space", " ");
        typedMap.setString("string-empty", "");
        typedMap.setString("string-100", "100");

        assertEquals("A String value", typedMap.getString("a-string"));
        assertNull(typedMap.getString("non.existent.key"));

        if (typedMap.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, typedMap.getType("string"));
   }
   
    @Test
    @Override
    public void testText() {}
 
    @Test
    @Override
    public void testXML()  {}
          
}