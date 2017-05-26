package org.informagen.oswf;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.PropertySetStore;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.exceptions.WorkflowStoreException;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import java.util.*;


/**
 * Interface for pluggable workflow stores configured in oswf.xml.
 * Only one instance of a workflow store is ever created, meaning that
 * if your persistence connections (such as java.sql.Connection) time out,
 * it would be un-wise to use just one Connection for the entire object.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public interface WorkflowStore {

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     *  DOCUMENT THIS
     */
    void setPropertySetStore(PropertySetStore propertySetStore);

    /**
     *  DOCUMENT THIS
     */
    PropertySetStore getPropertySetStore();


    /**
     * Set the state of the workflow instance.
     * @param entryId The workflow instance id.
     * @param state The state to move the workflow instance to.
     */
    public void setEntryState(long piid, ProcessInstanceState state) throws WorkflowStoreException;

    /**
     * Returns a PropertySet that is associated with this process instance ID.
     * @param entryId The workflow instance id.
     * @return a property set unique to this process instance (piid)
     */
    public PropertySet getPropertySet(long piid);

    /**
     * Persists a step with the given parameters.
     *
     * @param entryId The process instance id.
     * @param stepId the ID of the workflow step associated with this new
     *               Step (not to be confused with the step primary key)
     * @param owner the owner of the step
     * @param startDate the start date of the step
     * @param status the status of the step
     * @param previousIds the previous step IDs
     * @return a representation of the workflow step persisted
     */
    public Step createCurrentStep(long piid, int stepId, String owner, Date startDate, Date dueDate, String status, long[] previousIds) throws WorkflowStoreException;

    /**
     * Persists a new workflow entry that has <b>not been initialized</b>.
     *
     * @param workflowName the workflow name that this process is an instance of
     * @return a representation of the process instance persisted
     */
    public ProcessInstance createEntry(String workflowName) throws WorkflowStoreException;

    /**
     * Pulls up the workflow entry data for the entry ID given.
     *
     * @param piid The process instance id.
     * @return a representation of the process instance persisted
     */
    public ProcessInstance findProcessInstance(long piid) throws WorkflowStoreException;

    /**
     * Returns a list of all current steps for the given workflow instance ID.
     *
     * @param entryId The workflow instance id.
     * @return a List of Steps
     * @see org.informagen.oswf.impl.Step
     */
    public List<Step> findCurrentSteps(long piid) throws WorkflowStoreException;

    /**
     * Returns a list of all steps that are finished for the given workflow instance ID.
     *
     * @param entryId The process instance id
     * @return a List of Steps
     * @see org.informagen.oswf.Step
     */
    public List<Step> findHistorySteps(long piid) throws WorkflowStoreException;

    /**
     * Mark the specified step as finished.
     * @param step the step to finish.
     * @param actionId The action that caused the step to finish
     * @param finishDate the date when the step was finished; null if the step was cleared
     * @param status The status to set the finished step to
     * @param actor The actor that caused the step to finish
     * @return the finished step
     */

    public void moveToHistory(Step step, int actionId, Date finishDate, String status, String actor) throws WorkflowStoreException;

    /**
     * @param query the query to use
     * @return a List of process instance ID, piid
     */
    public List<Long> query(WorkflowExpressionQuery query) throws WorkflowStoreException;

}



