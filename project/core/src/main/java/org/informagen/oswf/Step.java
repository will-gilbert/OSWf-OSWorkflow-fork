package org.informagen.oswf;

import java.util.Date;


/**
 * Interface for both current steps and history steps step associated with 
 *    a workflow instance.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public interface Step {

    /**
     * Returns the unique presistent store ID of this step. If applicable.
     *  The ID get sets by the class which implements the interface.
     */
    public Long getId();

    /**
     * Returns the workflow process instance ID (piid).
     */
    public Long getProcessInstanceId();

    /**
     * Current Step
     * Returns the ID of the step in the workflow definition.
     */
    public int getStepId();

    /**
     * Returns the status of this step
     */
    public String getStatus();
    public void setStatus(String status);
    
    /**
     * Returns the owner of this step, or null if there is no owner.
     */
    public String getOwner();
    public void setOwner(String owner);

    /**
     * Returns the date that this step was created.
     */
    public Date getStartDate();
    public void setStartDate(Date startDate);


    /**
     * Returns an optional date signifying when this step must be finished
     */
    public Date getDueDate();
    public void setDueDate(Date dueDate);


    // History Step only below ------------------

    /**
     * Returns the ID of the action associated with this step,
     *      or 0 if there is no action associated.
     */
    public int getActionId();
    public void setActionId(int actionId);

    public String getActor();
    public void setActor(String actor);


    /**
     * Returns the date this step was finished, or null if it isn't finished
     */
    public Date getFinishDate();
    public void setFinishDate(Date finishDate);


    /**
     * Returns the unique ID of the previous step, or 0 if this is the first step
     */
    public long[] getPreviousStepIds();

    

}
