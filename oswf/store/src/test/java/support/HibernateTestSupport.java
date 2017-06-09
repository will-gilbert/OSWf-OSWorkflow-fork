package support;

// Hibernate
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;

import org.hibernate.cfg.Configuration;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Junit
import static org.junit.Assert.fail;


public class HibernateTestSupport {

    private static final Marker fatal = MarkerFactory.getMarker("FATAL");
	private static final Logger logger = LoggerFactory.getLogger(HibernateTestSupport.class.getName());

    private Configuration configuration = new Configuration();
    private SessionFactory sessionFactory = null;
    private Session session = null;
    
    public HibernateTestSupport() {}

    public HibernateTestSupport(String... resources) {
        for (String resource : resources) 
            addResource(resource);
    }

    public void addResource(String resource) {

        try {
            if(configuration != null)
                configuration.configure(resource);
        } catch (HibernateException hibernateException) {
            logger.error(fatal, hibernateException.getMessage());
            fail("Failed to load: " + resource + " " + hibernateException.getMessage());
        }
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        if(sessionFactory == null)
            if(configuration == null) {
                logger.error(fatal, "Hibernate not configured");
                fail();
            } else 
                sessionFactory = configuration.buildSessionFactory();

        return sessionFactory;
    }

    public Session getSession() {
    
        // If we already have a session, return quickly, else create one
        if(session != null && session.isOpen())
            return session;

        try {
            session = getSessionFactory().openSession();
        } catch (HibernateException hibernateException) {
            logger.error(fatal, hibernateException.getMessage());
            fail();
        }

            
        return session;
    }


    public void closeSession() {
        if((session != null) && session.isOpen()) {
            session.flush();
            session.close();
            session = null;
        }
    }


    public void closeSessionFactory() {
    
        closeSession();
        
        if((sessionFactory != null) && (sessionFactory.isClosed() == false)) 
            sessionFactory.close();

        sessionFactory = null;
    }

}
