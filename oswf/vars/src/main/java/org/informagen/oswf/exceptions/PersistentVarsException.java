package org.informagen.oswf.exceptions;


/**
 * Parent class of all exceptions thrown by PropertySet.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class PersistentVarsException extends RuntimeException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public PersistentVarsException() {
        super();
    }

    public PersistentVarsException(String msg) {
        super(msg);
    }


}
