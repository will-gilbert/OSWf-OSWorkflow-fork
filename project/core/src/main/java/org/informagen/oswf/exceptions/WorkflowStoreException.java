package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: May 10, 2003
 * Time: 11:29:45 AM                                              
 * 
 */
public class WorkflowStoreException extends WorkflowException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public WorkflowStoreException(String s) {
        super(s);
    }

    public WorkflowStoreException(String s, Throwable ex) {
        super(s, ex);
    }

    public WorkflowStoreException(Throwable ex) {
        super(ex);
    }
}
