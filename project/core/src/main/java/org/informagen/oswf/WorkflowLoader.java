package org.informagen.oswf;

// OSWfConfiguration Loader
import org.informagen.oswf.util.WorkflowLocation;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

import org.informagen.oswf.descriptors.*;

// Java - Collections
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Properties;


public interface WorkflowLoader {


    void init(Map<String,WorkflowLocation> workflows);

    void initDone() throws WorkflowLoaderException;

    WorkflowDescriptor getWorkflow(String name) throws WorkflowLoaderException;

    /**
     * Get a workflow descriptor given a workflow name.
     * @param name The name of the workflow to get.
     * @return The descriptor for the specified workflow.
     * @throws org.informagen.oswf.exceptions.WorkflowLoaderException if the specified workflow name does not exist or cannot be located.
     */

    WorkflowDescriptor getWorkflow(String name, boolean validate) throws WorkflowLoaderException;
    
    /**
       * Get all workflow names in the current factory
       * @return An array of all workflow names
       * @throws org.informagen.oswf.exceptions.WorkflowLoaderException if the specified workflow name does not exist or cannot be located.
       */

    Set<String> getWorkflowNames() throws WorkflowLoaderException;


    String getWorkflowAsXML(String workflowName) throws WorkflowLoaderException;

}
