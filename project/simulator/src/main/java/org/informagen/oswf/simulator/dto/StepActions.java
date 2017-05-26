package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import org.informagen.oswf.simulator.dto.Action;

import java.lang.Comparable;

import java.util.ArrayList;
import java.util.List;

public class StepActions implements IsSerializable, Comparable<StepActions> {

    private int stepId;
    private String stepName;
    private List<Action> actions = new ArrayList<Action>();

    public StepActions() {}

    public StepActions(int stepId, String stepName) {
        this.stepId = stepId;
        this.stepName = stepName;
    }
    
    public int getStepId() { return stepId; }
    public String getStepName() { return stepName; }
    public List<Action> getActions() { return actions; }

    public void addAction(Action action) {
        actions.add(action);
    }
 
    public int compareTo(StepActions object) {
       return stepName.compareTo(object.getStepName());
    }
   
}
