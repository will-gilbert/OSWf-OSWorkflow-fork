package org.informagen.oswf.impl;

import org.informagen.oswf.Step;

import java.io.Serializable;

import java.util.Date;


/**
 * Default implementation of the Step interface. Is used for both current and
 *    history steps
 *
 */
public class DefaultStep implements Step, Serializable {

    // I N S T A N C E   F I E L D S  ---------------------------------------------------------
    
    protected Long id = null;
    protected Long processInstanceId;
    protected int stepId;
    protected int actionId;

    protected Date startDate;
    protected String owner;
    protected String status;
    protected Date dueDate;
    
    protected String actor;
    protected Date finishDate;

    // Are these descriptor step ids or process instance step ids? I think pi Step Ids
    protected long[] previousIds;
    

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public DefaultStep() {}

    public DefaultStep(Long id, Long processInstanceId, int stepId, int actionId, String owner, Date startDate, Date dueDate, Date finishDate, String status, long[] previousIds, String actor) {
        this.id = id;
        this.processInstanceId = processInstanceId;
        this.stepId = stepId;
        this.actionId = actionId;
        this.owner = owner;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.dueDate = dueDate;
        this.status = status;
        this.previousIds = previousIds;
        this.actor = actor;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    // ID for the executed step within a process instance
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // The process instance ID (piid)
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }


    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getActionId() {
        return actionId;
    }

    // The step ID within the Workflow Description 
    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getStepId() {
        return stepId;
    }


    // The intended actor; could be a user, group or role
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    // The date the step was created; i.e. after an initial-action or action has been 
    //   executed
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    // Not often used but can could used to schedule and maintain service level agreements
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    // The status of the step; both when it is a current step and a history step
    //   i.e. its 'exit-status'
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // The actual 'actor' who completed the step; could be user or system or automatic
    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActor() {
        return actor;
    }

    // The date-time when a current step becomes a history step
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    // This is a non-presisted data used
    public void setPreviousIds(long[] previousIds) {
        this.previousIds = previousIds;
    }

    public long[] getPreviousIds() {
        return previousIds;
    }
 
    // Used to create sets of process instance steps; current and history
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

  
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (Step.class.isAssignableFrom(o.getClass()) == false) {
            return false;
        }

        final Step other = (Step) o;
        if ((this.id == null) ? (other.getId() != null) : !this.id.equals(other.getId())) {
            return false;
        }

        return true;        

    }  

    public String toString() {
        return new StringBuffer()
            .append("id=").append(getId()).append(", ")
            // .append("piid=").append(pi.getProcessInstanceId()).append(", ")
            // .append("workflow=").append(pi.getWorkflowName()).append(", ")
            // .append("state=").append(pi.getState().getName()).append(", ")
            .append("stepId=").append(getStepId()).append(", ")
            .append("status=").append(getStatus()).append(", ")
            .append("actionId=").append(getActionId()).append(", ")
            .append("actor=").append(getActor()).append(", ")
            .append("owner=").append(getOwner())
        .toString();
    }

}
