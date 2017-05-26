package org.informagen.oswf.propertyset.exceptions;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;


/**
 * Thrown if a property is attempted to be retrieved that
 * does exist but is of different type.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class InvalidPropertyTypeException extends PropertySetException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public InvalidPropertyTypeException() {
        super();
    }

    public InvalidPropertyTypeException(String msg) {
        super(msg);
    }
}
