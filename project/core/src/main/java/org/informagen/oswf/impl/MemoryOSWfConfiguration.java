package org.informagen.oswf.impl;


// OSWf Interfaces 
import org.informagen.oswf.OSWfConfiguration; 
import org.informagen.oswf.WorkflowLoader; 

// Persistence
import org.informagen.oswf.WorkflowStore;

import java.io.Serializable;

/**
 * Default implementation for a configuration object.
 * This configuration object is passed to the {@link org.informagen.oswf.Workflow#setConfiguration(Configuration)}
 * method. If the configuration is not initialized, the {@link #load(java.net.URL)} method will be called by
 * the workflow. Alternatively, the actor can explicitly load the configuration by calling that method before
 * calling {@link org.informagen.oswf.Workflow#setConfiguration(Configuration)}.
 * <p>
 */


public class MemoryOSWfConfiguration extends DefaultOSWfConfiguration implements Serializable {

    final static String STORE_CLASSNAME = "org.informagen.oswf.impl.stores.MemoryStore";
    final static String PROPERTYSETSTORE_CLASSNAME = "org.informagen.oswf.impl.MemoryPropertySetStore";

    public MemoryOSWfConfiguration() {
        workflowStoreClassname = STORE_CLASSNAME;      
        propertySetStoreClassname = PROPERTYSETSTORE_CLASSNAME;      
    }



}
