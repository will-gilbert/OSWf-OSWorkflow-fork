package org.informagen.oswf;

import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.ProcessInstance;


/**
 * Workflow listener interface to be implemented by local session
 * beans that wish to be notified of changes in the workflow state.
 * @author Hani Suleiman
 * Date: Apr 6, 2002
 * Time: 11:48:14 PM
 */
public interface WorkflowListener {
    // M E T H O D S  -------------------------------------------------------------------------

    public void stateChanged(ProcessInstance entry) throws WorkflowException;
}
