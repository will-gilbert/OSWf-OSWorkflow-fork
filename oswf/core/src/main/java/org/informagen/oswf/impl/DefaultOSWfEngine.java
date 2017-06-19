package org.informagen.oswf.impl;

// OSWf - Core
import org.informagen.oswf.Condition;
import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;
import org.informagen.oswf.Register;
import org.informagen.oswf.JoinSteps;
import org.informagen.oswf.TypeResolver;
import org.informagen.oswf.Validator;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.WorkflowStore;
import org.informagen.oswf.ProcessInstanceState;

// OSWf - Implementations
import org.informagen.oswf.impl.DefaultWorkflowContext;
 
// OSWf - OSWfConfiguration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf - Workflow Descriptors
import org.informagen.oswf.descriptors.AbstractDescriptor;
import org.informagen.oswf.descriptors.ActionDescriptor;
import org.informagen.oswf.descriptors.ConditionDescriptor;
import org.informagen.oswf.descriptors.ConditionsDescriptor;
import org.informagen.oswf.descriptors.ConditionalResultDescriptor;
import org.informagen.oswf.descriptors.FunctionDescriptor;
import org.informagen.oswf.descriptors.StepConditionDescriptor;
import org.informagen.oswf.descriptors.RegisterDescriptor;
import org.informagen.oswf.descriptors.RestrictionDescriptor;
import org.informagen.oswf.descriptors.ResultDescriptor;
import org.informagen.oswf.descriptors.JoinDescriptor;
import org.informagen.oswf.descriptors.SplitDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;
import org.informagen.oswf.descriptors.ValidatorDescriptor;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf - Query
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf Util
import org.informagen.oswf.VariableResolver;

// OSWf - Exceptions
import org.informagen.oswf.exceptions.InvalidActionException;
import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.exceptions.InvalidEntryStateException;
import org.informagen.oswf.exceptions.WorkflowLoaderException;


// OpenSymphony PropertySet
import org.informagen.oswf.PersistentVars;
import org.informagen.oswf.MemoryPersistentVars;

// Simple Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

// Java Collections
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.EMPTY_LIST;

// Java Util
import java.util.Date;
import java.util.Properties;


/**********************************************************************************************
 *
 * Default workflow instance that serves as the base for specific implementations.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 * @author Hani Suleiman
 */

public class DefaultOSWfEngine implements OSWfEngine {

    //~ Static fields/initializers ////////////////////////////////////////////////////////////

    protected final Marker fatal = MarkerFactory.getMarker("FATAL");
    protected final Logger logger = LoggerFactory.getLogger(DefaultOSWfEngine.class);

    //~ Instance fields ///////////////////////////////////////////////////////////////////////

    protected final WorkflowContext context;
    protected OSWfConfiguration configuration;
    private TypeResolver typeResolver;
    
    protected final ThreadLocal stateCache = new ThreadLocal();

    //~ Constructors //////////////////////////////////////////////////////////////////////////

    public DefaultOSWfEngine() {
        this(new DefaultWorkflowContext(""));
    }

    public DefaultOSWfEngine(String actor) {
        this(new DefaultWorkflowContext(actor));
    }

    public DefaultOSWfEngine(WorkflowContext context) {
        this.context = context;
        stateCache.set(new HashMap());
    }

    //~ Methods ///////////////////////////////////////////////////////////////////////////////
    // canInitialize //////////////////////////////////////////////////////////////////////////

    /**
     * @param workflowName the name of the workflow to check
     * @param initialAction The initial action to check
     * @return true if the workflow can be initialized
     */

    public boolean canInitialize(String workflowName, int initialAction) {
        return canInitialize(workflowName, initialAction, EMPTY_MAP);
    }

    /**
     * @param workflowName the name of the workflow to check
     * @param initialAction The initial action to check
     * @param inputs the inputs map
     * @return true if the workflow can be initialized
     */

    public boolean canInitialize(final String workflowName, int initialAction, Map<String,Object> inputs) {
        
        // Create an minimal inner entry for testing process creation
        ProcessInstance mockEntry = new ProcessInstance() {
            public Long getProcessInstanceId() { return null;}
            public String getWorkflowName() { return workflowName; }
            public boolean isInitialized() { return false; }
            public ProcessInstanceState getState() { return ProcessInstanceState.INITIATED; }
            public void setState(ProcessInstanceState state) {}
        };

        // Since no state change happens here, use a memory store and create
        //   an empty set of 'transientVars'

        Map<String, Object> transientVars = new HashMap<String, Object>();
        
        // Add any input variables in case the process description uses them during initialization
        if (inputs != null) 
            transientVars.putAll(inputs);

        // 'populateTransientMap' will :
        //  - add OSWf variables to transientVars
        //  - add CurrentSteps to transientVars
        //  - add Register variable names and values to transientVars
        // Catch and log exceptions as errors and return false
        
        boolean canInitialize = false;
        try {
            PersistentVars persistentVars = new MemoryPersistentVars();
            populateTransientMap(mockEntry, transientVars, EMPTY_LIST, new Integer(initialAction), EMPTY_LIST, persistentVars);
            canInitialize = canInitialize(workflowName, initialAction, transientVars, persistentVars);
        } catch (InvalidActionException invalidActionException) {
            logger.error(invalidActionException.getMessage());
        } catch (WorkflowException workflowException) {
            logger.error("Error checking canInitialize", workflowException);
        }
        
        return canInitialize;
    }


    /** 
    ** Create the ProcessInstance (Process Instance) and execute the initial action to move 
    **  into a Step (state) and return the ProcessInstance ID; There are no inputs.
    */ 

    public long initialize(String workflowName, int initialAction) throws InvalidActionException, 
                                                                          InvalidInputException, 
                                                                          WorkflowException {
         return initialize(workflowName, initialAction, EMPTY_MAP);

     }

    /**
    **
    ** Create the ProcessInstance (Process Instance) and execute the initial action to move 
    **  into a Step (state) and return the ProcessInstance ID 
    **  a.k.a. a Process Instance ID, piid
    */
    
    public long initialize(String workflowName, int initialAction, Map<String,Object> inputs) throws InvalidActionException, 
                                                                                                     InvalidInputException, 
                                                                                                     WorkflowException {
        
        // Get Process Description, Process Store, Process Instance and Process Instance ID

        WorkflowDescriptor pd = getOSWfConfiguration().getWorkflow(workflowName);
        WorkflowStore store = getPersistence();
        ProcessInstance pi = store.createEntry(workflowName);
        long piid = pi.getProcessInstanceId();

        // Get persistent variables from the persistence store; Create transient variables
        PersistentVars persistentVars = store.getPersistentVars(piid);
        Map<String,Object> transientVars = new HashMap<String,Object>();

        // Copy all of the inputs into the transient variables
        if (inputs != null) {
            transientVars.putAll(inputs);
        }
        
        // Add OSWf variables, CurrentSteps and Registers
        populateTransientMap(pi, transientVars, pd.getRegisters(), new Integer(initialAction), EMPTY_LIST, persistentVars);
        
        // Attempt initialize the Process Instance
        if (canInitialize(workflowName, initialAction, transientVars, persistentVars) == false) {
            context.setRollbackOnly();
            throw new InvalidActionException("You are restricted from initializing this workflow");
        }
        
        // Get the 'initial-action' definition
        ActionDescriptor action = pd.getInitialAction(initialAction);
        
        // Fire the 'initial-action' transition, catch exceptions
        // NB: Initial actions do not create a History Step
        try {
            fireTransition(pi, EMPTY_LIST, store, pd, action, transientVars, inputs, persistentVars);
        } catch (WorkflowException workflowException) {
            context.setRollbackOnly();
            throw workflowException;
        }
         
        // Return the process instance ID
        return piid;
    }

    /**
    **
    ** Fires a process transition, this method can be chained becuase it returns itself
    **
    */
    
    public OSWfEngine doAction(long piid, int actionId) throws WorkflowException {
        return doAction(piid, actionId, EMPTY_MAP);
    }

    /**
    **
    ** Fires a process transition, this method can be chained becuase it returns itself
    **
    */
    
    public OSWfEngine doAction(long piid, int actionId, Map<String,Object> inputs) throws WorkflowException {

        WorkflowStore store = getPersistence();
        ProcessInstance pi = store.findProcessInstance(piid);
        
        // Can only work with active processes
        if (pi.getState() != ProcessInstanceState.ACTIVE)
            throw new InvalidActionException("Process Instance is not ACTIVATED; Action " + actionId + " is invalid");
        
        // Get Process Description, Current Steps and Persistent Variables; Create a
        //  transientVars Map and add input and OSWf variables

        WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());
        List<Step> currentSteps = store.findCurrentSteps(piid);
        PersistentVars persistentVars = store.getPersistentVars(piid);
        Map<String,Object> transientVars = new HashMap<String,Object>();

        if (inputs != null) 
            transientVars.putAll(inputs);

        populateTransientMap(pi, transientVars, wf.getRegisters(), new Integer(actionId), currentSteps, persistentVars);

        // Find the applicable global or step action using the action ID
        ActionDescriptor action = null;

        // First search the global actions for the action ID
        for (ActionDescriptor actionDesc : wf.getGlobalActions()) {
            if (actionDesc.getId() == actionId && isActionAvailable(actionDesc, transientVars, persistentVars, 0)) {
                action = actionDesc;
                break;
            }
        } 
 
        // Didn't find a global action, search the step actions for the actionID
       if(action == null) {
            for ( Step step : currentSteps ) {
                StepDescriptor s = wf.getStep(step.getStepId());
                for ( ActionDescriptor actionDesc : s.getActions() ) {
                    if (actionDesc.getId() == actionId && isActionAvailable(actionDesc, transientVars, persistentVars, s.getId()) ) {  
                        action = actionDesc;
                        break;
                    }
                }
            }
       }
 
        // The requested action ID was not found; throw an exception 
        if (action == null) 
            throw new InvalidActionException("Action " + actionId + " was not found");

        try {
            
            // Execute the transition, 
            boolean completed = fireTransition(pi, currentSteps, store, wf, action, transientVars, inputs, persistentVars);
            
            //if the process instance isn't finished,  check for an implicit finish            
            if ( completed == false)
                checkImplicitFinish(action, piid);
            
        } catch (WorkflowException workflowException) {
            context.setRollbackOnly();
            throw workflowException;
        }
        
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Get the available actions for the specified workflow instance.
     * @param id The workflow instance id.
     * @return An array of action id's that can be performed on the specified pi.
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     */
     
    public List<Integer> getAvailableActions(long id) {
        return getAvailableActions(id, EMPTY_MAP);
    }

    /**
     * Get the available actions for the specified workflow instance.
     * @param piid The workflow instance id.
     * @param inputs The inputs map to pass on to conditions
     * @return An array of action id's that can be performed on the specified pi.
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     */
     
    public List<Integer> getAvailableActions(long piid, Map<String,Object> inputs) {

        try {
            
            WorkflowStore store = getPersistence();
            ProcessInstance pi = store.findProcessInstance(piid);

            if (pi == null) {
                throw new IllegalArgumentException("No such workflow piid " + piid);
            }

            if (pi.getState() != ProcessInstanceState.ACTIVE) {
                return new ArrayList<Integer>();
            }

            WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());

            if (wf == null) {
                throw new IllegalArgumentException("No such workflow " + pi.getWorkflowName());
            }

            List<Integer> list = new ArrayList<Integer>();
            PersistentVars persistentVars = store.getPersistentVars(piid);
            Map<String,Object> transientVars = (inputs == null) ? new HashMap<String,Object>() : new HashMap<String,Object>(inputs);
            Collection<Step> currentSteps = store.findCurrentSteps(piid);

            populateTransientMap(pi, transientVars, wf.getRegisters(), new Integer(0), currentSteps, persistentVars);

            // Get Global actions
            List<ActionDescriptor> globalActions = wf.getGlobalActions();
            
            for (ActionDescriptor action : globalActions) {
                
                RestrictionDescriptor restriction = action.getRestriction();
                ConditionsDescriptor conditions = null;

                transientVars.put("actionId", new Integer(action.getId()));

                if (restriction != null) {
                    conditions = restriction.getConditionsDescriptor();
                }

                //todo verify that 0 is the right currentStepId
                if (passesConditions(wf.getGlobalConditions(), transientVars, persistentVars, 0) && passesConditions(conditions, transientVars, persistentVars, 0)) {
                    list.add(new Integer(action.getId()));
                }
            }

            // Get Current Step(s) actions

            for(Step step : currentSteps) 
                list.addAll(getAvailableActionsForStep(wf, step, transientVars, persistentVars));

            return list;
            
        } catch (Exception e) {
            logger.error("Error checking available actions", e);
            return new ArrayList<Integer>();
        }
    }

    /**
     */ 
     
    public OSWfEngine setConfiguration(OSWfConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Returns the configuration for this workflow is set using setConfiguration.
     * If the configuration has not been set then it uses an in-memory configuration and
     *   looks for a configuation file named 'oswf.xml'
     *
     * @return The configuration that was set.
     *
     */

    public OSWfConfiguration getOSWfConfiguration() {
        
        OSWfConfiguration config = (configuration != null) ? configuration : new MemoryOSWfConfiguration();

        // Load the configuration file, 'oswf.xml', in the classpath
        if ( config.isInitialized() == false ) {
            try {
                config.load();
            } catch (WorkflowLoaderException e) {
                logger.error(fatal, "Error initializing configuration", e);

                // Fail fast, better to blow up with an NPE here rather than later
                return null;
            }
        }

        return config;
    }


    public ProcessInstanceState getProcessInstanceState(long piid) {

        ProcessInstanceState state = null;
        
        try {
            WorkflowStore store = getPersistence();
            state = store.findProcessInstance(piid).getState();
            if(state == null)
                throw new WorkflowStoreException("Persistence return null workflow state value");
        } catch (WorkflowStoreException e) {
            logger.error("Error checking instance state for piid " + piid, e);
            state = ProcessInstanceState.UNKNOWN;
        }

        return state;
    }

    public List<Step> getCurrentSteps(long piid) {
        try {
            WorkflowStore store = getPersistence();
            return store.findCurrentSteps(piid);
        } catch (WorkflowStoreException e) {
            logger.error("Error checking current steps for piid " + piid, e);
            return EMPTY_LIST;
        }
    }

    public List<Step> getHistorySteps(long piid) {
        try {
            WorkflowStore store = getPersistence();
            return store.findHistorySteps(piid);
        } catch (WorkflowStoreException e) {
            logger.error("Error getting history steps for piid " + piid, e);
        }

        return EMPTY_LIST;
    }

    public Properties getPersistenceProperties() {
        
        Properties properties = new Properties();

        for(Map.Entry<String,Object> entry : getOSWfConfiguration().getPersistenceArgs().entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue() == null ? null : entry.getValue().toString();
            properties.setProperty(name,  value);
        }
        return properties;
    }


    /**
     * Get the persistent variables for the specified process id
     * @param piid The process id
     */
    public PersistentVars getPersistentVars(long piid) {
        PersistentVars persistentVars = null;

        try {
            persistentVars = getPersistence().getPersistentVars(piid);
        } catch (WorkflowStoreException e) {
            logger.error("Error getting persistent variables for piid " + piid, e);
        }

        return persistentVars;
    }


    // TypeResolver ---------------------------------

    public void setResolver(TypeResolver resolver) {
        this.typeResolver = resolver;
    }

    public TypeResolver getTypeResolver() {
        
        if (typeResolver == null) 
            typeResolver = getOSWfConfiguration().getTypeResolver();

        return typeResolver;
    }
 
    //-------------------------------------------------------

    public Map<Integer,Set<StepCondition>> getStepConditions(long piid) {
        return getStepConditions(piid, EMPTY_MAP);
    }

    public Map<Integer,Set<StepCondition>> getStepConditions(long piid, Map<String,Object> inputs) {

        Map<Integer,Set<StepCondition>> stepConditionsMap = new HashMap<Integer,Set<StepCondition>>();

        try {
            
            WorkflowStore store = getPersistence();
            ProcessInstance pi = store.findProcessInstance(piid);
            WorkflowDescriptor wd = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());

            PersistentVars persistentVars = store.getPersistentVars(piid);
            Map<String,Object> transientVars = (inputs == null) ? new HashMap<String,Object>() : new HashMap<String,Object>(inputs);
            Collection<Step> currentSteps = store.findCurrentSteps(piid);
            populateTransientMap(pi, transientVars, wd.getRegisters(), null, currentSteps, persistentVars);

            for (Step step : currentSteps) {
                int stepId = step.getStepId();
                StepDescriptor sd = wd.getStep(stepId);
                List<StepConditionDescriptor> descriptors = sd.getStepConditions();
                
                Set<StepCondition> stepConditions = new HashSet<StepCondition>();
                for (StepConditionDescriptor descriptor : descriptors) {
                    
                    boolean passed = false;
                    if (descriptor.getRestriction() != null) {
                        ConditionsDescriptor cd = descriptor.getRestriction().getConditionsDescriptor();
                        passed = passesConditions(cd, transientVars, persistentVars, sd.getId());
                    }
                    
                    StepCondition stepCondition = new StepCondition(step.getStepId(), descriptor.getName(), passed);
                    stepCondition.setStepName(sd.getName());
                    stepConditions.add(stepCondition);
                }
                
                if(stepConditions.size() > 0)
                    stepConditionsMap.put(new Integer(stepId), stepConditions);
            }

        } catch (Exception e) {
            logger.error("Error getting stepConditions for piid " + piid, e);
        }

        return stepConditionsMap;
    }

    /**
     * Returns a workflow definition object associated with the given name.
     *
     * @param workflowName the name of the workflow
     * @return the object graph that represents a workflow definition
     */
    public WorkflowDescriptor getWorkflowDescriptor(String workflowName) {
        try {
            return getOSWfConfiguration().getWorkflow(workflowName);
        } catch (WorkflowLoaderException e) {
            logger.error("Error loading workflow: " + workflowName, e);
        }

        return null;
    }

    public String getWorkflowName(long piid) {
        try {
            WorkflowStore store = getPersistence();
            ProcessInstance pi = store.findProcessInstance(piid);

            if (pi != null) {
                return pi.getWorkflowName();
            }
        } catch (WorkflowStoreException e) {
            logger.error("Error getting instance name for piid " + piid, e);
        }

        return null;
    }

    /**
     * Get a list of workflow names available
     * @return a set of workflow names
     */

    public Set<String> getWorkflowNames() {
        
        try {
            return getOSWfConfiguration().getWorkflowNames();
        } catch (WorkflowLoaderException e) {
            logger.error("Error getting workflow names", e);
        }

        return new HashSet<String>();
    }


    public boolean canSwitchToProcessInstanceState(long piid, ProcessInstanceState newState) {

        boolean result = false;
        
        try {
            WorkflowStore store = getPersistence();
            ProcessInstance pi = store.findProcessInstance(piid);
            ProcessInstanceState currentState = pi.getState();

            switch (currentState) {

                case INITIATED:
                    if ((newState == ProcessInstanceState.RUNNING) || (newState == ProcessInstanceState.ACTIVE))
                    result = true;
                    break;

                case RUNNING:
                    if ((newState == ProcessInstanceState.ACTIVE) || 
                        (newState == ProcessInstanceState.COMPLETED) ||
                        (newState == ProcessInstanceState.SUSPENDED) ||
                        (newState == ProcessInstanceState.TERMINATED) ||
                        (newState == ProcessInstanceState.ARCHIVED) )
                        result = true;
                    break;

                case ACTIVE:
                    if ((newState == ProcessInstanceState.COMPLETED) || 
                        (newState == ProcessInstanceState.SUSPENDED) ||
                        (newState == ProcessInstanceState.TERMINATED) ||
                        (newState == ProcessInstanceState.ARCHIVED) )
                        result = true;
                    break;


                case SUSPENDED:
                    if ((newState == ProcessInstanceState.RUNNING) ||
                        (newState == ProcessInstanceState.ACTIVE) ||
                        (newState == ProcessInstanceState.TERMINATED) ||
                        (newState == ProcessInstanceState.ARCHIVED))
                        result = true;
                    break;
                
                case COMPLETED:
                    if (newState == ProcessInstanceState.ARCHIVED) 
                        result = true;
                    break;

                case TERMINATED:
                    if (newState == ProcessInstanceState.ARCHIVED)
                        result = true;
                    break;
                    
                case ARCHIVED:
                        result = false;
                    break;

                default:
                    throw new WorkflowStoreException("Invalid process state change requested");
            }

        } catch (WorkflowStoreException storeException) {
            logger.error("Error checking modifiable state for piid " + piid, storeException);
            return false;
        }

        return result;
    }

    public void changeEntryState(long piid, ProcessInstanceState newState) throws WorkflowException {
        
        WorkflowStore store = getPersistence();
        ProcessInstance pi = store.findProcessInstance(piid);

        if (pi.getState() == newState) 
            return;

        if (canSwitchToProcessInstanceState(piid, newState)) {
            
            if ((newState == ProcessInstanceState.TERMINATED) || (newState == ProcessInstanceState.COMPLETED)) {
                
                Collection currentSteps = getCurrentSteps(piid);

                if (currentSteps.size() > 0)
                    completeProcess(null, piid, currentSteps, newState);
            }

            store.setEntryState(piid, newState);
            
        } else {
            throw new InvalidEntryStateException("Can't transition workflow piid " + piid + ". Current state is " + pi.getState().getName() + ", requested state is " + newState.getName());
        }
         

        if (logger.isDebugEnabled()) 
            logger.debug("PIID:  " + piid + " is " + pi.getState().getName());

    }
 

    public void executeTriggerFunction(long piid, int triggerId) throws WorkflowException {
        WorkflowStore store = getPersistence();
        ProcessInstance pi = store.findProcessInstance(piid);

        if (pi == null) {
            logger.warn("Cannot execute trigger id " + triggerId + " on non-existent piid " + piid);
            return;
        }

        WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());

        PersistentVars persistentVars = store.getPersistentVars(piid);
        Map<String,Object> transientVars = new HashMap<String,Object>();
        populateTransientMap(pi, transientVars, wf.getRegisters(), null, store.findCurrentSteps(piid), persistentVars);
        executeFunction(wf.getTriggerFunction(triggerId), transientVars, persistentVars);
    }



    public List<Long> query(WorkflowExpressionQuery query) throws WorkflowException {
        return getPersistence().query(query);
    }

    /**
     * check if an action is available or not
     * @param action The action descriptor
     * @return true if the action is available
     */
    protected boolean isActionAvailable(ActionDescriptor action, Map<String,Object> transientVars, PersistentVars persistentVars, int stepId) throws WorkflowException {
        if (action == null) {
            return false;
        }

        WorkflowDescriptor wf = getWorkflowDescriptorForAction(action);

        Map cache = (Map) stateCache.get();

        Boolean result = null;

        if (cache != null) {
            result = (Boolean) cache.get(action);
        } else {
            cache = new HashMap();
            stateCache.set(cache);
        }

        if (result == null) {
            RestrictionDescriptor restriction = action.getRestriction();
            ConditionsDescriptor conditions = null;

            if (restriction != null) {
                conditions = restriction.getConditionsDescriptor();
            }

            result = new Boolean(passesConditions(wf.getGlobalConditions(), new HashMap(transientVars), persistentVars, stepId) && passesConditions(conditions, new HashMap(transientVars), persistentVars, stepId));
            cache.put(action, result);
        }

        return result.booleanValue();
    }

    protected List<Integer> getAvailableActionsForStep(WorkflowDescriptor workflowDescriptor, Step step, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {

        List<Integer> list = new ArrayList<Integer>();

        StepDescriptor stepDescriptor = workflowDescriptor.getStep(step.getStepId());

        if (stepDescriptor == null) {
            logger.warn("getAvailableActionsForStep called for non-existent step Id " + step.getStepId());
            return list;
        }

        List<ActionDescriptor> actions = stepDescriptor.getActions();

        if ((actions == null) || (actions.size() == 0)) {
            return list;
        }


        for (ActionDescriptor action : actions) {

            RestrictionDescriptor restriction = action.getRestriction();
            ConditionsDescriptor conditions = null;

            transientVars.put("actionId", new Integer(action.getId()));

            if (restriction != null)
                conditions = restriction.getConditionsDescriptor();

            if ( passesConditions(workflowDescriptor.getGlobalConditions(), new HashMap<String,Object>(transientVars), persistentVars, stepDescriptor.getId()) && 
                 passesConditions(conditions, new HashMap<String,Object>(transientVars), persistentVars, stepDescriptor.getId())) {
                list.add(new Integer(action.getId()));
            }
        }

        return list;
    }

    protected List<Integer> getAvailableAutoActions(long piid, Map<String,Object> inputs) {

        List<Integer> list = new ArrayList<Integer>();

        try {
            WorkflowStore store = getPersistence();
            ProcessInstance pi = store.findProcessInstance(piid);

            if (pi == null) {
                throw new IllegalArgumentException("No such process instance " + piid);
            }

            if (pi.getState() != ProcessInstanceState.ACTIVE) {
                logger.debug("--> state is " + pi.getState().getName());
                return list;
            }

            WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());

            if (wf == null) {
                throw new IllegalArgumentException("No such workflow " + pi.getWorkflowName());
            }

            PersistentVars persistentVars = store.getPersistentVars(piid);
            Map<String,Object> transientVars = (inputs == null) ? new HashMap<String,Object>() : new HashMap<String,Object>(inputs);
            Collection<Step> currentSteps = store.findCurrentSteps(piid);

            populateTransientMap(pi, transientVars, wf.getRegisters(), new Integer(0), currentSteps, persistentVars);

            // Get Global-Actions
            List<ActionDescriptor> globalActions = wf.getGlobalActions();

            for(ActionDescriptor action : globalActions) {
                
                transientVars.put("actionId", new Integer(action.getId()));

                if (action.getAutoExecute()) {
                    if (isActionAvailable(action, transientVars, persistentVars, 0)) {
                        list.add(new Integer(action.getId()));
                    }
                }
            }

            // Get Current Step(s) actions
            for (Step step : currentSteps) {
                list.addAll(getAvailableAutoActionsForStep(wf, step, transientVars, persistentVars));
            }

        } catch (Exception e) {
            logger.error("Error checking available actions", e);
            return list;
        }

        return list;
    }

    /**
     * Get just auto action availables for a step
     */

    protected List getAvailableAutoActionsForStep(WorkflowDescriptor wf, Step step, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {

        List list = new ArrayList();
        StepDescriptor s = wf.getStep(step.getStepId());

        if (s == null) {
            logger.warn("getAvailableAutoActionsForStep called for non-existent step Id " + step.getStepId());

            return list;
        }

        List<ActionDescriptor> actions = s.getActions();

        if ((actions == null) || (actions.size() == 0)) {
            return list;
        }

        for (ActionDescriptor action : actions) {
            transientVars.put("actionId", new Integer(action.getId()));

            // Collect auto actions
            if (action.getAutoExecute()) {
                if (isActionAvailable(action, transientVars, persistentVars, s.getId())) {
                    list.add(new Integer(action.getId()));
                }
            }
        }

        return list;
    }

    protected Step getCurrentStep(WorkflowDescriptor wfDesc, int actionId, List<Step> currentSteps, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {
        
        // Only one step, return it as the current step
        if (currentSteps.size() == 1) {
            return currentSteps.get(0);
        }

        for (Step step : currentSteps) {
            
            ActionDescriptor action = wfDesc.getStep(step.getStepId()).getAction(actionId);
            if (isActionAvailable(action, transientVars, persistentVars, step.getStepId())) {
                return step;
            }
        }

        return null;
    }

    protected WorkflowStore getPersistence() throws WorkflowStoreException {
        return getOSWfConfiguration().getWorkflowStore();
    }

    protected boolean canInitialize(String workflowName, int initialAction, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {

        // Attempt to get the 'initialAction' from the PD
        WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(workflowName);

        ActionDescriptor actionDescriptor = wf.getInitialAction(initialAction);

        if (actionDescriptor == null) {
            throw new InvalidActionException("Invalid initial-action = " + initialAction);
        }

        // Check against Restriction conditions
        RestrictionDescriptor restriction = actionDescriptor.getRestriction();
        ConditionsDescriptor conditions = null;

        if (restriction != null) {
            conditions = restriction.getConditionsDescriptor();
        }

        return passesConditions(conditions, new HashMap<String,Object>(transientVars), persistentVars, 0);
    }

    protected void checkImplicitFinish(ActionDescriptor action, long piid) throws WorkflowException {
        
        WorkflowStore store = getPersistence();
        ProcessInstance pi = store.findProcessInstance(piid);
        WorkflowDescriptor wf = getOSWfConfiguration().getWorkflow(pi.getWorkflowName());

        Collection<Step> currentSteps = store.findCurrentSteps(piid);

        // Check global actions first
        boolean isCompleted = wf.getGlobalActions().size() == 0;

       for (Step step : currentSteps) {
           
            StepDescriptor stepDes = wf.getStep(step.getStepId());

            // if at least one current step has an available action; We are not done
            if (stepDes.getActions().size() > 0) {
                isCompleted = false;
                break;
            }
        }

        if (isCompleted) {
            completeProcess(action, piid, currentSteps, ProcessInstanceState.COMPLETED);
        }
    }

    /**
     * Mark the specified pi as completed, and move all current steps to history.
     */


    protected void completeProcess(ActionDescriptor action, long id, Collection<Step> currentSteps, ProcessInstanceState state) throws WorkflowStoreException {

        getPersistence().setEntryState(id, state);

        for (Step step : new ArrayList<Step>(currentSteps)) {
            String exitStatus = (action != null) ? action.getUnconditionalResult().getExitStatus() : "Finished";

            getPersistence().moveToHistory(step, (action != null) ? action.getId() : (-1), new Date(), exitStatus, context.getActor());
        }
    }



    protected Step createNewCurrentStep(
                    ResultDescriptor theResult, 
                    ProcessInstance pi, 
                    WorkflowStore store, 
                    int actionId, 
                    Step currentStep, 
                    long[] previousIds, 
                    Map<String,Object> transientVars, 
                    PersistentVars persistentVars) throws WorkflowException {
                        

        try {
            
            int nextStep = theResult.getStep();

            if (nextStep == -1) {
                if (currentStep != null) {
                    nextStep = currentStep.getStepId();
                } else {
                    throw new WorkflowStoreException("Illegal argument: requested new current step same as current step, but current step not specified");
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Outcome: stepId=" + nextStep + ", status=" + theResult.getStatus() + ", owner=" + theResult.getOwner() + ", actionId=" + actionId + ", currentStep=" + ((currentStep != null) ? currentStep.getStepId() : 0));
            }

            if (previousIds == null) {
                previousIds = new long[0];
            }

            String owner = theResult.getOwner();

            VariableResolver variableResolver = getOSWfConfiguration().getVariableResolver();

            if (owner != null) {
                Object o = variableResolver.translateVariables(owner, transientVars, persistentVars);
                owner = (o != null) ? o.toString() : null;
            }

            String exitStatus = theResult.getExitStatus();
            exitStatus = variableResolver.translateVariables(exitStatus, transientVars, persistentVars).toString();

            String status = theResult.getStatus();
            status = variableResolver.translateVariables(status, transientVars, persistentVars).toString();

            if (currentStep != null) {
                store.moveToHistory(currentStep, actionId, new Date(), exitStatus, context.getActor());
                // store.markFinished(currentStep, actionId, new Date(), exitStatus, context.getActor());
                // store.moveToHistory(currentStep);
            }

            // construct the start date and optional due date
            Date startDate = new Date();
            Date dueDate = null;

            if ((theResult.getDueDate() != null) && (theResult.getDueDate().length() > 0)) {
                Object dueDateObject = variableResolver.translateVariables(theResult.getDueDate(), transientVars, persistentVars);

                if (dueDateObject instanceof Date) {
                    dueDate = (Date) dueDateObject;
                } else if (dueDateObject instanceof String) {
                    long offset = 0;

                    try {
                        offset = Long.parseLong((String) dueDateObject);
                    } catch (NumberFormatException e) {
                    }

                    if (offset > 0) {
                        dueDate = new Date(startDate.getTime() + offset);
                    }
                } else if (dueDateObject instanceof Number) {
                    Number num = (Number) dueDateObject;
                    long offset = num.longValue();

                    if (offset > 0) {
                        dueDate = new Date(startDate.getTime() + offset);
                    }
                }
            }

            Step newStep = store.createCurrentStep(pi.getProcessInstanceId(), nextStep, owner, startDate, dueDate, status, previousIds);
            transientVars.put("createdStep", newStep);

            if ((previousIds != null) && (previousIds.length == 0) && (currentStep == null)) {
                
                // At this point, it must be a brand new workflow, so we'll overwrite the empty currentSteps
                // with an array of just this current step
                
                List<Step> currentSteps = new ArrayList<Step>();
                currentSteps.add(newStep);
                transientVars.put("currentSteps", new ArrayList(currentSteps));
            }

            WorkflowDescriptor descriptor = (WorkflowDescriptor) transientVars.get("descriptor");
            StepDescriptor step = descriptor.getStep(nextStep);

            if (step == null) {
                throw new WorkflowException("step id " + nextStep + " does not exist");
            }

            executeFunctions(step.getPreFunctions(), transientVars, persistentVars);

            return newStep;
            
        } catch (WorkflowException e) {
            context.setRollbackOnly();
            throw e;
        }
    }

    /**
     * Executes a function.
     *
     * @param function the function to execute
     * @param transientVars the transientVars given by the end-user
     * @param persistentVars the persistence variables
     */

    protected void executeFunction(FunctionDescriptor function, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {

        if (function != null) {

            String type = function.getType();
            String name = function.getName();

            Map<String,String> args = new HashMap<String,String>(function.getArgs());

            for (Map.Entry<String,String> mapEntry : args.entrySet()) {
                VariableResolver resolver = getOSWfConfiguration().getVariableResolver();
                mapEntry.setValue(resolver.translateVariables(mapEntry.getValue(), transientVars, persistentVars));
            }

            FunctionProvider provider = getTypeResolver().getFunction(type, name, args);

            if (provider == null) {
                String message = "Could not load FunctionProvider class";
                context.setRollbackOnly();
                throw new WorkflowException(message);
            }

            try {
                provider.execute(transientVars, args, persistentVars);
            } catch (WorkflowException e) {
                context.setRollbackOnly();
                throw e;
            }
        }
    }

    protected boolean passesCondition(ConditionDescriptor conditionDesc, Map<String,Object> transientVars, PersistentVars persistentVars, int currentStepId) throws WorkflowException {

        String type = conditionDesc.getType();
        String name = conditionDesc.getName();

        // Arguments come from the Workflow Descriptor via <arg> tag(s)
        Map<String,String> args = new HashMap<String,String>(conditionDesc.getArgs());

        // Transfer the 'arg' strings into the 'transientVars' name/Object map
        //  Apply variable interpolation during the transfer

        VariableResolver resolver = getOSWfConfiguration().getVariableResolver();
        for (Map.Entry<String,String> mapEntry : args.entrySet()) 
            mapEntry.setValue(resolver.translateVariables(mapEntry.getValue(), transientVars, persistentVars));

        if (currentStepId != -1) {
            
            Object stepId = args.get("stepId");

            if ((stepId != null) && stepId.equals("-1")) {
                args.put("stepId", String.valueOf(currentStepId));
            }
        }
        
        // Use TypeResolver to obtain Condition
        Condition condition = getTypeResolver().getCondition(type, name, args);
        
        // 
        if (condition == null) {
            context.setRollbackOnly();
            throw new WorkflowException("Could not load condition");
        }

        try {
            boolean passed = condition.passesCondition(transientVars, args, persistentVars);

            if (conditionDesc.isNegate()) {
                passed = !passed;
            }

            return passed;
        } catch (Exception e) {
            context.setRollbackOnly();

            if (e instanceof WorkflowException) {
                throw (WorkflowException) e;
            }

            throw new WorkflowException("Unknown exception encountered when checking condition " + condition, e);
        }
    }

    protected boolean passesConditions(String conditionType, List conditions, Map<String,Object> transientVars, PersistentVars persistentVars, int currentStepId) throws WorkflowException {

        if ((conditions == null) || (conditions.size() == 0))
            return true;

        boolean and = "AND".equals(conditionType);
        boolean or = !and;
        
        boolean result = false;

        for (AbstractDescriptor descriptor : (List<AbstractDescriptor>)conditions) {

            if (descriptor instanceof ConditionsDescriptor) {
                ConditionsDescriptor conditionsDescriptor = (ConditionsDescriptor) descriptor;
                result = passesConditions(conditionsDescriptor.getType(), conditionsDescriptor.getConditions(), transientVars, persistentVars, currentStepId);
            } else {
                result = passesCondition((ConditionDescriptor) descriptor, transientVars, persistentVars, currentStepId);
            }
            
            // If for any condition we get a 'false' return 'false' for AND tests now.
            // If for any condition we get a 'true' return 'true' for OR tests now.

            if (and && !result) 
                return false;
            else if (or && result)
                return true;
        }
 
        // If we reach here:
        //  For AND, we never encountered a 'false', return 'true'
        //  For OR, we never encountered a 'true', return 'false'
        
        if (and) 
            return true;
        else 
            return false;
    }

    protected boolean passesConditions(ConditionsDescriptor descriptor, Map<String,Object> transientVars, PersistentVars persistentVars, int currentStepId) throws WorkflowException {
        if (descriptor == null) {
            return true;
        }

        return passesConditions(descriptor.getType(), descriptor.getConditions(), transientVars, persistentVars, currentStepId);
    }
    
    /**
     * Provide additional variables by adding them to the 'transientVars' Map
     *
     *  - Add OSWf variable to transientVars
     *  - Add CurrentSteps to transientVars
     *  - Add Register variable name and value to transientVars
     */

    protected void populateTransientMap(ProcessInstance pi, Map<String,Object> transientVars, List<RegisterDescriptor> registers, Integer actionId, Collection<Step> currentSteps, PersistentVars persistentVars) throws WorkflowException {

        transientVars.put("context", context);
        transientVars.put("pi", pi);
        transientVars.put("store", getPersistence());
        transientVars.put("configuration", getOSWfConfiguration());
        transientVars.put("descriptor", getOSWfConfiguration().getWorkflow(pi.getWorkflowName()));

        // Add often used variables here so the workflows doesn't have to add them explicitly
        transientVars.put("actor", context.getActor());

        if (actionId != null) {
            transientVars.put("actionId", actionId);
        }

        transientVars.put("currentSteps", new ArrayList(currentSteps));

        // Now talk to the registers for any extra objects which need to be in scope

        for (RegisterDescriptor register : registers) {
            
            Map<String,String> args = register.getArgs();

            String type = register.getType();
            Register r = getTypeResolver().getRegister(type, args);

            if (r == null) {
                String message = "Could not load register class";
                context.setRollbackOnly();
                throw new WorkflowException(message);
            }

            try {
                transientVars.put(register.getVariableName(), r.registerVariable(context, pi, args, persistentVars));
            } catch (Exception e) {
                context.setRollbackOnly();

                if (e instanceof WorkflowException) {
                    throw (WorkflowException) e;
                }

                throw new WorkflowException("An unknown exception occured while registering variable using register " + r, e);
            }
        }
    }

    /**
     * Transition this process from step to step via the ActionDescriptor
     *
     * @return true if the process instance has been explicitly completed is this transition, 
     *          false otherwise
     *
     * @throws WorkflowException
     */

    protected boolean fireTransition(ProcessInstance pi, 
                                     List<Step> currentSteps, 
                                     WorkflowStore store, 
                                     WorkflowDescriptor wf, 
                                     ActionDescriptor action, 
                                     Map<String,Object> transientVars, 
                                     Map<String,Object> inputs, 
                                     PersistentVars persistentVars) throws WorkflowException {


        // Obtain and ensure we have an empty state cache
        Map cache = (Map)stateCache.get();

        if (cache != null) {
            cache.clear();
        } else {
            stateCache.set(new HashMap());
        }

        // Get the current step for this process
        Step step = getCurrentStep(wf, action.getId(), currentSteps, transientVars, persistentVars);

        if (action.getValidators().size() > 0)
            verifyInputs(pi, action.getValidators(), Collections.unmodifiableMap(transientVars), persistentVars);

        // We're leaving this current step, so execute any post-functions
        //  Null Check: This might be a transition out of an inital-action 
        //  which has no current step

        if (step != null) {
            List stepPostFunctions = wf.getStep(step.getStepId()).getPostFunctions();
            executeFunctions(stepPostFunctions, transientVars, persistentVars);
        }

        // Get any preFunctions for this action and execute them
        executeFunctions(action.getPreFunctions(), transientVars, persistentVars);

        // Check each conditional result to find the FIRST one which passes

        List<ConditionalResultDescriptor> conditionalResults = action.getConditionalResults();
        List extraPreFunctions = null;
        List extraPostFunctions = null;
        ResultDescriptor theResult = null;
        
        for (ConditionalResultDescriptor conditionalResult : conditionalResults) {

            // If this condition passes; get its result, pre and post function and break
            if (passesConditions(null, conditionalResult.getConditions(), Collections.unmodifiableMap(transientVars), persistentVars, (step != null) ? step.getStepId() : (-1))) {
                
                theResult = conditionalResult;
                
                if (conditionalResult.getValidators().size() > 0) 
                    verifyInputs(pi, conditionalResult.getValidators(), Collections.unmodifiableMap(transientVars), persistentVars);

                extraPreFunctions = conditionalResult.getPreFunctions();
                extraPostFunctions = conditionalResult.getPostFunctions();

                break;
            }
        }

        // If we failed to find any 'conditional-result's.  Use the 'default-result' for this action

        if (theResult == null) {
            
            theResult = action.getUnconditionalResult();
            verifyInputs(pi, theResult.getValidators(), Collections.unmodifiableMap(transientVars), persistentVars);
            extraPreFunctions = theResult.getPreFunctions();
            extraPostFunctions = theResult.getPostFunctions();
            
        }
 
        // During debugging log step we are transitioning to and its new status
        if (logger.isDebugEnabled()) 
            logger.debug("Result=" + theResult.getStep() + " status=" + theResult.getStatus());


        // Execute the Results Pre-functions
        executeFunctions(extraPreFunctions, transientVars, persistentVars);

        // Transition to the result Steps (states)

        //  Are we transitioning into a Split?
        
        if ( theResult.getSplit() != 0 ) {

            // Begin Split Logic ------------------------------------------------
                        
            SplitDescriptor splitDesc = wf.getSplit(theResult.getSplit());
            Collection<ResultDescriptor> splitResults = splitDesc.getResults();

            List splitPreFunctions = new ArrayList();
            List splitPostFunctions = new ArrayList();

            // Check all results in the split and verify the input against any validators specified
            //    Also build up all the Pre and Post functions that should be invoked
            
            for (ResultDescriptor resultDescriptor : splitResults) {
                if (resultDescriptor.getValidators().size() > 0) {
                    verifyInputs(pi, resultDescriptor.getValidators(), Collections.unmodifiableMap(transientVars), persistentVars);
                }

                splitPreFunctions.addAll(resultDescriptor.getPreFunctions());
                splitPostFunctions.addAll(resultDescriptor.getPostFunctions());
            }

            // Execute the Split Pre-functions
            executeFunctions(splitPreFunctions, transientVars, persistentVars);
            
            // Move the Current Step into the History Steps and create new Current Steps
            //  for each Split result
            
            if (action.isFinish() == false) {
                                
                boolean moveFirst = true;

                // theResults = new ResultDescriptor[results.size()];
                // results.toArray(theResults);
                
                for (ResultDescriptor resultDescriptor : splitResults) {
                    
                    Step moveToHistoryStep = null;

                    if (moveFirst)
                        moveToHistoryStep = step;

                    long[] previousIds = null;

                    if (step != null) {
                        previousIds = new long[] {step.getId()};
                    }

                    createNewCurrentStep(resultDescriptor, pi, store, action.getId(), moveToHistoryStep, previousIds, transientVars, persistentVars);
                    
                    moveFirst = false;
                }
            }

            // Execute the Splits post-functions
            executeFunctions(splitPostFunctions, transientVars, persistentVars);

            // End Split Logic ------------------------------------------------

        //  Are we transitioning into a Join? 'getJoin' returns a non-zero id.
        
        } else if ( theResult.getJoin() != 0 ) {

            JoinSteps joinSteps = new JoinSteps();

            // Begin Join Logic -----------------------------------------------

            JoinDescriptor joinDesc = wf.getJoin(theResult.getJoin());
            
            store.moveToHistory(step, action.getId(), new Date(), theResult.getExitStatus(), context.getActor());

            // Add this step; JoinSteps behaves as a Set, if a step exists it will not be overwritten
            joinSteps.add(step);

            //  Add any other Current Steps which transition into this Join 
                                 
            for (Step currentStep : currentSteps) {
                StepDescriptor stepDesc = wf.getStep(currentStep.getStepId());
                if ( stepDesc.resultsInJoin(theResult.getJoin()) ) 
                    joinSteps.add(currentStep);
            }

            // Create a Collection of Steps associated with this Join so that they can
            //  be passed via the 'transientVars' for evaluation 
            // Add any History Steps which are waiting at the join 
            
            List<Step> historySteps = store.findHistorySteps(pi.getProcessInstanceId());
            
            for (Step historyStep : historySteps) {
                StepDescriptor stepDesc = wf.getStep(historyStep.getStepId());
                if ( stepDesc.resultsInJoin(theResult.getJoin()) ) 
                    joinSteps.add(historyStep);
            }

            // Add those Steps which are or were part of this Join to the transientVars
            //  for possible evaluation in a script
            
            transientVars.put("joinSteps", joinSteps);

            // TODO: Verify that 0 is the right value for currentstep here

            if ( passesConditions(null, joinDesc.getConditions(), Collections.unmodifiableMap(transientVars), persistentVars, step.getStepId())) {

                // (?) Move the rest without creating a new step ...
                ResultDescriptor joinResult = joinDesc.getResult();

                if ( joinResult.getValidators().size() > 0 ) 
                    verifyInputs(pi, joinResult.getValidators(), Collections.unmodifiableMap(transientVars), persistentVars);

                // Execute the Pre-functions
                executeFunctions(joinResult.getPreFunctions(), transientVars, persistentVars);

                long[] previousIds = new long[joinSteps.size()];
                int i = 1;
                
                // If pass this Join's condition change any pending Current Steps into
                //   History Steps; They will not have an actor, exit status or end date
                //   They essentially go 'poof' i.e. removed from the Task List
                
                for (Step currentStep : joinSteps) {
                    
                    if (currentStep.getStepId() != step.getStepId()) {
                        
                        // If this is already a history step (ie for all join steps completed prior to this one),
                        // We don't move it, since it's already history.
                        
                        if (!historySteps.contains(currentStep))
                            store.moveToHistory(currentStep, 0, null, null, null);

                        previousIds[i] = currentStep.getId();
                        i++;
                    }
                }

                if ( action.isFinish() == false ) {
                    
                    // ... now finish this step normally
                    
                    previousIds[0] = step.getId();
                    theResult = joinDesc.getResult();

                    // We pass in null for the current step since we've already moved it to history above
                    
                    createNewCurrentStep(joinDesc.getResult(), pi, store, action.getId(), null, previousIds, transientVars, persistentVars);
                }

                // Execute the Post-functions
                executeFunctions(joinResult.getPostFunctions(), transientVars, persistentVars);
            }

            // End Join Logic -----------------------------------------------

        } else {
            
            // Step-Action-Step transition, no splits or joins, Pre functions have been executed
            //   Move single Current Step to the History Steps and create a new Current Step
            
            long[] previousIds = null;

            if (step != null) 
                previousIds = new long[] {step.getId()};
 
            if (action.isFinish() == false) 
                createNewCurrentStep(theResult, pi, store, action.getId(), step, previousIds, transientVars, persistentVars);
        }



        // Post-Functions which were defined by the Step
        executeFunctions(extraPostFunctions, transientVars, persistentVars);

        // Post-Functions which were defined by the Action
        executeFunctions(action.getPostFunctions(), transientVars, persistentVars);

        // If executed action was an initial action then workflow is now activated

        if ((wf.getInitialAction(action.getId()) != null) && (pi.getState() != ProcessInstanceState.ACTIVE)) 
            changeEntryState(pi.getProcessInstanceId(), ProcessInstanceState.ACTIVE);

        // If it's a finish action, then we set the process instance state to COMPLETED and
        //   return true per the API
        
        if (action.isFinish()) {
            completeProcess(action, pi.getProcessInstanceId(), getCurrentSteps(pi.getProcessInstanceId()), ProcessInstanceState.COMPLETED);
            return true;
        }

        // Get any available auto actions
        
        List<Integer> availableAutoActions = getAvailableAutoActions(pi.getProcessInstanceId(), inputs);

        // We perform the first auto action that applies, not all of them
        //    (?) Will this recurse?
        
        if (availableAutoActions.size() > 0) 
            doAction(pi.getProcessInstanceId(), availableAutoActions.get(0), inputs);
        
        // Return 'false' meaning that the process instance is not yet complete
        
        return false;
    }

    protected void executeFunctions(List<FunctionDescriptor> functions, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {
        
        if(functions == null)
            return;

        for (FunctionDescriptor function : functions)
            executeFunction(function, transientVars, persistentVars);
    }



    /**
     * Validates input against a list of ValidatorDescriptor objects.
     *
     * @param pi the workflow instance
     * @param validators the list of ValidatorDescriptors
     * @param transientVars the transientVars
     * @param persistentVars the persistence variables
     * @throws InvalidInputException if the input is deemed invalid by any validator
     */
    protected void verifyInputs(ProcessInstance pi, List<ValidatorDescriptor> validators, Map<String,Object> transientVars, PersistentVars persistentVars) throws WorkflowException {


        for(ValidatorDescriptor input : validators) {
            
            if (input != null) {
                
                String type = input.getType();
                
                HashMap<String,String> args = new HashMap<String,String>(input.getArgs());

                for (Map.Entry<String,String> mapEntry : args.entrySet()) {
                    VariableResolver resolver = getOSWfConfiguration().getVariableResolver();
                    mapEntry.setValue(resolver.translateVariables(mapEntry.getValue(), transientVars, persistentVars));
                }

                Validator validator = getTypeResolver().getValidator(type, args);

                if (validator == null) {
                    String message = "Could not load validator class";
                    context.setRollbackOnly();
                    throw new WorkflowException(message);
                }

                try {
                    validator.validate(transientVars, args, persistentVars);
                } catch (InvalidInputException e) {
                    throw e;
                } catch (Exception e) {
                    context.setRollbackOnly();

                    if (e instanceof WorkflowException) {
                        throw (WorkflowException) e;
                    }

                    String message = "An unknown exception occured executing Validator " + validator;
                    throw new WorkflowException(message, e);
                }
            }
        }
    }

    private WorkflowDescriptor getWorkflowDescriptorForAction(ActionDescriptor action) {
        AbstractDescriptor objWfd = action;

        while (!(objWfd instanceof WorkflowDescriptor)) {
            objWfd = objWfd.getParent();
        }

        WorkflowDescriptor wf = (WorkflowDescriptor) objWfd;

        return wf;
    }
}
