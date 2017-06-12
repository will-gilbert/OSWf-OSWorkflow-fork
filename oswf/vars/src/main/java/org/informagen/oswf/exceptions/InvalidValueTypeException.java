package org.informagen.oswf.exceptions;

import org.informagen.oswf.exceptions.PersistentVarsException;


/**
 * Thrown if a value is attempted to be retrieved that does exist but is of different type.
 *
 */
public class InvalidValueTypeException extends PersistentVarsException {
	
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public InvalidValueTypeException() {
        super();
    }

    public InvalidValueTypeException(String msg) {
        super(msg);
    }
}
