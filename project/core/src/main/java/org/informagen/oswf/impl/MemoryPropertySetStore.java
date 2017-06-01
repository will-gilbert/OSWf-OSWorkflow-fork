package org.informagen.oswf.impl;

import org.informagen.oswf.PropertySetStore;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.MemoryPropertySet;

// Java - Collections
import java.util.Map;
import java.util.HashMap;

/**
 * A map which behaves like a persistence store i.e. a database, to store
 *  the property set for each processes instance. 
 */
 
public class MemoryPropertySetStore implements PropertySetStore {

    private static Map<Long,PropertySet> propertySets = new HashMap<Long,PropertySet>();
    
    public MemoryPropertySetStore() {
        // no-op
    }

    public MemoryPropertySetStore(Map<String,String> config, Map<String,Object> args) {
        // no-op
    }

    public PropertySet getPropertySet(long piid) {
        
        if(propertySets.containsKey(piid) == false)
            propertySets.put(piid, new MemoryPropertySet());
        
        return propertySets.get(piid);
    }
    
    // Useful for Unit Testing
    public static void clear() {
        propertySets.clear();
    }
    
}
