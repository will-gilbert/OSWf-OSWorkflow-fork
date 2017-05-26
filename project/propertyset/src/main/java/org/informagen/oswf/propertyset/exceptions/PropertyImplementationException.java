package org.informagen.oswf.propertyset.exceptions;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;


/**
 * Thrown if a specific implementation exception is thrown
 * (such as EJBException, RemoteException, NamingException, IOException, etc).
 *
 * <p>A specific Exception can be wrapped in this Exception, by being
 * passed to the constructor. It can be retrieved via
 * {@link #getRootCause()} .</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 146 $
 */
public class PropertyImplementationException extends PropertySetException {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected Throwable original;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public PropertyImplementationException() {
        super();
    }

    public PropertyImplementationException(String msg) {
        super(msg);
    }

    public PropertyImplementationException(String msg, Throwable original) {
        super(msg);
        this.original = original;
    }

    public PropertyImplementationException(Throwable original) {
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
