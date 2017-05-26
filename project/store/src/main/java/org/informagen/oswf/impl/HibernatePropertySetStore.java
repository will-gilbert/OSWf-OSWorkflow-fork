package org.informagen.oswf.impl;


// OSWf -  PropertySet Hibernate 3.x
import org.informagen.oswf.propertyset.HibernatePropertySet;
import org.informagen.oswf.propertyset.hibernate.HibernateConfigurationProvider;


// OSWf - Core
import org.informagen.oswf.PropertySetStore;

// OpenSymphony - PropertySet 
import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.PropertySetFactory;
import org.informagen.oswf.exceptions.WorkflowStoreException;

// Hibernate 3.2.x
import org.hibernate.SessionFactory;

// Java - Collections
import java.util.Map;
import java.util.HashMap;


/**
 */

public class HibernatePropertySetStore implements PropertySetStore  {

    private HibernateConfigurationProvider configurationProvider;
    private SessionFactory sessionFactory = null;
    private String propertySetName = null;

    public HibernatePropertySetStore() {}

    public HibernatePropertySetStore(Map<String,String> config, Map<String,Object> args) {
        this.sessionFactory = (SessionFactory)args.get("sessionFactory");
        this.propertySetName = config.get("propertySet");
    }


    public HibernatePropertySetStore(String propertySetName) {
        this(propertySetName, null);
    }

    public HibernatePropertySetStore(SessionFactory sessionFactory) {
        this(null, sessionFactory);
    }

    public HibernatePropertySetStore(String propertySetName, SessionFactory sessionFactory) {
        this.propertySetName = propertySetName;
        this.sessionFactory = sessionFactory;
    }

    public void init(Map<String,Object> args) {
        sessionFactory = (SessionFactory)args.get("sessionFactory");
    }

    /**
     * Get the propery set for a process instance
     *
     * @param piid - long, the process instance ID
     * @return ProperySet - the propertySet
     */

    public PropertySet getPropertySet(long piid)  {
        
        HashMap<String,Object>args = new HashMap<String,Object>();
        
        // 'entityName' not used; provided as placeholder
        args.put("processInstanceId", piid); 
        args.put("piid", piid); 

        // SessionFactory was created elsewhere and passed in via the constructor, 
        //   otherwise let the DefaultHibernateConfigurationProvider read the  
        //   Hibernate configuration from the 'propertyset.xml' file

        configurationProvider = new HibernateConfigurationProvider(sessionFactory);
        args.put("configurationProvider", configurationProvider);

        PropertySet propertySet = null;
        if (propertySetName != null)
            propertySet = PropertySetFactory.getInstance().createPropertySet(propertySetName, args);
        else {
            propertySet = new HibernatePropertySet(null, args);
        }

        return propertySet;

    }

    public SessionFactory getSessionFactory() {
        return configurationProvider.getSessionFactory();
    }

}
