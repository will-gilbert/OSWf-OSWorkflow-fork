package org.informagen.oswf.simulator.client.simulator;

// Application
import org.informagen.oswf.simulator.client.application.Callback;
import org.informagen.oswf.simulator.client.application.SimpleCallback;
import org.informagen.oswf.simulator.client.application.ContentPresenter;
import org.informagen.oswf.simulator.client.application.Constants;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent;
import org.informagen.oswf.simulator.client.events.SwitchToPresenterEvent;
import org.informagen.oswf.simulator.client.events.DisplayInputsDialogEvent;
import org.informagen.oswf.simulator.client.events.DisplayActorDialogEvent;

// DTO
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;

//-----------------------------------------------------------------

// GWT - Core, Widgets, Timer
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Timer;


// GWT - Events
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.ListDataProvider;

// SmartGWT - ListGrid and ListGrid events
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HasRecordClickHandlers;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;

// SmartGWT - Events
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// SmartGWT - Form Events
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.HasChangedHandlers;

// SmartGWT - Utilities
import com.smartgwt.client.util.SC;

// Java - Collections
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;


public class SimulatorPresenter implements ContentPresenter, Constants {

    // MVP Interfaces =========================================================================

    public interface View {

        void setCurrentPIID(long piid);

        void setProcessInstancesData(ListGridRecord[] data);
        void setCurrentStepsData(ListGridRecord[] data);
        void setHistoryStepsData(ListGridRecord[] data);
        void setProcessVariablesData(ListGridRecord[] data);
        void setWorkflowOverviewData(ListGridRecord[] data);
        
        HasRecordClickHandlers getProcessInstanceSelector();
        Long getSelectedProcessInstance();
        
        void selectProcessInstance(Long piid);
        Widget asWidget();
    }

    public interface Controls {
        HasChangedHandlers actorSelector();
        HasClickHandlers getInputsClickable();
        void clearInitialActions();
        void clearStepActions();
        
        void installInitialActions(List<Action> actions, SimpleCallback<Integer> callback);
        void installStepActions(List<StepActions> actions, SimpleCallback<Integer> callback);

        String getActor();

        Widget asWidget();       
    }

    public interface Model {
             
        void setActor(String actor);
        void setCurrentPIID(long piid);
        Long getCurrentPIID();
        void switchToWorkflow(String currentWorkflowName, Callback<Long> callback);
        void fetchProcessInstance(Callback<ProcessInstanceState> callback);

        void startProcess(int initialActionId, Callback<Long>callback);
        void fetchInitialActions(final Callback<List<Action>> callback);
        void fetchStepActions(Callback<List<StepActions>>callback);
        void doAction(int actionId, Callback<Void>callback);

        void fetchProcessInstances(Callback<List<ProcessInstance>> callback);
        void fetchWorkflowOverview(final Callback<List<Overview>> callback);
    }
        
    //=========================================================================================

    private final View view;
    private final Model model;
    private final Controls controls;
    private final EventBus eventBus;

    private Timer timer = null;
        
    @Inject
    public SimulatorPresenter(View view, Model model, Controls controls, EventBus eventBus) {
        
        this.view = view;
        this.model = model;
        this.controls = controls;
        this.eventBus = eventBus;

        bindViewHandlers();       
        bindEvents();
        bindControlHandlers();

    }

    // ContentPresenter interface -------------------------------------------------------------

    public Widget getView() { 
        fetchProcessInstances();
        return view.asWidget(); 
    }
    
    public Widget getControlsWidget() { 
        return controls.asWidget(); 
    }
    
    // ----------------------------------------------------------------------------------------

    private void bindViewHandlers() {
        
        view.getProcessInstanceSelector().addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent clickEvent) {
                switchToPIID(view.getSelectedProcessInstance());
            }  
        });  
        
    }

    private void bindEvents() {
        
        eventBus.addHandler(SwitchToPresenterEvent.TYPE,
            new HandlerFor.SwitchToPresenterEvent() {
                public void processEvent(SwitchToPresenterEvent event) {
                    if(SIMULATOR_SECTION_ID.equals(event.sectionId)) {
                        timer = createTimer(1);
                    } else if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        );

        eventBus.addHandler(WorkflowSelectionChangedEvent.TYPE,
            new HandlerFor.WorkflowSelectionChangedEvent() {
                public void processEvent(WorkflowSelectionChangedEvent event) {
                   switchToWorkflow(event.getWorkflowName());
                }
            }
        );
    }

    private void bindControlHandlers() {

        controls.getInputsClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new DisplayInputsDialogEvent());
            }
        });

        controls.actorSelector().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent changedEvent) {
               model.setActor(controls.getActor());
            }
        });
        
    }


    //=========================================================================================

    private void switchToPIID(Long piid) {
        if(piid != null) {
            model.setCurrentPIID(piid);
            view.setCurrentPIID(piid);
            fetchStepActions();
            fetchProcessInstance();
        }
    }

    // User switched to a new workflow; update the model and view title
    private void switchToWorkflow(final String workflowName) {
        
        // Fetch the process instances for this workflow
        model.switchToWorkflow(workflowName, new Callback<Long>() {
            public void onSuccess(Long piid) {
                
                fetchInitialActions();
                
                if(piid != null) {
                    view.selectProcessInstance(piid);
                    fetchProcessInstance();
                } else {
                    controls.clearStepActions();
                }
            }
        });
        
    }

    private void fetchInitialActions() {

        // Fetch  the initial actions for this workflow
        model.fetchInitialActions(new Callback<List<Action>>() {
            public void onSuccess(List<Action> list) {
                installInitialActions(list);
            }
        });

    }

    private void fetchStepActions() {

        // Fetch the avaiable actions for all current steps for this PIID
        model.fetchStepActions(new Callback<List<StepActions>>() {
            public void onSuccess(List<StepActions> list) {
                installStepActions(list);
            }
        });

    }

    private void installInitialActions(List<Action> actions) {
        controls.installInitialActions(actions, new SimpleCallback<Integer>() {
            public void execute(Integer initialActionId) {
                startProcess(initialActionId.intValue());
            }
        });
    }

    private void installStepActions(List<StepActions> actions) {
        controls.installStepActions(actions, new SimpleCallback<Integer>() {
            public void execute(Integer actionId) {
                doAction(actionId.intValue());
            }
        });
    }

    // Process management
    //   - Create a Process Instance via an Initial Action
    //   - Execute a Current Step's 'doAction'
    //   - Update the Process Instances List
    //   - Update the Current Process Instance

    private void startProcess(int initialActionId) {
        model.startProcess(initialActionId, new Callback<Long>() {
            public void onSuccess(Long piid) {
                view.setCurrentPIID(piid);
                fetchProcessInstances();
                fetchProcessInstance();
                fetchStepActions();
            }
        });
        
    }

    private void fetchProcessInstance() {
        model.fetchProcessInstance(new Callback<ProcessInstanceState>() {
            public void onSuccess(ProcessInstanceState pi) {
                setCurrentSteps(pi.currentSteps);
                setHistorySteps(pi.historySteps);
                setProcessVariables(pi.processVariables);                
            }
        });
    }

    private void fetchProcessInstances() {
        model.fetchProcessInstances(new Callback<List<ProcessInstance>>() {
            public void onSuccess(List<ProcessInstance> list) {
                setProcessInstances(list);
            }
        });
    }

    private void fetchWorkflowOverview() {
        model.fetchWorkflowOverview(new Callback<List<Overview>>() {
            public void onSuccess(List<Overview> list) {
                setWorkflowOverview(list);
            }
        });
    }

    private void doAction(int actionId) {
        
        controls.clearStepActions();
        
        model.doAction(actionId, new Callback<Void>(){
            public void onSuccess(Void noop) {
                fetchProcessInstances();
                fetchProcessInstance();
                fetchStepActions();
            }
        });
    }


    private Timer createTimer(int seconds) {
        
        Timer timer = new Timer() {
            public void run() { fetchWorkflowOverview(); }
        };

        timer.scheduleRepeating(seconds * 1000);
            
        return timer;
    }


    // Process DTOs into ListGridRecord array for display -------------------------------------


    private void setCurrentSteps(List<Step> steps) { 
        
        ListGridRecord[] array = new ListGridRecord[steps.size()];

        int i=0;
        for(Step step : steps) {

            ListGridRecord listGridRecord = new ListGridRecord();

            listGridRecord.setAttribute("name", step.name);
            listGridRecord.setAttribute("status", step.status);
            listGridRecord.setAttribute("owner", step.owner);
            listGridRecord.setAttribute("created", step.startDate);
            listGridRecord.setAttribute("due", step.dueDate);
                 
            array[i++] = listGridRecord;
        }

        view.setCurrentStepsData(array);                
    }

    private void setHistorySteps(List<Step> steps) { 
        
        ListGridRecord[] array = new ListGridRecord[steps.size()];

        int i=0;
        for(Step step : steps) {

            ListGridRecord listGridRecord = new ListGridRecord();

            listGridRecord.setAttribute("name", step.name);
            listGridRecord.setAttribute("status", step.status);
            listGridRecord.setAttribute("actor", step.actor);
            listGridRecord.setAttribute("action", step.action);
            listGridRecord.setAttribute("finished",  step.finishDate);
                 
            array[i++] = listGridRecord;
        }

        view.setHistoryStepsData(array);                
    }

    private void setProcessVariables(List<ProcessVariable> processVariables) {
        
        ListGridRecord[] array = new ListGridRecord[processVariables.size()];

        int i=0;
        for(ProcessVariable processVariable : processVariables) {

            ListGridRecord listGridRecord = new ListGridRecord();

            listGridRecord.setAttribute("name", processVariable.name);
            listGridRecord.setAttribute("type", processVariable.type);
            listGridRecord.setAttribute("value", processVariable.value);
                 
            array[i++] = listGridRecord;
        }

        view.setProcessVariablesData(array);                
    }               


    private void setProcessInstances(List<ProcessInstance> list) {
                
        ListGridRecord[] array = new ListGridRecord[list.size()];

        int i=0;
        for(ProcessInstance pi : list) {

            ListGridRecord listGridRecord = new ListGridRecord();

            listGridRecord.setAttribute("piid", pi.piid);
            listGridRecord.setAttribute("state", pi.state);
            listGridRecord.setAttribute("current", pi.currentStepCount);
            listGridRecord.setAttribute("history", pi.historyStepCount);
                 
            array[i++] = listGridRecord;
        }

        view.setProcessInstancesData(array);
    }


    private void setWorkflowOverview(List<Overview> list) {
                
        ListGridRecord[] array = new ListGridRecord[list.size()];

        int i=0;
        for(Overview overview : list) {

            ListGridRecord listGridRecord = new ListGridRecord();

            listGridRecord.setAttribute("id", overview.id);
            listGridRecord.setAttribute("name", overview.stepName);
            listGridRecord.setAttribute("current", overview.current);
            listGridRecord.setAttribute("current-display", overview.current != 0 ? overview.current : "");
            listGridRecord.setAttribute("history", overview.history);
            listGridRecord.setAttribute("history-display", overview.history != 0 ? overview.history : "");
            listGridRecord.setAttribute("max", overview.maxPendingDisplay);
            listGridRecord.setAttribute("max-display", overview.maxPendingDisplay.equals("0ms") ? "" : overview.maxPendingDisplay);
            listGridRecord.setAttribute("avg", overview.avgPendingDisplay);
            listGridRecord.setAttribute("avg-display", overview.avgPendingDisplay.equals("0ms") ? "" : overview.avgPendingDisplay);
                 
            array[i++] = listGridRecord;
        }

        view.setWorkflowOverviewData(array);
    }


}
