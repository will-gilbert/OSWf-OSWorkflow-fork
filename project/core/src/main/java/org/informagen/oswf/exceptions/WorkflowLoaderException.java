package org.informagen.oswf.exceptions;

import org.informagen.oswf.exceptions.WorkflowException;

/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Apr 8, 2003
 * Time: 9:42:15 AM
 */

public class WorkflowLoaderException extends WorkflowException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public WorkflowLoaderException() {
    }

    public WorkflowLoaderException(String message) {
        super(message);
    }

    public WorkflowLoaderException(Exception cause) {
        super(cause);
    }

    public WorkflowLoaderException(String message, Exception cause) {
        super(message, cause);
    }
}
