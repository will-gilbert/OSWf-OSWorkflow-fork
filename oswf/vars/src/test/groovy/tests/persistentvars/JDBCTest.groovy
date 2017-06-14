package tests.persistentvars

import org.informagen.oswf.PersistentVars
import org.informagen.oswf.Type
import org.informagen.oswf.JDBCPersistentVars

import org.osjava.sj.loader.SJDataSource

import javax.naming.Context
import javax.naming.InitialContext
import javax.naming.NamingException

// Java - Collections
import java.util.Map
import java.util.HashMap
import java.util.Properties

// Java - SQL Extension
import javax.sql.DataSource
import java.sql.Connection
import java.sql.Statement


// JUnit 4.x testing
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test


class JDBCTest extends AbstractTestClass  {


    static JDBCPersistentVars jdbcPersistentVars

    @BeforeClass
    static void setup() throws Exception {
 
        // Configure Simple-JNDI for testing via System Properties
        // System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory")
        // System.setProperty("org.osjava.sj.root", "target/resources/test/config/")
        // System.setProperty("org.osjava.sj.delimiter", "/")

                
        def config = new HashMap<String,String>()
        config['table.name']    = 'OS_PROPERTYENTRY'
        config['col.globalKey'] = 'GLOBAL_KEY'
        config['col.itemKey']   = 'ITEM_KEY'
        config['col.itemType']  = 'ITEM_TYPE'
        config['col.string']    = 'STRING_VALUE'
        config['col.date']      = 'DATE_VALUE'
        config['col.data']      = 'DATA_VALUE'
        config['col.float']     = 'FLOAT_VALUE'
        config['col.number']    = 'NUMBER_VALUE'

        def args = new HashMap<String,Object>()
        args['globalKey'] = 'UnitTest'
        
        DataSource dataSource = new SJDataSource(
            'org.h2.Driver', 
            'jdbc:h2:mem:workflowDB_CLOSE_DELAY=-1',
            'sa', 
            '', 
            new Properties())
            
        args['datasource'] = dataSource

        jdbcPersistentVars = new JDBCPersistentVars(config, args)
        
    }

    @AfterClass
    static void tearDown() throws Exception {
    }


    @Before
    void before() throws Exception {
        persistentVars = jdbcPersistentVars

        Connection connection = jdbcPersistentVars.getConnection()
        Statement statement = connection.createStatement()
        statement.execute('''
            CREATE TABLE OS_PROPERTYENTRY (
            GLOBAL_KEY varchar(255), 
            ITEM_KEY varchar(255), 
            ITEM_TYPE smallint, 
            STRING_VALUE varchar(255), 
            DATE_VALUE timestamp, 
            DATA_VALUE oid,
            FLOAT_VALUE float8, 
            NUMBER_VALUE numeric, 
            primary key (GLOBAL_KEY, ITEM_KEY)
            )'''.stripMargin()
        )

    }

     @After
    void after() throws Exception {
        Connection connection = jdbcPersistentVars.getConnection()
        Statement statement = connection.createStatement()
        statement.execute('DROP TABLE OS_PROPERTYENTRY')
    }

 
    
    // T E S T S ------------------------------------------------------------------------------

    @Test
    void supportsType() {
        assert persistentVars.supportsType(Type.BOOLEAN)
        assert persistentVars.supportsType(Type.DATA)
        assert persistentVars.supportsType(Type.DATE)
        assert persistentVars.supportsType(Type.DOUBLE)
        assert persistentVars.supportsType(Type.INT)
        assert persistentVars.supportsType(Type.LONG)
        assert persistentVars.supportsType(Type.OBJECT)
        assert persistentVars.supportsType(Type.PROPERTIES) == false
        assert persistentVars.supportsType(Type.STRING)
        assert persistentVars.supportsType(Type.TEXT)
        assert persistentVars.supportsType(Type.XML) == false
    }
    
 
 
    @Test
    @Override
    void testProperties() {}
   
     @Test
    @Override
    void testString() {
        
        persistentVars.setString('a-string', 'A String value')
        persistentVars.setString('string', 'string')
        persistentVars.setString('string-space', ' ')
        persistentVars.setString('string-empty', '')
        persistentVars.setString('string-100', '100')

        assert "A String value" == persistentVars.getString('a-string')
        assert persistentVars.getString('non.existent.key') == null

        if (persistentVars.supportsType(Type.STRING)) 
            assert Type.STRING == persistentVars.getType('string')
   }
   
    @Test
    @Override
    void testText() {}
 
    @Test
    @Override
    void testXML()  {}
          
}
