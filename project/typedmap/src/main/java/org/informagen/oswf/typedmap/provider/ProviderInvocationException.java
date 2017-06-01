package org.informagen.oswf.typedmap.provider;


/**
 * Exception thrown by Provider to encapsulate propriertry exception thrown by
 * underlying implementation provider.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 */
public class ProviderInvocationException extends Exception {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Throwable cause;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public ProviderInvocationException(Throwable cause) {
        super();
        this.cause = cause;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Throwable getCause() {
        return cause;
    }
}
