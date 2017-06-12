package org.informagen.oswf.impl;

import org.informagen.oswf.PersistentVars;

// OSWf -  PersistentVars Hibernate 3.x
import org.informagen.oswf.HibernateTypedMap;
import org.informagen.oswf.hibernate.HibernateConfigurationProvider;


// OSWf - Core
import org.informagen.oswf.PeristentVarsStore;

// OSWf - PersistentVars 
import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.PersistentVarsFactory;
import org.informagen.oswf.exceptions.WorkflowStoreException;

// Hibernate 3.2.x
import org.hibernate.SessionFactory;

// Java - Collections
import java.util.Map;
import java.util.HashMap;


/**
 */

public class HibernatePersistentVarsStore implements PeristentVarsStore  {

    private HibernateConfigurationProvider configurationProvider;
    private SessionFactory sessionFactory = null;
    private String propertySetName = null;

    public HibernatePersistentVarsStore() {}

    public HibernatePersistentVarsStore(Map<String,String> config, Map<String,Object> args) {
        this.sessionFactory = (SessionFactory)args.get("sessionFactory");
        this.propertySetName = config.get("propertySet");
    }


    public HibernatePersistentVarsStore(String propertySetName) {
        this(propertySetName, null);
    }

    public HibernatePersistentVarsStore(SessionFactory sessionFactory) {
        this(null, sessionFactory);
    }

    public HibernatePersistentVarsStore(String propertySetName, SessionFactory sessionFactory) {
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
     * @return PersistentVars - A map with typed objects
     */

    public PersistentVars getPersistentVars(long piid)  {
        
        HashMap<String,Object>args = new HashMap<String,Object>();
        
        // 'entityName' not used; provided as placeholder
        args.put("processInstanceId", piid); 
        args.put("piid", piid); 

        // SessionFactory was created elsewhere and passed in via the constructor, 
        //   otherwise let the DefaultHibernateConfigurationProvider read the  
        //   Hibernate configuration from the 'propertyset.xml' file

        configurationProvider = new HibernateConfigurationProvider(sessionFactory);
        args.put("configurationProvider", configurationProvider);

        PersistentVars propertySet = null;
        if (propertySetName != null)
            propertySet = PersistentVarsFactory.getInstance().createTypedMap(propertySetName, args);
        else {
            propertySet = new HibernateTypedMap(null, args);
        }

        return propertySet;

    }

    public SessionFactory getSessionFactory() {
        return configurationProvider.getSessionFactory();
    }

}
