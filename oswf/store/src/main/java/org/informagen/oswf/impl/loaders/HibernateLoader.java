package org.informagen.oswf.impl.loaders;

// OSWf - Loaders
import org.informagen.oswf.WorkflowLoader;
import org.informagen.oswf.impl.loaders.JDBCLoader;
import org.informagen.oswf.util.WorkflowLocation;
import org.informagen.oswf.util.WorkflowXMLParser;

// OSWF - Descriptors
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Hibernate 3.2.x
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;

// Hibernate - Criteria
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

// Java - I/O
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

// Java - Collections
import java.util.Map;
import java.util.Properties;

// Java - JNDI
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class HibernateLoader extends JDBCLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowLoader.class);

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------
    
    protected SessionFactory sessionFactory;
    
    // C O N S T U C T O R S  ----------------------------------------------------------------- 

    public HibernateLoader(Properties parameters, Map<String,Object> persistentArgs) throws WorkflowLoaderException {
       super(parameters, persistentArgs);
                             
       this.sessionFactory = (SessionFactory)persistentArgs.get("sessionFactory");
    }

    // P R O T E C T E D   M E T H O D S  -----------------------------------------------------

    @Override
    protected InputStream fetchProcessDefinition(WorkflowLocation workflowLocation) throws WorkflowLoaderException {
 
        String processDefinition = null;
        Session session = null;
        Transaction transaction = null;

        if(sessionFactory == null)
            throw new WorkflowLoaderException("'sessionFactory' has not been defined for HibernateLoader");

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            Criteria criteria = session.createCriteria(HibernateProcessDescription.class);
            criteria.add(Restrictions.eq("workflowName", workflowLocation.location));
            
            HibernateProcessDescription hpd = (HibernateProcessDescription)criteria.uniqueResult();
            
            if(hpd != null)
                processDefinition = hpd.getContent();

            transaction.commit();

        } catch(HibernateException hibernateException) {
            throw new WorkflowLoaderException(hibernateException);
        } finally {

            if (transaction != null && transaction.isActive())
                 transaction.rollback();

            if (session != null) 
                session.close();
        }
        
        if(processDefinition == null)
            throw new WorkflowLoaderException("Process definition '" + workflowLocation.location + "' not found");
        
        return new ByteArrayInputStream(processDefinition.getBytes());
    }

}
