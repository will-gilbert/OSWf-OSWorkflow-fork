package org.informagen.oswf.propertyset.hibernate;

// This package
import org.informagen.oswf.propertyset.hibernate.HibernatePropertySetItem;

// OSWf PropertySet
import org.informagen.oswf.propertyset.Type;
import org.informagen.oswf.propertyset.exceptions.PropertySetException;


// Hibernate
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.MatchMode;
import org.hibernate.HibernateException;

// Java - Collections
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;


/**
 * DOCUMENT ME!
 *
 * @author will gilbert
 */

public class HibernatePropertySetDAO  {

    protected SessionFactory sessionFactory;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    public HibernatePropertySetDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    // M E T H O D S  -------------------------------------------------------------------------

    public Class getPersistentClass() {
        return HibernatePropertySetItem.class;
    }


    public void save(HibernatePropertySetItem item) {

        if( item == null)
            throw new PropertySetException("Could not save 'null' PropertyItem");
 
       Session session = null;
       Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            session.saveOrUpdate(item);
            session.flush();
            
            transaction.commit();
            
        } catch (HibernateException hibernateException) {
            throw new PropertySetException("Could not save key '" + item.getKey() + "':" + hibernateException.getMessage());
        } finally {
            
            if (transaction != null && transaction.isActive())
                 transaction.rollback();

            if (session != null)
                session.close();
        }
    }

    public Collection<String> getKeys(Long piid, String prefix, Type type) {

        if( piid == null)
            throw new PropertySetException("Could not find keys for 'null' piid");

        Session session = null;
        Collection<String> list = null;
        
        try {
            session = sessionFactory.openSession();
            list = getKeysImpl(session, piid, prefix, type);
        } catch (HibernateException hibernateException) {
            throw new PropertySetException("HibernatePropertySet.getKeys: " + hibernateException.getMessage());
        } finally {
            if (session != null) 
                session.close();
        } 

        return list;
    }

    public HibernatePropertySetItem findByKey(Long piid, String key) {

        if( piid == null)
            throw new PropertySetException("Could not find property for 'null' piid");

        if( key == null)
            throw new PropertySetException("Could not find property for 'null' key");
        
        Session session = null;
        HibernatePropertySetItem item = null;

        try {
            session = sessionFactory.openSession();
            item = getItem(session, piid, key);
            session.flush();
        } catch (HibernateException hibernateException) {
            throw new PropertySetException("Could not find key '" + key + "': " + hibernateException.getMessage());
        } finally {
            if (session != null) 
                session.close();
        }

        return item;
    }


    public HibernatePropertySetItem create(Long piid, String key) {
 
        if( piid == null)
            throw new PropertySetException("Could not create property with 'null' piid");

        if( key == null)
            throw new PropertySetException("Could not create property with 'null' key");
            
       Session session = null;
       Transaction transaction = null;
    
        HibernatePropertySetItem item = new HibernatePropertySetItem(piid, key);

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(item);
            transaction.commit();
        } catch (HibernateException hibernateException) {
            throw new PropertySetException("Could not save key '" + key + "': " + hibernateException.getMessage());
        } finally {
            
            if (transaction != null && transaction.isActive())
                 transaction.rollback();
            
            if (session != null) 
                session.close();
        }
        
        return item;
    }


    public void remove(Long piid, String key) {


        if( piid == null)
            throw new PropertySetException("Could not remove property for 'null' piid");

        if( key == null)
            throw new PropertySetException("Could not remove property with 'null' key");

       Session session = null;
       Transaction transaction = null;

        try {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.delete(getItem(session, piid, key));
            session.flush();
            transaction.commit();
        } catch (HibernateException hibernateException) {
            throw new PropertySetException("Could not remove key '" + key + "': " + hibernateException.getMessage());
        } finally {
            
            if (transaction != null && transaction.isActive())
                 transaction.rollback();
            
            if (session != null) 
                session.close();
        }
    }

    public void remove(Long piid) {

        if( piid == null)
            throw new PropertySetException("Could not remove properties for 'null' piid");

        Session session = null;
        Transaction transaction = null;

        try  {
            session = this.sessionFactory.openSession();
            transaction = session.beginTransaction();

            Collection<String> keys = getKeys(piid, null, null);

            for(String key : keys)
                session.delete(getItem(session, piid, key));

            transaction.commit();
        } catch( HibernateException hibernateException ) {
            throw new PropertySetException("Could not remove all keys: " + hibernateException.getMessage());
        } finally {
            // Rollback if 'commit' failed 
            if(transaction != null && transaction.isActive())
                transaction.rollback();

            if (session != null) 
                session.close();
        }
    }

    // P R O T E C T E D   M E T H O D S  -----------------------------------------------------

    protected HibernatePropertySetItem getItem(Session session, Long piid, String key) throws HibernateException {

        if( piid == null || key == null)
            return null;

        return (HibernatePropertySetItem) session.createCriteria(getPersistentClass())
            .add(Restrictions.eq("processInstanceId", piid))
            .add(Restrictions.eq("key", key))
            .uniqueResult();

    }

    @SuppressWarnings("unchecked")
    protected Collection<String> getKeysImpl(Session session, Long piid, String prefix, Type type) throws HibernateException {

        if( piid == null )
            return Collections.EMPTY_LIST;

        Criteria criteria = session.createCriteria(getPersistentClass())
            .add(Restrictions.eq("processInstanceId", piid))
            .setProjection(Projections.property("key"));

        if (prefix != null)
            criteria.add(Restrictions.ilike("key", prefix, MatchMode.START));
            
        if(type != null)
            criteria.add(Restrictions.eq("type", type.getValue()));
        
        return criteria.list();
    }

}