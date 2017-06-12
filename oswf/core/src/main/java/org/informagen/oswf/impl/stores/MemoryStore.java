package org.informagen.oswf.impl.stores;

import org.informagen.oswf.impl.stores.AbstractWorkflowStore;

import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.PeristentVarsStore;

import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.Step;

import org.informagen.oswf.exceptions.WorkflowStoreException;

import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import org.informagen.oswf.impl.DefaultStep;
import org.informagen.oswf.impl.DefaultProcessInstance;
import org.informagen.oswf.impl.MemoryPeristentVarsStore;

import java.security.InvalidParameterException;


// Java - Collections
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// Java - Util
import java.util.Date;
import java.util.Iterator;


/**
 * Workflow store implementation using Java Collections Maps
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */

public class MemoryStore extends AbstractWorkflowStore  {

    // Instance Variables =====================================================================

    private static Map<Long,ProcessInstance> piidCache = new HashMap<Long,ProcessInstance>();
    private static Map<Long,List<Step>>    currentStepsCache = new HashMap<Long,List<Step>>();
    private static Map<Long,List<Step>>    historyStepsCache = new HashMap<Long,List<Step>>();
    private static Map<Long,PersistentVars>      propertySetCache = new HashMap<Long,PersistentVars>();

    private static long nextPIID = 1;
    private static long nextStepId = 1;


    public MemoryStore() {
        setTypedMapStore(new MemoryPeristentVarsStore());
    }

    public MemoryStore(Map<String,String> config, Map<String,Object> args) {
        super(config, args);
    }

    // WorkflowStore methods ==================================================================

    public void setTypedMapStore(PeristentVarsStore typedMapStore) {
        //throw new UnsupportedOperationException("CollectionStore uses MemoryPeristentVarsStore and cannot be set");
    }


    /**
     ** returns the PersistentVars for a Process Instance; Here from a Map class
     **
     */

    public PersistentVars getPersistentVars(long piid) {
        return getTypedMapStore().getPersistentVars(piid);
    }
        
    public ProcessInstance createEntry(String workflowName) throws WorkflowStoreException {

        long piid = nextPIID++;
        ProcessInstance pi = new DefaultProcessInstance(piid, workflowName);
        
        piidCache.put(piid, pi);
        currentStepsCache.put(piid, new ArrayList());
        historyStepsCache.put(piid, new ArrayList());
        //propertySetCache.put(piid, new MemoryTypedMap());

        return pi;
    }

    public Step createCurrentStep(long piid, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) {

        long id = nextStepId++;
        Step step = new DefaultStep(id, piid, stepId, 0, owner, startDate, dueDate, null, status, previousIds, null);

        findCurrentSteps(piid).add(step);

        return step;
    }


    public ProcessInstance findProcessInstance(long piid) {
        return piidCache.get(piid);
    }

    public List<Step> findCurrentSteps(long piid) {
        return currentStepsCache.get(piid);
    }

    public List<Step> findHistorySteps(long piid) {
        return historyStepsCache.get(piid);
    }

    public void moveToHistory(Step currentStep, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException {

        List<Step> currentSteps = findCurrentSteps(currentStep.getProcessInstanceId());
        List<Step> historySteps = findHistorySteps(currentStep.getProcessInstanceId());

        markFinished(currentStep, actionId, finishDate, status, actor);

        // Collections manipulation
        for(Step aStep : currentSteps) {

            if (currentStep.getId() == aStep.getId()) {
                currentSteps.remove(aStep);
                historySteps.add(0, currentStep);  // Should History steps be appended or prepended?
                break;
            }
        }
    }

    public List<Long> query(WorkflowExpressionQuery query) {

        // List of EntyIds which satisfy this 'query'
        ArrayList<Long> results = new ArrayList<Long>();

        // Check each workflow pi in our memory store; If it 
        //  satisfies the query conditions add it to the return list
        //  Works for Unit Testing not scalable if used in RDBMS
        
        for(ProcessInstance pi : piidCache.values())
            if ( doesIdSatifyQuery(pi.getProcessInstanceId(), query) ) 
                results.add(pi.getProcessInstanceId());

        return results;
    }

    // End: WorkflowStore interface ===========================================================
    

    // MemoryStore methods ============================================================

    /**
    * Reset the MemoryStore so it doesn't have any information.
    *   Useful for Unit Testing and when you don't want the MemoryStore to 
    *   have old data in it.
    */

    public static void reset() {  

        nextPIID = 1;
        nextStepId = 1;

        piidCache.clear();
        currentStepsCache.clear();
        historyStepsCache.clear();
        propertySetCache.clear();
        
        MemoryPeristentVarsStore.clear();
    }

    // Abstract method implementations =======================================================


    // Query support ==========================================================================

    // returns match / no match for each EntryId tested
    
    
    private boolean doesIdSatifyQuery(long piid, WorkflowExpressionQuery query) {

        Expression expression = query.getExpression();

        if (expression.isNested()) {
            return checkNestedExpression(piid, (NestedExpression) expression);
        } else {
            return checkExpression(piid, (FieldExpression) expression);
        }
    }

    private boolean checkExpression(long piid, FieldExpression expression) {
        
        Context context = expression.getContext();
        Field field = expression.getField();
        Operator operator = expression.getOperator();
        Object value = expression.getValue();

        Long id = new Long(piid);

        if (context == Context.ENTRY) {
            
            DefaultProcessInstance theEntry = (DefaultProcessInstance) piidCache.get(id);

            if (field == Field.NAME)
                return compareText(theEntry.getWorkflowName(), (String) value, operator);

            if (field == Field.STATE) 
                return compareLong(((Integer)value).intValue(), theEntry.getState().getValue(), operator);

            throw new InvalidParameterException("unknown field");
        }

        List<Step> steps;

        if (context == Context.CURRENT_STEPS) {
            steps = currentStepsCache.get(id);
        } else if (context == Context.HISTORY_STEPS) {
            steps = historyStepsCache.get(id);
        } else {
            throw new InvalidParameterException("unknown field context");
        }

        if (steps == null) {
            return false;
        }

        boolean expressionResult = false;

        switch (field) {
            case ACTION:

                long actionId = ((Integer)value).intValue();

                for(Step step : steps) {
                    if (compareLong(step.getActionId(), actionId, operator)) {
                        expressionResult = true;
                        break;
                    }
                }

                break;

            case ACTOR:

                String actor = (String) value;

                for(Step step : steps) {
                    if (compareText(step.getActor(), actor, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case FINISH_DATE:

                Date finishDate = (Date) value;

                // for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                //     DefaultStep step = (DefaultStep) iterator.next();
                for(Step step : steps) {
                    if (compareDate(step.getFinishDate(), finishDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case OWNER:

                String owner = (value != null) ? value.toString() : null;

                // for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                //     DefaultStep step = (DefaultStep) iterator.next();
                for(Step step : steps) {
                    if (compareText(step.getOwner(), owner, operator)) {
                        expressionResult = true;
                        break;
                    }
                }

                break;

            case START_DATE:

                Date startDate = (Date) value;

                // for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
                //     DefaultStep step = (DefaultStep) iterator.next();
                for(Step step : steps) {
                    if (compareDate(step.getStartDate(), startDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case STEP:

                int stepId = ((Integer)value).intValue();

                for(Step step : steps) {
                    if (compareLong(step.getStepId(), stepId, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case STATUS:

                String status = (String) value;

                for(Step step : steps) {
                    if (compareText(step.getStatus(), status, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;

            case DUE_DATE:

                Date dueDate = (Date) value;

                for(Step step : steps) {
                    if (compareDate(step.getDueDate(), dueDate, operator)) {
                        expressionResult = true;

                        break;
                    }
                }

                break;
        }

        if (expression.isNegate()) {
            return !expressionResult;
        } else {
            return expressionResult;
        }
    }

    private boolean checkNestedExpression(long entryId, NestedExpression nestedExpression) {

        for (int i = 0; i < nestedExpression.getExpressionCount(); i++) {

            boolean expressionResult;

            Expression expression = nestedExpression.getExpression(i);

            if (expression.isNested()) {
                expressionResult = checkNestedExpression(entryId, (NestedExpression) expression);
            } else {
                expressionResult = checkExpression(entryId, (FieldExpression) expression);
            }

            if (nestedExpression.getExpressionOperator() == LogicalOperator.AND) {
                if (expressionResult == false) {
                    return nestedExpression.isNegate();
                }
            } else if (nestedExpression.getExpressionOperator() == LogicalOperator.OR) {
                if (expressionResult == true) {
                    return !nestedExpression.isNegate();
                }
            }
        }

        if (nestedExpression.getExpressionOperator() == LogicalOperator.AND) {
            return !nestedExpression.isNegate();
        } else if (nestedExpression.getExpressionOperator() == LogicalOperator.OR) {
            return nestedExpression.isNegate();
        }

        throw new InvalidParameterException("unknown operator");
    }

    private boolean compareDate(Date value1, Date value2, Operator operator) {
        
        switch (operator) {
            
            case EQUALS:
                return value1.compareTo(value2) == 0;

            case NOT_EQUALS:
                return value1.compareTo(value2) != 0;

            case GT:
                return (value1.compareTo(value2) > 0);

            case LT:
                return value1.compareTo(value2) < 0;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private boolean compareLong(long value1, long value2, Operator operator) {
        
        switch (operator) {
            
            case EQUALS:
                return value1 == value2;

            case NOT_EQUALS:
                return value1 != value2;

            case GT:
                return value1 > value2;

            case LT:
                return value1 < value2;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private boolean compareText(String value1, String value2, Operator operator) {
        
        switch (operator) {
            
            case EQUALS:
                return notNull(value1).equals(value2);

            case NOT_EQUALS:
                return notNull(value1).equals(value2) == false;

            case GT:
                return notNull(value1).compareTo(value2) > 0;

            case LT:
                return notNull(value1).compareTo(value2) < 0;
        }

        throw new InvalidParameterException("unknown field operator");
    }

    private String notNull(String string) {
        return (string != null) ? string : "";
    }

}
