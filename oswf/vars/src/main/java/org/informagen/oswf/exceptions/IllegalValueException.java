package org.informagen.oswf.exceptions;

import org.informagen.oswf.exceptions.PersistentVarsException;

/**
 * Thrown if a value is set which is not allowed.
 *
 * <p><i>That is,</i> non-serializable Object is passed to SerializablePersistentVars,
 *  or field is persisted that cannot be stored in database.</p>
 *
 */

public class IllegalValueException extends PersistentVarsException {

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public IllegalValueException() {
        super();
    }

    public IllegalValueException(String msg) {
        super(msg);
    }
}
