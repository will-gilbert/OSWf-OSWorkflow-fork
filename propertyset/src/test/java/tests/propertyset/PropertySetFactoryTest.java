package tests.propertyset;

// OSWf Core
// import org.informagen.oswf.OSWfConfiguration;
// import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf Exceptions
// import org.informagen.oswf.exceptions.WorkflowLoaderException;


import tests.propertyset.TestBean;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.PropertySetFactory;
import org.informagen.oswf.propertyset.exceptions.PropertyImplementationException;


// Java - Collections
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// JUnit 4.x testing
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class PropertySetFactoryTest {


    // @Before
    // public void configureOSWf() throws WorkflowLoaderException {
    //     new MemoryOSWfConfiguration().load();
    // }


    @Test
    public void getInstance() {
        PropertySetFactory instance = PropertySetFactory.getInstance();
        assertNotNull(instance);
    }


    @Test
    public void getBuiltInPropertySets() {
        
        PropertySetFactory factory = PropertySetFactory.getInstance();
        assertNotNull(factory);
        
        assertEquals(factory.getClassName("aggregate"), "org.informagen.oswf.propertyset.AggregatePropertySet");
        assertEquals(factory.getClassName("proxy"), "org.informagen.oswf.propertyset.ProxyPropertySet");
        assertEquals(factory.getClassName("bean"), "org.informagen.oswf.propertyset.BeanPropertySet");
        assertEquals(factory.getClassName("map"), "org.informagen.oswf.propertyset.MapPropertySet");
        assertEquals(factory.getClassName("memory"), "org.informagen.oswf.propertyset.MemoryPropertySet");
        assertEquals(factory.getClassName("serializable"), "org.informagen.oswf.propertyset.SerializablePropertySet");
        assertEquals(factory.getClassName("xml"), "org.informagen.oswf.propertyset.XMLPropertySet");
} 
 
    @Ignore
    public void getAdditionalPropertySets() {
        
        PropertySetFactory factory = PropertySetFactory.getInstance();
        assertNotNull(factory);
        assertEquals(factory.getClassName("jdbc"), "org.informagen.oswf.propertyset.JDBCPropertySet");
        assertEquals(factory.getClassName("jdbc-jndi"), "org.informagen.oswf.propertyset.JDBCPropertySet");
    }

    @Ignore
    public void getParameters() {
        
        PropertySetFactory config = PropertySetFactory.getInstance();
        assertNotNull(config);
        assertEquals(config.getClassName("jdbc-jndi"), "org.informagen.oswf.propertyset.JDBCPropertySet");

        Map<String,String> parameters = config.getParameters("jdbc-jndi");
        assertNotNull(parameters);
        assertTrue(parameters.containsKey("jndi"));
        assertEquals(parameters.get("jndi"), "jdbc/H2");

    }
    
    @Test
    public void createNoArgsPropertySet() {
        
        PropertySetFactory factory = PropertySetFactory.getInstance();
        assertNotNull(factory); 
        
        assertNotNull(factory.createPropertySet("serializable")); 
        assertNotNull(factory.createPropertySet("memory")); 
        assertNotNull(factory.createPropertySet("map")); 
        assertNotNull(factory.createPropertySet("xml")); 
    }
    
    @Test 
    public void createBeanPropertySet() {
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("bean", new TestBean());
        PropertySetFactory factory = PropertySetFactory.getInstance();
        assertNotNull(factory.createPropertySet("bean", args)); 
    }
    

    //@Test(expected=PropertyImplementationException.class)
    public void missingBean() {
        PropertySetFactory factory = PropertySetFactory.getInstance();
        factory.createPropertySet("bean");
    }
    
    @Test 
    public void createProxyPropertySet() {
        
        PropertySetFactory factory = PropertySetFactory.getInstance();
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("PropertySet", factory.createPropertySet("memory"));
        assertNotNull(factory.createPropertySet("proxy", args)); 

        args.put("PropertySet", factory.createPropertySet("memory"));
        args.put("proxyName", "serializable");
        assertNotNull(factory.createPropertySet("proxy", args)); 

        args.put("PropertySet", factory.createPropertySet("memory"));
        args.put("bulkload", Boolean.TRUE);
        assertNotNull(factory.createPropertySet("proxy", args)); 

    }

    @Ignore
    public void createJDBCPropertySet() {

        PropertySetFactory factory = PropertySetFactory.getInstance();

        Map<String,Object> args = new HashMap<String,Object>();
        args.put("globalKey", "testing");

        assertNotNull(factory.createPropertySet("jdbc", args));        
    }


    @Ignore
    public void createJNDIPropertySet() {

        PropertySetFactory factory = PropertySetFactory.getInstance();

        Map<String,Object> args = new HashMap<String,Object>();
        args.put("globalKey", "testing");

        assertNotNull(factory.createPropertySet("jdbc-jndi", args));        
    }



    @Test
    public void createAggregatePropertySet() {
        
        PropertySetFactory factory = PropertySetFactory.getInstance();
        
        List<PropertySet> propertySets = new ArrayList<PropertySet>();
        
        // Build an ordered list of PropertySets
        propertySets.add(factory.createPropertySet(("memory")));   
        propertySets.add(factory.createPropertySet(("map")));
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("PropertySets", propertySets);
        
        assertNotNull(factory.createPropertySet("aggregate", args));        
    }

    
}
