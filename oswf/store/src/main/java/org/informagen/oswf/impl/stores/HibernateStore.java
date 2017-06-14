package org.informagen.oswf.impl.stores;

// OSWf - Interfaces
import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf - Exceptions
import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.exceptions.QueryNotSupportedException;

// OSWf - Workflow Store
import org.informagen.oswf.impl.DefaultProcessInstance;
import org.informagen.oswf.impl.stores.AbstractWorkflowStore;

// Hibernate
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;

// Hibernate - Criteria
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

// SLF4J Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Java - Collections
import java.util.Collection;
import java.util.Map;

import java.util.Set;
import java.util.HashSet;

import java.util.List;
import java.util.ArrayList;

// Java Util
import java.util.Date;

/**
 * @author will gilbert
 *
 */

public class HibernateStore extends AbstractWorkflowStore {

    private static final Marker fatal = MarkerFactory.getMarker("FATAL");
    private static final Logger logger = LoggerFactory.getLogger(HibernateStore.class);
    
    
    // I N S T A N C E   F I E L D S  ---------------------------------------------------------

    SessionFactory sessionFactory;

    private String cacheRegion = null;
    private boolean cacheable = false;
    
    // M E T H O D S  -------------------------------------------------------------------------


    public HibernateStore(Map<String,String> config, Map<String,Object> parameters) throws WorkflowStoreException {
        super(config, parameters);
        
        sessionFactory = (SessionFactory)parameters.get("sessionFactory");
        
        if(sessionFactory == null)
            throw new WorkflowStoreException("Hibernate SessionFactory not configured in persistence store");
    }

    // What are these for???  Remove at some point WAG
    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }
    
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }
 
    protected String getCacheRegion() {
        return cacheRegion;
    }
    
    protected boolean isCacheable() {
        return cacheable;
    }

    // W O R K F L O W S T O R E  -------------------------------------------------------------

    public void setEntryState(final long entryId, final ProcessInstanceState state) throws WorkflowStoreException {

        Session session = null;

        try {
            session = sessionFactory.openSession();
            ProcessInstance entry = loadEntry(session, entryId);
  
            // 'save' method will provide transactions
            if(entry != null) {
                entry.setState(state);
                save(session, entry);
            }


        } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (session != null)
                session.close();
        }
            
    }


    public Step createCurrentStep(final long entryId, final int stepId, final String owner, final Date startDate, final Date dueDate, final String status, final long[] previousIds) throws WorkflowStoreException {

        Session session = null;
        HibernateStep step = null;

        try {
            
            session = sessionFactory.openSession();

            HibernateProcessInstance entry = loadEntry(session, entryId);
            step = new HibernateCurrentStep();

            step.setStepId(stepId);
            step.setOwner(owner);
            step.setStartDate(startDate);
            step.setDueDate(dueDate);
            step.setStatus(status);

            // This is for backward compatibility, but current Store doesn't 
            // persist this collection, nor is such property visibile outside 
            // OSWF internal classes

            List<Step> previousSteps = new ArrayList<Step>(previousIds.length);

            for (int i = 0; i < previousIds.length; i++) {
                HibernateStep previousStep =  new HibernateCurrentStep();
                previousSteps.add(previousStep);
            }

            step.setPreviousSteps(previousSteps);
            entry.addCurrentStep(step);

            // We need to save here because we soon will need the stepId 
            // that hibernate calculate on save or flush

            save(session, step);

        } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (session != null)
                session.close();
        }

        return step;
    }


    public ProcessInstance createEntry(String workflowName) throws WorkflowStoreException {

        Session session = null;
        HibernateProcessInstance workflowEntry = null;
        
        try {
            session = sessionFactory.openSession();
            workflowEntry = new HibernateProcessInstance();

            workflowEntry.setState(ProcessInstanceState.INITIATED);
            workflowEntry.setWorkflowName(workflowName);
            
            save(session, workflowEntry);
            
        } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {
 
            if (session != null)
                session.close();
        }

        return workflowEntry;
    }


    public List<Step> findCurrentSteps(final long entryId) throws WorkflowStoreException {

        Session session = null;
        Transaction transaction = null;
        List<Step> steps = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            steps = loadEntry(session, entryId).getCurrentSteps();

            transaction.commit();

         } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (transaction != null && transaction.isActive())
                 transaction.rollback();

            if (session != null)
                session.close();
        }
               
        return steps;
    }

    public ProcessInstance findProcessInstance(long entryId) throws WorkflowStoreException {
 
        Session session = null;
        Transaction transaction = null;
        ProcessInstance workflowEntry = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            workflowEntry = loadEntry(session, entryId);

            transaction.commit();

        } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (transaction != null && transaction.isActive())
                 transaction.rollback();

            if (session != null)
                session.close();
        }
        
        return workflowEntry;
    }

    public List<Step> findHistorySteps(final long entryId) throws WorkflowStoreException {

        Session session = null;
        Transaction transaction = null;
        List<Step> steps = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            steps = loadEntry(session, entryId).getHistorySteps();

            transaction.commit();

         } catch (HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (transaction != null && transaction.isActive())
                 transaction.rollback();
            
            if (session != null)
                session.close();
        }
               
        return steps;
    }


    public void moveToHistory(Step step, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException {

        Session session = null;
        
        try {
            
            session = sessionFactory.openSession();
            session.refresh(step);

            super.markFinished(step, actionId, finishDate, status, actor);
            update(session, step);

            HibernateStep currentStep = (HibernateStep)step;
            HibernateStep historyStep = new HibernateHistoryStep(currentStep);  
            HibernateProcessInstance entry = currentStep.getEntry();

            entry.removeCurrentStep(currentStep);
            entry.addHistoryStep(historyStep);

            // Session transaction managed by the delete, save and update DAO methods
            delete(session, currentStep);
            save(session, historyStep);
            update(session, entry);
        
            if(logger.isDebugEnabled()) {
                logger.debug(historyStep.toString());
                logger.debug(entry.toString());
            }
       
        } catch(HibernateException hibernateException) {
            throw new WorkflowStoreException(hibernateException);
        } finally {

            if (session != null)
                session.close();
        }
        
        // Logging the results of this action

    }


    // ~ Entity Instance & Fetch methods //////////////////////////////////////////////////////
    
    protected HibernateProcessInstance loadEntry(Session session, final long piid) throws WorkflowStoreException {

        HibernateProcessInstance workflowEntry = (HibernateProcessInstance)session.load(HibernateProcessInstance.class, piid);
        
        // The next line forces the ORM to load the properties into the object; This should not be lazy loading
        //  but seems to be occuring; Access any property loads them all.  Work-around?

        workflowEntry.getState();
        return workflowEntry;
    }
    
    // DAO methods ////////////////////////////////////////////////////////////////////////////


    protected void delete(Session session, final Object entity) throws WorkflowStoreException {

       Transaction transaction = null;

         try {
             transaction = session.beginTransaction();
             session.delete(entity);
             transaction.commit();
         } catch (HibernateException hibernateException) {
             
             if (transaction != null)
                 transaction.rollback();
                 
             logger.error(fatal, hibernateException.toString());
             throw new WorkflowStoreException(hibernateException);
        } 

    }


    protected void save(final Session session, final Object entity) throws HibernateException {
 
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (HibernateException hibernateException) {
            
            if (transaction != null)
                transaction.rollback();
                
            logger.error(fatal, hibernateException.toString());
            throw hibernateException;
        } 
        
    }

    protected void update(Session session, final Object entity) throws WorkflowStoreException {
 
        Transaction transaction = null;

          try {
              transaction = session.beginTransaction();
              session.update(entity);
              transaction.commit();
          } catch (HibernateException hibernateException) {
              
              if (transaction != null)
                  transaction.rollback();
                  
              logger.error(fatal, hibernateException.toString());
              throw new WorkflowStoreException(hibernateException);
        } 
    }
 

    // OSWf Query Support using Hibernate Criteria ////////////////////////////////////////////

     public List<Long> query(final WorkflowExpressionQuery query) throws WorkflowStoreException {

        Session session = null;
        Transaction transaction = null;
        Set<Long> results = new HashSet<Long>();

        try {
            
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            Expression expression = query.getExpression();
            Class entityClass = getQueryClass(expression, null);

            Criterion expr = (expression.isNested()) ?
                buildNested((NestedExpression) expression) :
                queryComparison((FieldExpression) expression);

            logger.debug(entityClass.toString());
            logger.debug(expr.toString());

            Criteria criteria = session.createCriteria(entityClass);
            criteria.setCacheable(isCacheable());

            if (isCacheable()) 
                criteria.setCacheRegion(getCacheRegion());

            criteria.add(expr);
            
            // Get all of the ids which satisfy the criteria
            
            for(Object next : criteria.list()) {
                
                if (next instanceof HibernateStep) {
                    results.add(((HibernateStep)next).getEntryId());
                } else {
                    results.add(((ProcessInstance) next).getProcessInstanceId());
                }
            }

            transaction.commit();

        } catch (HibernateException hibernateException) {
            
            if (transaction != null)
                transaction.rollback();
                
            logger.error(fatal, hibernateException.toString());
            throw new WorkflowStoreException(hibernateException);

        } catch (QueryNotSupportedException queryNotSupportedException) {
            throw new WorkflowStoreException(queryNotSupportedException);
        } finally {
            if (session != null)
                session.close();
        }


        return new ArrayList<Long>(results);
    }

    private String getFieldName(Field field) {
        
        switch (field) { 
            
            case ACTION: // actionId
                return "actionId";

            case ACTOR:
                return "actor";

            case FINISH_DATE:
                return "finishDate";

            case OWNER:
                return "owner";

            case START_DATE:
                return "startDate";

            case STEP: // stepId
                return "stepId";

            case STATUS:
                return "status";

            case STATE: // Integer value of State enum
                return "stateId";

            case NAME:
                return "workflowName";

            case DUE_DATE:
                return "dueDate";

            default:
                return "1";
        }
    }


    private Criterion buildNested(NestedExpression nestedExpression) {
        
        Criterion full = null;

        for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
            Criterion expr;
            Expression expression = nestedExpression.getExpression(i);

            if (expression.isNested()) {
                expr = buildNested((NestedExpression) nestedExpression.getExpression(i));
            } else {
                FieldExpression sub = (FieldExpression) nestedExpression.getExpression(i);
                expr = queryComparison(sub);

                if (sub.isNegate()) {
                    expr = Restrictions.not(expr);
                }
            }

            if (full == null) {
                full = expr;
            } else {
                switch (nestedExpression.getExpressionOperator()) {
                case AND:
                    full = Restrictions.and(full, expr);
                    break;

                case OR:
                    full = Restrictions.or(full, expr);
                }
            }
        }

        return full;
    }

    private Criterion queryComparison(FieldExpression expression) {

        Operator operator = expression.getOperator();

        switch (operator) {
            case EQUALS:
                return Restrictions.eq(getFieldName(expression.getField()), expression.getValue());
    
            case NOT_EQUALS:
                return Restrictions.not(Restrictions.like(getFieldName(expression.getField()), expression.getValue()));
    
            case GT:
                return Restrictions.gt(getFieldName(expression.getField()), expression.getValue());
    
            case LT:
                return Restrictions.lt(getFieldName(expression.getField()), expression.getValue());
    
            default:
                return Restrictions.eq(getFieldName(expression.getField()), expression.getValue());
        }
    }


    private Class getQueryClass(Expression expr, Collection<Class> classesCache) throws QueryNotSupportedException {

        if (classesCache == null) 
            classesCache = new HashSet<Class>();

        if (expr instanceof FieldExpression) {
            
            FieldExpression fieldExpression = (FieldExpression) expr;

            switch (fieldExpression.getContext()) {
                
                case CURRENT_STEPS:
                    classesCache.add(HibernateCurrentStep.class);

                    break;

                case HISTORY_STEPS:
                    classesCache.add(HibernateHistoryStep.class);

                    break;

                case ENTRY:
                    classesCache.add(HibernateProcessInstance.class);

                    break;

                default:
                    throw new QueryNotSupportedException("Query for unsupported context " + fieldExpression.getContext());
            }
            
        } else {

            NestedExpression nestedExpression = (NestedExpression) expr;

            for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {
                Expression expression = nestedExpression.getExpression(i);

                if (expression.isNested()) {
                    classesCache.add(getQueryClass(nestedExpression.getExpression(i), classesCache));
                } else {
                    classesCache.add(getQueryClass(expression, classesCache));
                }
            }
        }

        if (classesCache.size() > 1) {
            throw new QueryNotSupportedException("Store does not support nested queries of different types (types found:" + classesCache + ")");
        }

        return classesCache.iterator().next();
    }


}
