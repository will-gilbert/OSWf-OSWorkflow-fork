package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


/**
 * Exception thrown to indicate that the action just attempt is invalid
 * for the specified workflow.
 *
 * @author Hani Suleiman (hani@formicary.net)
 */
public class InvalidActionException extends WorkflowException {

    public InvalidActionException() {
    }

    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(Exception cause) {
        super(cause);
    }

    public InvalidActionException(String message, Exception cause) {
        super(message, cause);
    }
}
