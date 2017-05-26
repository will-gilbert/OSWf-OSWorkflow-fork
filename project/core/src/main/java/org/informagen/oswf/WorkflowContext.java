package org.informagen.oswf;


/**
** Used to communicate the contents of the OWSf engine with rest of the system
*/

public interface WorkflowContext extends java.io.Serializable {

    /**
     * @return the workflow actor
     */
    public String getActor();

    /**
     * Sets the current transaction to be rolled back
     */
    public void setRollbackOnly();
}
