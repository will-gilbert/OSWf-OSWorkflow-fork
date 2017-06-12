package org.informagen.oswf.exceptions;

import org.informagen.oswf.exceptions.PersistentVarsException;


/**
 * Thrown if a specific implementation exception is thrown
 * (such as EJBException, RemoteException, NamingException, IOException, etc).
 *
 * <p>A specific Exception can be wrapped in this Exception, by being
 * passed to the constructor. It can be retrieved via
 * {@link #getRootCause()} .</p>
 *
 */

public class ValueImplementationException extends PersistentVarsException {
    
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected Throwable original;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public ValueImplementationException() {
        super();
    }

    public ValueImplementationException(String msg) {
        super(msg);
    }

    public ValueImplementationException(String msg, Throwable original) {
        super(msg);
        this.original = original;
    }

    public ValueImplementationException(Throwable original) {
        this(original.getLocalizedMessage(), original);
    }

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Retrieve original Exception.
     */
    public Throwable getRootCause() {
        return original;
    }
}
