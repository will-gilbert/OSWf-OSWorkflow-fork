package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


/**
 * @author Hani Suleiman
 * Date: Aug 28, 2003
 * Time: 8:19:30 PM
 */
public class InvalidEntryStateException extends WorkflowException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public InvalidEntryStateException(String message) {
        super(message);
    }
}
