package org.informagen.oswf.impl;

import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;

import java.io.Serializable;

/**
 * Default implementation of a Process Instance. 
 *
 */

public class DefaultProcessInstance implements ProcessInstance, Serializable {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected String workflowName;
    protected boolean initialized;
    protected ProcessInstanceState state;
    protected Long piid = null;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DefaultProcessInstance() {}

    public DefaultProcessInstance(Long piid, String workflowName) {
        this.piid = piid;
        this.workflowName = workflowName;
        this.state = ProcessInstanceState.INITIATED;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public void setProcessInstanceId(Long piid) {
        this.piid = piid;
    }

    public Long getProcessInstanceId() {
        return piid;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setState(ProcessInstanceState state) {
        this.state = state;
    }

    public ProcessInstanceState getState() {
        return state;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }
}
