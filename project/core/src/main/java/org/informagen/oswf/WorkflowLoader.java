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

    WorkflowDescriptor getWorkflow(String workflowName) throws WorkflowLoaderException;

    /**
     * Get a workflow descriptor given a workflow name
     * @param workflowName The name of the workflow to get
     * @param validate should this workflow be validated against the DTD
     * @return The descriptor for the specified workflow
     * @throws WorkflowLoaderException if the specified workflow name does not exist or cannot be located
     */

    WorkflowDescriptor getWorkflow(String workflowName, boolean validate) throws WorkflowLoaderException;
    
    /**
     * Get all workflow names in the this loader
     * @return An array of all workflow names
     * @throws WorkflowLoaderException if the workflows do not exist or cannot be located
     */

    Set<String> getWorkflowNames() throws WorkflowLoaderException;


    /**
     * Get a workflow and an XML
     * @param workflowName The name of the workflow to get
     * @throws WorkflowLoaderException if the specified workflow name does not exist or cannot be located
     */
    String getWorkflowAsXML(String workflowName) throws WorkflowLoaderException;

}
