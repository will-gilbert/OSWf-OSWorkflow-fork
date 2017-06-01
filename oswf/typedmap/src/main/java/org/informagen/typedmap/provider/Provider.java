package org.informagen.typedmap.provider;


/**
 * Provider interface. A provider is a pluggable runtime resource and is used
 * when different behaviours are required in different situations. 
 *
 *
 * @see org.informagen.typedmap.provider.ProviderFactory
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
     * @exception org.informagen.typedmap.provider.ProviderConfigurationException thrown if error in startup
     *            or configuration.
     */
     
    void init() throws ProviderConfigurationException;
}
