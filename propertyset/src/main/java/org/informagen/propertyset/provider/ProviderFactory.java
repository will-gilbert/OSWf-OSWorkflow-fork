package org.informagen.propertyset.provider;


/**
 * Get specific provider implementation. This is a singleton.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 *
 * @see org.informagen.propertyset.provider.Provider
 */
public class ProviderFactory {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static ProviderFactory instance;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    /**
     * Private constructor. To obtain instance, use {@see #getInstance()}.
     */
    private ProviderFactory() {
    }

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Obtain singleton instance of factory.
     */
    public static ProviderFactory getInstance() {
        if (instance == null) 
            instance = new ProviderFactory();

        return instance;
    }

    /**
     * Find appropriate provider.
     *
     * @param property System property under which to find class name of provider implementation.
     * @param defaultClass Name of provider to use if all else fails (this should always be able to instantiate).
     */
    public Provider getProvider(String property, String defaultClass) {
        String providerClassName = System.getProperty(property);
        Provider result = null;

        if ((providerClassName != null) && (providerClassName.trim().length() > 0)) {
            result = load(providerClassName);

            if (result == null) {
                System.err.println("Provider " + providerClassName + " cannot be loaded. \nUsing " + defaultClass + " instead.");
                result = load(defaultClass);
            }
        } else {
            result = load(defaultClass);
        }

        if (result == null) {
            // if defaultProvider cannot be started, this is bad (and should never happen).
            System.err.println("!!! CANNOT LOAD DEFAULT PROVIDER : " + defaultClass + "!!!");
        }

        return result;
    }

    private Provider load(String className) {
        try {
            Class providerClass = null;

            try {
                providerClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                providerClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            }

            Provider result = (Provider) providerClass.newInstance();
            result.init();

            return result;
        } catch (ProviderConfigurationException e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace(System.err);
            } else {
                e.printStackTrace(System.err);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
