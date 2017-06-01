package org.informagen.oswf.typedmap.exceptions;


/**
 * Parent class of all exceptions thrown by PropertySet.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class TypedMapException extends RuntimeException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public TypedMapException() {
        super();
    }

    public TypedMapException(String msg) {
        super(msg);
    }


}
