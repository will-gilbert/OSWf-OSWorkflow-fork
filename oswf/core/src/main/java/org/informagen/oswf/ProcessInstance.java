package org.informagen.oswf;

import org.informagen.oswf.ProcessInstanceState;

/**
 * Interface for a individual Process Instance
 *
 */
  
public interface ProcessInstance {
    
    /**
     * Returns the unique ID of this process instance, piid.
     */
    public Long getProcessInstanceId();

    /**
     * Returns true if the process instance has been initialized
     */
    public boolean isInitialized();

    /**
     * Current state of this process instance
     */

    public ProcessInstanceState getState();
    public void setState(ProcessInstanceState state);

    /**
     * Returns the name of the workflow name used to create this process instance
     */
     
    public String getWorkflowName();
}
