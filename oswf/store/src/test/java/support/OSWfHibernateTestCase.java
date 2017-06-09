package support;

import org.informagen.oswf.testing.OSWfTestCase;
import support.HibernateTestSupport;

// Hibernate
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class OSWfHibernateTestCase extends OSWfTestCase {

    private HibernateTestSupport hibernateTestSupport = null;


    public OSWfHibernateTestCase(String... resources) {

        hibernateTestSupport = new HibernateTestSupport();
    
        for (String resource : resources) 
            hibernateTestSupport.addResource(resource);
    }

    // Hibernate Support Methods ==============================================================

    protected SessionFactory getSessionFactory() throws Exception {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        return hibernateTestSupport.getSessionFactory();
    }

    protected Session getSession() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        return hibernateTestSupport.getSession();
    }

    protected void closeSession() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        hibernateTestSupport.closeSession();
    }

    protected void closeSessionFactory() {
        if(hibernateTestSupport == null)
            throw new RuntimeException("HibernateSupport has not been initialized.");
    
        hibernateTestSupport.closeSessionFactory();
    }

    protected HibernateTestSupport getHibernateTestSupport() {
        return hibernateTestSupport;
    }

}
