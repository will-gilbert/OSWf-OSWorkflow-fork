package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


/**
 * Exception thrown to indicate that the query requested is not supported by the
 * current store
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Oct 4, 2003
 * Time: 5:26:31 PM
 */
public class QueryNotSupportedException extends WorkflowException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public QueryNotSupportedException() {
    }

    public QueryNotSupportedException(String message) {
        super(message);
    }

    public QueryNotSupportedException(Exception cause) {
        super(cause);
    }

    public QueryNotSupportedException(String message, Exception cause) {
        super(message, cause);
    }
}
