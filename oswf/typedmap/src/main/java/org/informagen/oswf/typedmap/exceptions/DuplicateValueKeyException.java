package org.informagen.oswf.typedmap.exceptions;

import org.informagen.oswf.typedmap.exceptions.TypedMapException;


/**
 * Thrown if a value is set who's key matches a key of an
 * existing value with a different type.
 *
 */
public class DuplicateValueKeyException extends TypedMapException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DuplicateValueKeyException() {
        super();
    }

    public DuplicateValueKeyException(String msg) {
        super(msg);
    }
}
