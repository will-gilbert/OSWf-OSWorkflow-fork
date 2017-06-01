package org.informagen.propertyset.provider;


/**
 * Provider interface. A provider is a pluggable runtime resource and is used
 * when different behaviours are required in different situations. 
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 *
 * @see org.informagen.propertyset.provider.ProviderFactory
 */
public interface Provider {
    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Shutdown Provider.
     */
    void destroy();

    /**
     * Startup Provider.
     *
     * @exception org.informagen.propertyset.provider.ProviderConfigurationException thrown if error in startup
     *            or configuration.
     */
     
    void init() throws ProviderConfigurationException;
}
