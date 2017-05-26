package org.informagen.oswf.propertyset.provider;


/**
 * Thrown by Provider if it cannot initialize properly. If this is thrown,
 * then it is the job of the ProviderFactory to log the reason, and try
 * another Provider.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 *
 * @see org.informagen.oswf.propertyset.provider.Provider
 * @see org.informagen.oswf.propertyset.provider.ProviderFactory
 */
public class ProviderConfigurationException extends Exception {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Throwable cause;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public ProviderConfigurationException() {
        super();
    }

    public ProviderConfigurationException(String msg) {
        super(msg);
    }

    public ProviderConfigurationException(Throwable cause) {
        super();
        this.cause = cause;
    }

    public ProviderConfigurationException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Throwable getCause() {
        return cause;
    }
}
