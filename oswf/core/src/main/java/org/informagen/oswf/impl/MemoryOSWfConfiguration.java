package org.informagen.oswf.impl;


// OSWf Interfaces 
import org.informagen.oswf.OSWfConfiguration; 
import org.informagen.oswf.WorkflowLoader; 

// Persistence
import org.informagen.oswf.WorkflowStore;

import java.io.Serializable;

/**
 * Default implementation for a configuration object.
 * This configuration object is passed to the Workflow#setConfiguration(Configuration)
 * method. If the configuration is not initialized, the {@link #load(java.net.URL)} method will be called by
 * the workflow. Alternatively, the actor can explicitly load the configuration by calling that method before
 * calling Workflow.setConfiguration(Configuration).
 * <p>
 */


public class MemoryOSWfConfiguration extends DefaultOSWfConfiguration implements Serializable {

    final static String WORKFLOW_STORE_CLASSNAME = "org.informagen.oswf.impl.stores.MemoryWorkflowStore";
    final static String PERSISTENTVARS_STORE_CLASSNAME = "org.informagen.oswf.impl.MemoryPeristentVarsStore";

    public MemoryOSWfConfiguration() {
        workflowStoreClassname = WORKFLOW_STORE_CLASSNAME;      
        persistentVarsStoreClassname = PERSISTENTVARS_STORE_CLASSNAME;      
    }



}
