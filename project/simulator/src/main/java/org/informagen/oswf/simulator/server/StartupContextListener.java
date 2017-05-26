package org.informagen.oswf.simulator.server;

import org.informagen.oswf.simulator.server.ServiceModule;
import org.informagen.oswf.simulator.server.Locality;

// OSWf Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

// Guice
import com.google.inject.Guice;
import com.google.inject.Injector;


// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Hibernate
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.lang.InstantiationException; 
 
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
/**
 * Implementation of the ServletContextListener for that bootstraps the GUICE Module.
 */
 
public class StartupContextListener implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger("StartupLogger");
    private static final Marker fatal = MarkerFactory.getMarker("FATAL");

    SessionFactory sessionFactory = null; 

   /**
     * This method is called when the servlet context is initialized(when the Web application is deployed).
     * You can initialize servlet context related data here.
     *
     * @param sce event object provided by container. Provides the servlet context [application scope]
     */

   public void contextInitialized(ServletContextEvent servletContextEvent) {
       
       Locality locality = readLocality();

       sessionFactory = createSessionFactory();
       OSWfConfiguration oswfConfiguration = createOSWfConfiguration(sessionFactory);

       Injector injector = Guice.createInjector(new ServiceModule(locality, oswfConfiguration));

       // Share these objects with the other servlets in this WAR
       servletContextEvent.getServletContext().setAttribute("injector", injector);
       servletContextEvent.getServletContext().setAttribute("oswfConfiguration", oswfConfiguration);
   }
 
   /**
     * This method is invoked when the Servlet Context is undeployed or
     * Application Server shuts down.
     *
     * @param sce event object provided by container. Provides the servlet context [application scope]
     */
     
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
       
       if(sessionFactory != null)
            sessionFactory.close();
       
       servletContextEvent.getServletContext().removeAttribute("injector");
       servletContextEvent.getServletContext().removeAttribute("oswfConfiguration");
   }


    // Need to move this to an injector or external properties file or drive by a filter
    private SessionFactory createSessionFactory() {
        
        SessionFactory sessionFactory = null;
    
        try {
    
            String resource;
            
            Configuration configuration = new Configuration();

            resource = "oswf-store.cfg.xml";
            logger.info("Configuring Hibernate Mapping: " + resource);
            configuration.configure(resource);

            resource = "oswf-propertyset.cfg.xml";
            logger.info("Configuring Hibernate Mapping: " + resource);
            configuration.configure(resource);
            
            // Database configuration; H2 or MySQL
            resource = "hibernate.xml";
            logger.info("Configuring Hibernate Mapping: " + resource);
            configuration.configure(resource);
            
            // Attempt to create the SessionFactory; exceptions may be thrown                
            sessionFactory =  configuration.buildSessionFactory();

        } catch (Throwable e) {
            logger.error(fatal, "Failed to create Hibernate SessionFactory: " + e.toString());
            sessionFactory = null;
        }
        
        return sessionFactory;
    }

    private Locality readLocality() {
        
        Locality locality = new Locality();
                
        logger.info("============================================================================");
        logger.info("Starting " + locality.getValue("application.name") + " " + locality.getValue("application.version", "vX.X"));
         
        return locality;
    }


    private OSWfConfiguration createOSWfConfiguration(SessionFactory sessionFactory) {
        
        OSWfConfiguration configuration = null;
        
        try {
        
            configuration = new DefaultOSWfConfiguration()
                .load(getClass().getResource("/oswf.xml"))
                .addPersistenceArg("sessionFactory", sessionFactory);
    
        } catch (WorkflowLoaderException workflowLoaderException) {
           logger.error(workflowLoaderException.getMessage());
        }

        return configuration;
    }
 
}