package org.informagen.oswf;

import org.informagen.oswf.ProcessInstanceState;

 
// OpenSymphony PropertySet
import org.informagen.oswf.propertyset.PropertySet;


// OSWf Core
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;
import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.InvalidEntryStateException;
import org.informagen.oswf.exceptions.InvalidActionException;
import org.informagen.oswf.exceptions.InvalidActionException;
import org.informagen.oswf.exceptions.InvalidInputException;

// Java Collections
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The workflow engine interface.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 */
 
public interface OSWfEngine {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    String BSH_SCRIPT   = "script";
    String JNDI_LOCATION = "jndi.location";
   

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Returns a Collection of Step objects that are the current steps of the specified workflow instance.
     *
     * @param id The workflow instance id.
     * @return The steps that the workflow instance is currently in.
     */
    List<Step> getCurrentSteps(long piid);

    /**
     * Return the state of the specified workflow instance id.
     * @param id The workflow instance id.
     * @return ProcessInstanceState The state id of the specified workflow
     */
    ProcessInstanceState getProcessInstanceState(long piid);

    /**
     * Returns a list of all steps that are completed for the given workflow instance id.
     *
     * @param id The workflow instance id.
     * @return a List of Steps
     * @see org.informagen.oswf.impl.Step
     */
    List<Step> getHistorySteps(long piid);

    /**
     * Get the PropertySet for the specified workflow instance id.
     * @param id The workflow instance id.
     */
    PropertySet getPropertySet(long piid);

    /**
     * Get a collection (Strings) of currently defined stepConditions for the specified workflow instance.
     * @param id id the workflow instance id.
     * @param inputs inputs The inputs to the workflow instance.
     * @return A Map with a of stepConditions for each current Step
     */
    Map<Integer,Set<StepCondition>> getStepConditions(long piid);
    Map<Integer,Set<StepCondition>> getStepConditions(long piid, Map<String,Object> inputs);

    /**
     * Get the workflow descriptor for the specified workflow name.
     * @param workflowName The workflow name.
     */
    WorkflowDescriptor getWorkflowDescriptor(String workflowName);

    /**
     * Get the name of the specified workflow instance.
     * @param id the workflow instance id.
     */
    String getWorkflowName(long id);

    /**
    * Check if the state of the specified workflow instance can be changed to the new specified one.
    * @param id The workflow instance id.
    * @param newState The new state id.
    * @return true if the state of the workflow can be modified, false otherwise.
    */
    boolean canSwitchToProcessInstanceState(long id, ProcessInstanceState newState);

    /**
     * Modify the state of the specified workflow instance.
     * @param id The workflow instance id.
     * @param newState the new state to change the workflow instance to.
     * If the new state is KILLED or COMPLETED
     * then all current steps are moved to history steps. If the new state is
     */
    void changeEntryState(long id, ProcessInstanceState newState) throws WorkflowException;

    /**
     * Perform an action on the specified workflow instance.
     * @param id The workflow instance id.
     * @param actionId The action id to perform (action id's are listed in the workflow descriptor).
     * @param inputs The inputs to the workflow instance.
     * @throws InvalidInputException if a validator is specified and an input is invalid.
     * @throws InvalidActionException if the action is invalid for the specified workflow
     * instance's current state.
     */
    OSWfEngine doAction(long id, int actionId) throws InvalidInputException, WorkflowException;
    OSWfEngine doAction(long id, int actionId, Map<String,Object> inputs) throws InvalidInputException, WorkflowException;

    /**
     * Executes a special trigger-function using the context of the given workflow instance id.
     * Note that this method is exposed for Quartz trigger jobs, user code should never call it.
     * @param id The workflow instance id
     * @param triggerId The id of the speciail trigger-function
     */
    void executeTriggerFunction(long id, int triggerId) throws WorkflowException;

    /**
    * Initializes a workflow so that it can begin processing. A workflow must be initialized before it can
    * begin any sort of activity. It can only be initialized once.
    *
    * @param workflowName The workflow name to create and initialize an instance for
    * @param initialAction The initial step to start the workflow
    * @param inputs The inputs entered by the end-user
    * @throws InvalidActionException if the user can't start this function
    * @throws InvalidInputException if a validator is specified and an input is invalid.
    * @throws InvalidActionException if the specified initial action is invalid for the specified workflow.
    */

    long initialize(String workflowName, int initialAction) throws InvalidActionException, InvalidInputException, WorkflowException, InvalidEntryStateException, InvalidActionException;
    long initialize(String workflowName, int initialAction, Map<String,Object> inputs) throws InvalidActionException, InvalidInputException, WorkflowException, InvalidEntryStateException, InvalidActionException;

    /**
     * Query the workflow store for matching instances
     */
    List<Long> query(WorkflowExpressionQuery query) throws WorkflowException;

    /**
     * Get the available actions for the specified workflow instance.
     * @param id The workflow instance id.
     * @param inputs The inputs map to pass on to conditions
     * @return An array of action id's that can be performed on the specified entry
     * @throws IllegalArgumentException if the specified id does not exist, or if its workflow
     * descriptor is no longer available or has become invalid.
     */
    List<Integer> getAvailableActions(long id);
    List<Integer> getAvailableActions(long id, Map<String,Object> inputs);

    /**
     * Set the configuration for this workflow.
     * If not set, then the workflow will use the default configuration static instance.
     * @param configuration a workflow configuration
     */
    OSWfEngine setConfiguration(OSWfConfiguration configuration);

    /**
     * Get all available workflow names.
     */
    Set<String> getWorkflowNames();


    /**
     * Check if the calling user has enough stepConditions to initialise the specified workflow.
     * @param workflowName The name of the workflow to check.
     * @param initialStep The id of the initial state to check.
     * @return true if the user can successfully call initialize, false otherwise.
     */
    boolean canInitialize(String workflowName, int initialStep);

    /**
     * Determine if a particular workflow can be initialized.
     * @param workflowName The workflow name to check.
     * @param initialAction The potential initial action.
     * @param inputs The inputs to check.
     * @return true if the workflow can be initialized, false otherwise.
     */
    boolean canInitialize(String workflowName, int initialAction, Map<String,Object> inputs);

}
