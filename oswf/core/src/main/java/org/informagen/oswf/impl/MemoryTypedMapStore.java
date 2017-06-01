package org.informagen.oswf.impl;

import org.informagen.oswf.TypedMapStore;

import org.informagen.typedmap.TypedMap;
import org.informagen.typedmap.MemoryTypedMap;

// Java - Collections
import java.util.Map;
import java.util.HashMap;

/**
 * A map which behaves like a persistence store i.e. a database, to store
 *  the property set for each processes instance. 
 */
 
public class MemoryTypedMapStore implements TypedMapStore {

    private static Map<Long,MemoryTypedMap> propertySets = new HashMap();
    
    public MemoryTypedMapStore() {
        // no-op
    }

    public MemoryTypedMapStore(Map<String,String> config, Map<String,Object> args) {
        // no-op
    }

    // Find the TypedMap for this process instance
    public TypedMap getTypedMap(long piid) {
        
        if(propertySets.containsKey(piid) == false)
            propertySets.put(piid, new MemoryTypedMap());
        
        return propertySets.get(piid);
    }
    
    // Useful for Unit Testing
    public static void clear() {
        propertySets.clear();
    }
    
}
