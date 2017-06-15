package org.informagen.oswf.impl.stores;

// OSWf - Core
import org.informagen.oswf.Step;
import org.informagen.oswf.impl.DefaultStep;

// Java Collections
import java.util.List;


/**
 * This class is abstract because the current and historical steps are stored 
 *  in seperate tables. To split the history and current steps into two tables 
 *  in hibernate, the easiest approach is to use two separate classes.
 */
 
public abstract class HibernateStep extends DefaultStep {

    //~ Instance fields ////////////////////////////////////////////////////////

    protected List<Step> previousSteps;
    protected HibernateProcessInstance entry;

    //~ Constructors ///////////////////////////////////////////////////////////

    public HibernateStep() {}


    /**
     * Clone a HibernateStep from another HibernateStep; This operation is
     *  used to create a HistoryStep from a CurrrentStep
     *  Do not copy the 'id'; It's used to indicate unsaved instances
     *  @param step Hibernate step to be cloned
     */

    public HibernateStep(HibernateStep step) {
        
        setActionId(step.getActionId());
        setActor(step.getActor());
        setFinishDate(step.getFinishDate());
        setDueDate(step.getDueDate());
        setStartDate(step.getStartDate());

        setOwner(step.getOwner());
        setStatus(step.getStatus());
        setStepId(step.getStepId());
        setEntry(step.getEntry());
 
        this.previousSteps = step.getPreviousSteps();

    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEntry(HibernateProcessInstance entry) {
        this.entry = entry;
    }

    public HibernateProcessInstance getEntry() {
        return entry;
    }

    public long getEntryId() {
        return entry.getProcessInstanceId();
    }


    public long[] getPreviousIds() {
        
        if (previousSteps == null) {
            return new long[0];
        }

        long[] previousIds = new long[previousSteps.size()];
        int i = 0;

        for(Step step :  previousSteps) {
            previousIds[i] = step.getId();
            i++;
        }

        return previousIds;
    }


    public void setPreviousSteps(List<Step> previousSteps) {
        this.previousSteps = previousSteps;
    }

    public List<Step> getPreviousSteps() {
        return previousSteps;
    }


}
