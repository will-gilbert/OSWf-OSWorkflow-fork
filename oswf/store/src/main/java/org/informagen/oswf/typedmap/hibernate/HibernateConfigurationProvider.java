package org.informagen.oswf.hibernate;

// This package
import org.informagen.oswf.hibernate.HibernatePersistentVarsDAO;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Hibernate 3.x
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// Java Util
import java.util.Iterator;
import java.util.Map;


public class HibernateConfigurationProvider {

    protected static Logger logger = LoggerFactory.getLogger(HibernateConfigurationProvider.class);

    protected Configuration configuration;
    protected HibernatePersistentVarsDAO persistentVarsDAO = null;
    protected SessionFactory sessionFactory = null;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public HibernateConfigurationProvider() {}

    public HibernateConfigurationProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    // HibernateConfigurationProvider Interface ///////////////////////////////////////////////

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public HibernatePersistentVarsDAO getPersistentVarsDAO() {
    
        if (persistentVarsDAO == null) 
            persistentVarsDAO = new HibernatePersistentVarsDAO(sessionFactory);

        return persistentVarsDAO;
    }

    public void setupConfiguration(Map<String,String> configurationProperties) {

        // Test here to prevent overriding explictly defined SessionFactory
        
        if (sessionFactory == null) {
        
            try {
            
                configuration = new Configuration();

                if (configurationProperties != null) {
                
                    if (logger.isDebugEnabled()) 
                        for(String key : configurationProperties.keySet()) 
                            logger.debug((String)key + " : " +  configurationProperties.get(key));
                    
                    
                    // First, load the resources first
                    if(configurationProperties.containsKey("resources")) {
                        String resources = configurationProperties.get("resources");
                        for(String resource : resources.split(","))
                            configuration.configure(resource);
                    }

                    // Then, load hibernate properties and mapping files
                    for (String key : configurationProperties.keySet()) {
                    
                        if (key.startsWith("hibernate")) 
                            configuration.setProperty(key, configurationProperties.get(key));
                            
                        if (key.startsWith("mapping-")) 
                            configuration.addResource(configurationProperties.get(key));
                    }
                }

                sessionFactory = configuration.buildSessionFactory();

            } catch (MappingException e) {
                logger.error("Could not create new Hibernate 3.x configuration: " + e, e);
            } catch (HibernateException e) {
                logger.error("Could not create new Hibernate 3.x configuration: " + e, e);
            }
       }
    }
}
