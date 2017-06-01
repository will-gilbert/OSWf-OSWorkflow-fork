package org.informagen.typedmap.exceptions;

import org.informagen.typedmap.exceptions.TypedMapException;


/**
 * Thrown if a value is attempted to be retrieved that does exist but is of different type.
 *
 */
public class InvalidValueTypeException extends TypedMapException {
	
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public InvalidValueTypeException() {
        super();
    }

    public InvalidValueTypeException(String msg) {
        super(msg);
    }
}
