package org.informagen.oswf.simulator.dto;

import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;

import com.google.gwt.user.client.rpc.IsSerializable;

// Java - Collections
import java.util.List;
import java.util.ArrayList;


public class ProcessInstanceState implements IsSerializable {

    public long piid;

    public List<Step> currentSteps = new ArrayList<Step>();
    public List<Step> historySteps = new ArrayList<Step>();
    public List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();
    
    public ProcessInstanceState() {}
    
    public void addCurrentStep(Step currentStep) {
        currentSteps.add(currentStep);
    }
        
    public void addHistoryStep(Step historyStep) {
        historySteps.add(historyStep);
    }
        
    public void addProcessVariable(ProcessVariable processVariable) {
        processVariables.add(processVariable);
    }
    
}
