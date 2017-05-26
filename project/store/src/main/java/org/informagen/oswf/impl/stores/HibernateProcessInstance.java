package org.informagen.oswf.impl.stores;

import org.informagen.oswf.Step;
import org.informagen.oswf.ProcessInstanceState;

import org.informagen.oswf.impl.DefaultProcessInstance;


import java.util.ArrayList;
import java.util.List;


/**
 * @author Hani
 * @author Will Gilbert
 */

public class HibernateProcessInstance extends DefaultProcessInstance {

    //~ Instance fields ////////////////////////////////////////////////////////

    List<Step> currentSteps = new ArrayList<Step>();
    List<Step> historySteps = new ArrayList<Step>();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setStateId(int state) {
        super.setState(ProcessInstanceState.getProcessInstanceState(state));
    }

    public int getStateId() {
        return getState().getValue();
    }

    public void setCurrentSteps(List<Step> currentSteps) {
        this.currentSteps = currentSteps;
    }

    public List<Step> getCurrentSteps() {
        return currentSteps;
    }

    public void setHistorySteps(List<Step> historySteps) {
        this.historySteps = historySteps;
    }

    public List<Step> getHistorySteps() {
        return historySteps;
    }

    public void addCurrentStep(HibernateStep step) {
        step.setEntry(this);
        getCurrentSteps().add(step);
    }

    public void addHistoryStep(HibernateStep step) {
        step.setEntry(this);
        getHistorySteps().add(step);
    }

    public void removeCurrentStep(HibernateStep step) {
        getCurrentSteps().remove(step);
    }

    public String toString() {
        return new StringBuffer()
            .append("piid=").append(getProcessInstanceId()).append(", ")
             .append("workflowName=").append(getWorkflowName()).append(", ")
             .append("state=").append(getState().getName())
             .toString();
    }

}
