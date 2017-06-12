package org.informagen.oswf.exceptions;

import org.informagen.oswf.exceptions.PersistentVarsException;


/**
 * Thrown if a value is set who's key matches a key of an
 * existing value with a different type.
 *
 */
public class DuplicateValueKeyException extends PersistentVarsException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DuplicateValueKeyException() {
        super();
    }

    public DuplicateValueKeyException(String msg) {
        super(msg);
    }
}
