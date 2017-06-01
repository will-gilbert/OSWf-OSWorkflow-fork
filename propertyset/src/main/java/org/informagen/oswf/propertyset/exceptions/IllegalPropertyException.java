package org.informagen.oswf.propertyset.exceptions;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;


/**
 * Thrown if a property is set which is not allowed.
 *
 * <p><i>e.g.</i> non-serializable Object is passed to SerializablePropertySet,
 * or field is persisted that cannot be stored in database.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class IllegalPropertyException extends PropertySetException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public IllegalPropertyException() {
        super();
    }

    public IllegalPropertyException(String msg) {
        super(msg);
    }
}
