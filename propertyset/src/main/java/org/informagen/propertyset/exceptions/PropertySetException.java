package org.informagen.propertyset.exceptions;


/**
 * Parent class of all exceptions thrown by PropertySet.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class PropertySetException extends RuntimeException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public PropertySetException() {
        super();
    }

    public PropertySetException(String msg) {
        super(msg);
    }


}
