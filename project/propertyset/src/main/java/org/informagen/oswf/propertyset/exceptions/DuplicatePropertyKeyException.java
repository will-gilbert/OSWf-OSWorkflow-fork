package org.informagen.oswf.propertyset.exceptions;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;


/**
 * Thrown if a property is set who's key matches a key of an
 * existing property with different type.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class DuplicatePropertyKeyException extends PropertySetException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DuplicatePropertyKeyException() {
        super();
    }

    public DuplicatePropertyKeyException(String msg) {
        super(msg);
    }
}
