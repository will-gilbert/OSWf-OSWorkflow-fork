package org.informagen.oswf.simulator.client.wfdefinition;

// Application
import org.informagen.oswf.simulator.client.application.Callback;
import org.informagen.oswf.simulator.client.application.ContentPresenter;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent;
import org.informagen.oswf.simulator.client.events.RequestReSTEvent; 


// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// GWT - Events
import com.google.gwt.event.shared.EventBus;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// SmartGWT - Form Events
import com.smartgwt.client.widgets.form.fields.events.HasChangedHandlers;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;

// Google Inject
import com.google.inject.Inject;

public class WfDefinitionPresenter implements ContentPresenter {

    // MVP Interfaces =========================================================================

    public interface View {
        void displayDescriptor(String descriptor);
        Widget asWidget();
    }

    public interface Controls {
        String getRendering(); 
        HasChangedHandlers getRenderer(); 
        HasClickHandlers getDownloadClickable();      
        void mediate();
        Widget asWidget();
    }

    public interface Model {
        void fetchWfDefinitionAsHTML(String workflowName, String style, Callback<String> callback);    
    }
        
    //=========================================================================================

    private final View view;
    private final Controls controls;
    private final Model model;
    private final EventBus eventBus;
    
    String workflowName = null;
        
    @Inject
    public WfDefinitionPresenter(View view, Controls controls, Model model, EventBus eventBus) {        
        this.view = view;
        this.controls = controls;
        this.model = model;
        this.eventBus = eventBus;

        bindControlHandlers();
        bindEventHandlers();
    }

    // ContentPresenter interface -------------------------------------------------------------
   
    public Widget getView() { 
        return view.asWidget(); 
    }

    public Widget getControlsWidget() { 
        return controls.asWidget(); 
    }
    
    // ----------------------------------------------------------------------------------------

    private void bindControlHandlers() {

        controls.getRenderer().addChangedHandler(new ChangedHandler(){
            public void onChanged(ChangedEvent event) {
                fetchWfDefinitionAsHTML();
            }
        });

        controls.getDownloadClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new RequestReSTEvent("GET", "workflows/" + workflowName + "/definition"));
            }
        });
        
    }

    private void bindEventHandlers() {

        eventBus.addHandler(WorkflowSelectionChangedEvent.TYPE,
            new HandlerFor.WorkflowSelectionChangedEvent() {
                public void processEvent(WorkflowSelectionChangedEvent event) {
                   switchToWorkflow(event.getWorkflowName());
                }
            }
        );
    }

    // ----------------------------------------------------------------------------------------

    private void switchToWorkflow(final String workflowName) {
        this.workflowName = workflowName;
        fetchWfDefinitionAsHTML();
    }

    private void fetchWfDefinitionAsHTML() {

        model.fetchWfDefinitionAsHTML(workflowName, controls.getRendering(), new Callback<String>(){
            public void onSuccess(String renderedXML) {
                view.displayDescriptor(renderedXML);
            }
        });        
        
    }

}
