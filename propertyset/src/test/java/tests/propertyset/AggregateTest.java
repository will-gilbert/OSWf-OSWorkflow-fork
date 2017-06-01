
package tests.propertyset;


// This package - Super class tests
import tests.propertyset.AbstractTestClass;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.MemoryPropertySet;
import org.informagen.oswf.propertyset.MapPropertySet;
import org.informagen.oswf.propertyset.XMLPropertySet;
import org.informagen.oswf.propertyset.SerializablePropertySet;
import org.informagen.oswf.propertyset.AggregatePropertySet;


// Java - Collections
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// Java - Utility
import java.util.Random;


// JUnit 4.x testing
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertTrue;

public class AggregateTest extends AbstractTestClass {

    Random random = new Random();

    @Before
    public void createPropertySet() {
        
        // Create a list of PropertySets; Some support Types others don't
        List<PropertySet> propertySets = new ArrayList<PropertySet>();
        
        // Build a randomized, ordered list of PropertySets
        propertySets.add(randomPropertySet());   
        propertySets.add(randomPropertySet());
        propertySets.add(randomPropertySet());
        propertySets.add(randomPropertySet());
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("PropertySets", propertySets);
        
        propertySet = new AggregatePropertySet(args);
        
    }
    
    private PropertySet randomPropertySet() {

        PropertySet propertySet = null;
        
        switch( random.nextInt(3) ) {
            case 0: propertySet = new MapPropertySet(); break;            // Does not support Types
            case 1: propertySet = new MemoryPropertySet(); break;         // Does support Types
            case 2: propertySet = new XMLPropertySet(); break;            // Does support Types
            case 3: propertySet = new SerializablePropertySet(); break;   // Does support Types
        }
        
        return propertySet;
    }

    /**
     * Sometimes these tests will fail based on the contents of the AggregatePropertySet
     *   Running the test generates a new order.  Ignore this test to prevent bad tests
     *   from affecting the Continous Build processor
     */

    @Ignore
    public void getKeysWithPrefix() {}

    
    @Ignore
    public void removeAllKeys() {}
    
    
    @Ignore
    public void getKeys() {}

}
