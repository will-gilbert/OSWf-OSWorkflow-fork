package org.informagen.oswf.impl;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.TypedMapStore;

import org.informagen.oswf.MemoryPersistentVars;

// Java - Collections
import java.util.Map;
import java.util.HashMap;

/**
 * A map which behaves like a persistence store i.e. a database, to store
 *  the property set for each processes instance. 
 */
 
public class MemoryTypedMapStore implements TypedMapStore {

    private static Map<Long,PersistentVars> persistentVarsMap = new HashMap();
    
    public MemoryTypedMapStore() {
        // no-op
    }

    public MemoryTypedMapStore(Map<String,String> config, Map<String,Object> args) {
        // no-op
    }

    // Find the persistent variables for this process instance
    public PersistentVars getPersistentVars(long piid) {
        
        if(persistentVarsMap.containsKey(piid) == false)
            persistentVarsMap.put(piid, new MemoryPersistentVars());
        
        return persistentVarsMap.get(piid);
    }
    
    // Useful for Unit Testing
    public static void clear() {
        persistentVarsMap.clear();
    }
    
}
