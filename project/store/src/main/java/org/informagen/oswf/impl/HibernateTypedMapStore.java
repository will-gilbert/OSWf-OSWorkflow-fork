package org.informagen.oswf.impl;


// OSWf -  TypedMap Hibernate 3.x
import org.informagen.oswf.typedmap.HibernateTypedMap;
import org.informagen.oswf.typedmap.hibernate.HibernateConfigurationProvider;


// OSWf - Core
import org.informagen.oswf.TypedMapStore;

// OpenSymphony - TypedMap 
import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.typedmap.TypedMapFactory;
import org.informagen.oswf.exceptions.WorkflowStoreException;

// Hibernate 3.2.x
import org.hibernate.SessionFactory;

// Java - Collections
import java.util.Map;
import java.util.HashMap;


/**
 */

public class HibernateTypedMapStore implements TypedMapStore  {

    private HibernateConfigurationProvider configurationProvider;
    private SessionFactory sessionFactory = null;
    private String propertySetName = null;

    public HibernateTypedMapStore() {}

    public HibernateTypedMapStore(Map<String,String> config, Map<String,Object> args) {
        this.sessionFactory = (SessionFactory)args.get("sessionFactory");
        this.propertySetName = config.get("propertySet");
    }


    public HibernateTypedMapStore(String propertySetName) {
        this(propertySetName, null);
    }

    public HibernateTypedMapStore(SessionFactory sessionFactory) {
        this(null, sessionFactory);
    }

    public HibernateTypedMapStore(String propertySetName, SessionFactory sessionFactory) {
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

    public TypedMap getTypedMap(long piid)  {
        
        HashMap<String,Object>args = new HashMap<String,Object>();
        
        // 'entityName' not used; provided as placeholder
        args.put("processInstanceId", piid); 
        args.put("piid", piid); 

        // SessionFactory was created elsewhere and passed in via the constructor, 
        //   otherwise let the DefaultHibernateConfigurationProvider read the  
        //   Hibernate configuration from the 'propertyset.xml' file

        configurationProvider = new HibernateConfigurationProvider(sessionFactory);
        args.put("configurationProvider", configurationProvider);

        TypedMap propertySet = null;
        if (propertySetName != null)
            propertySet = TypedMapFactory.getInstance().createTypedMap(propertySetName, args);
        else {
            propertySet = new HibernateTypedMap(null, args);
        }

        return propertySet;

    }

    public SessionFactory getSessionFactory() {
        return configurationProvider.getSessionFactory();
    }

}