package tests.propertyset;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.JDBCPropertySet;
import org.informagen.oswf.propertyset.PropertySetFactory;

// Java - Collections
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Hashtable;

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

@Ignore
public class JNDITest extends AbstractTestClass  {

    static JDBCPropertySet jdbcPropertySet;

    @BeforeClass
    public static void setup() throws Exception {

        OSWfConfiguration configuration = new DefaultOSWfConfiguration()
            .load();

        PropertySetFactory factory = PropertySetFactory.getInstance();

        Map<String,Object> args = new HashMap<String,Object>();
        args.put("globalKey", "UnitTest");

        jdbcPropertySet = (JDBCPropertySet)factory.createPropertySet("jdbc-jndi", args);
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }


    @Before
    public void before() throws Exception {
        
        propertySet = jdbcPropertySet;

        Connection connection = jdbcPropertySet.getConnection();
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
        Connection connection = jdbcPropertySet.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE OS_PROPERTYENTRY");
    }

 
    
    // T E S T S ------------------------------------------------------------------------------

    @Test
    public void supportsType() {
        assertTrue(propertySet.supportsType(Type.BOOLEAN));
        assertTrue(propertySet.supportsType(Type.DATA));
        assertTrue(propertySet.supportsType(Type.DATE));
        assertTrue(propertySet.supportsType(Type.DOUBLE));
        assertTrue(propertySet.supportsType(Type.INT));
        assertTrue(propertySet.supportsType(Type.LONG));
        assertTrue(propertySet.supportsType(Type.OBJECT));
        assertFalse(propertySet.supportsType(Type.PROPERTIES));
        assertTrue(propertySet.supportsType(Type.STRING));
        assertTrue(propertySet.supportsType(Type.TEXT));
        assertFalse(propertySet.supportsType(Type.XML));
    }
    
 
 
    @Test
    @Override
    public void testProperties() {}
   
     @Test
    @Override
    public void testString() {
        
        propertySet.setString("a-string", "A String value");
        propertySet.setString("string", "string");
        propertySet.setString("string-space", " ");
        propertySet.setString("string-empty", "");
        propertySet.setString("string-100", "100");

        assertEquals("A String value", propertySet.getString("a-string"));
        assertNull(propertySet.getString("non.existent.key"));

        if (propertySet.supportsType(Type.STRING)) 
            assertEquals(Type.STRING, propertySet.getType("string"));
   }
   
    @Test
    @Override
    public void testText() {}
 
    @Test
    @Override
    public void testXML()  {}
          
}
