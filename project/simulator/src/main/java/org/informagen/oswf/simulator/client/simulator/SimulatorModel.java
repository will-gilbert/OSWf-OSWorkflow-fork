package org.informagen.oswf.simulator.client.simulator;


// Application
import org.informagen.oswf.simulator.client.application.Callback;
import  org.informagen.oswf.simulator.client.application.ApplicationModel;
// Presenter
import org.informagen.oswf.simulator.client.simulator.SimulatorPresenter;
import org.informagen.oswf.simulator.client.simulator.inputs.InputsPresenter;
import org.informagen.oswf.simulator.client.simulator.ActorPresenter;

// DTO
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;

// RPC Services
import org.informagen.oswf.simulator.rpc.OSWfServiceAsync;

// GWT ---
import com.google.gwt.view.client.ListDataProvider;


// Java - Collections
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import static java.util.Collections.EMPTY_LIST;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// Java Collections
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;

public class SimulatorModel implements SimulatorPresenter.Model, 
                                       InputsPresenter.Model,
                                       ActorPresenter.Model {

    // RPC services
    private final OSWfServiceAsync service;

    private final Map<String,Long> currentPIIDMap;
    private final Map<String,String> inputs;

    private String currentWorkflowName;
    private String actor = "Simulator";

    @Inject
    public SimulatorModel(OSWfServiceAsync service) {
        this.service = service;

        currentPIIDMap = new HashMap<String,Long>();
        inputs = new HashMap<String,String>(); 
    }

   public void setInputs(Map<String,String> inputs) {
       this.inputs.clear();
       if(inputs !=null)
           this.inputs.putAll(inputs);
   }

   public void setActor(String actor) {
       this.actor = actor;
   }

    public void setCurrentPIID(long piid) {
        currentPIIDMap.put(currentWorkflowName, piid);
    }

    public Long getCurrentPIID() {
        return currentPIIDMap.get(currentWorkflowName);
    }

    public void switchToWorkflow(String workflowName, final Callback<Long> callback) {
 
        if(workflowName == null)
            return;
   
        this.currentWorkflowName = workflowName;        
        
        service.fetchProcessInstances(currentWorkflowName, new Callback<List<ProcessInstance>>() {
            public void onSuccess(List<ProcessInstance> list) {
                // processInstanceProvider.setList(list);
                // processInstanceProvider.refresh();                
                callback.onSuccess(getCurrentPIID());
            }
        });
    }

    public void fetchProcessInstances(Callback<List<ProcessInstance>> callback) {
        service.fetchProcessInstances(currentWorkflowName, callback);
    }

    /**
     * Fetch the initial actions for the current workflow
     */
    public void fetchInitialActions(final Callback<List<Action>> callback) {
        service.fetchInitialActions(currentWorkflowName, new Callback<List<Action>>() {
            public void onSuccess(List<Action> list) {
                callback.onSuccess(list);
            }
        });
    }

 
    /**
     * Fetch the state of the currently focused PIID
     */
    public void fetchProcessInstance(Callback<ProcessInstanceState> callback) {
        service.fetchProcessInstanceState(getCurrentPIID(), callback);
    }

    public void fetchWorkflowOverview(final Callback<List<Overview>> callback) {

        if(currentWorkflowName == null)
            return;
      
        service.fetchWorkflowOverview(currentWorkflowName,  new Callback<List<Overview>>() {
        
            public void onSuccess(List<Overview> list) {
            
                // Convert milliseconds to human readable displays based on magnitude
                for(Overview overview : list) {
                    overview.maxPendingDisplay = toDisplay(overview.maxPending);
                    overview.avgPendingDisplay = toDisplay(overview.avgPending);
                }

                callback.onSuccess(list);
            }
            
        });
        
    }

    /**
     *  Fetch the current step actions for currently focused Process Instance
     */
    public void fetchStepActions(Callback<List<StepActions>>callback) {
        service.fetchStepActions(getCurrentPIID(), callback);
    }


    /**
     *  Using an 'initial-action' ID start a process and return the created
     *    Process Instance ID (piid)
     */
    public void startProcess(int initialActionId, final Callback<Long>callback) {
        service.startProcess(currentWorkflowName, actor, initialActionId, inputs, 
            new Callback<Long>() {
                public void onSuccess(Long piid) {
                    setCurrentPIID(piid);
                    callback.onSuccess(piid);
                }
            }
        );
    }

    public void doAction(int actionId, Callback<Void>callback) {
        service.doAction(currentWorkflowName,  getCurrentPIID(), actor, actionId, inputs, callback);
    }


    // Private --------------------------------------------------------------------------------
    
    
    private String toDisplay(long milliseconds) {
    
        if(milliseconds < 1000L)                                          // milliseconds
            return milliseconds + "ms";
        else if (milliseconds >= 1000L && milliseconds < 60000L)          // seconds
            return milliseconds/1000L + "s";
        else if (milliseconds >= 60000L && milliseconds < 3600000L )      // minutes
            return milliseconds/60000L + "m";
        else if (milliseconds >= 3600000L && milliseconds < 86400000L )   // hours
            return milliseconds/3600000L + "h";
        else if (milliseconds >= 86400000L && milliseconds < 604800000L ) // days
            return milliseconds/86400000L + "d";
        else
            return milliseconds/604800000L + "w";                         // weeks

    }

}
