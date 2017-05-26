package org.informagen.oswf.simulator.client.graphviz;

// Application
import org.informagen.oswf.simulator.client.application.Callback;
import org.informagen.oswf.simulator.client.application.SimpleCallback;
import org.informagen.oswf.simulator.client.application.ContentPresenter;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent;
import org.informagen.oswf.simulator.client.events.RequestReSTEvent; 

// DTO
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;


// GWT - Events
import com.google.gwt.event.shared.EventBus;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// Java - Collections
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;


public class GraphvizPresenter implements ContentPresenter {

    // MVP Interfaces =========================================================================

    public interface View {
        void displayImage(String base64Image);
        Widget asWidget();
    }


    public interface Controls {
        HasClickHandlers getDOTDownloadClickable();      
        HasClickHandlers getPNGDownloadClickable();      
        void mediate();
        Widget asWidget();
    }

    public interface Model {
        void renderAsGraphviz(String workflowName, Callback<String> callback);    
    }
        
    //=========================================================================================

    private final View view;
    private final Controls controls;
    private final Model model;
    private final EventBus eventBus;

    String workflowName = null;
        
    @Inject
    public GraphvizPresenter(View view, Controls controls, Model model, EventBus eventBus) {
        
        this.view = view;
        this.controls = controls;
        this.model = model;
        this.eventBus = eventBus;

        bindEventHandlers();
    }

    // ContentPresenter interface -------------------------------------------------------------
   
    public Widget getView() { return view.asWidget(); }
    
    public Widget getControlsWidget() {
        return controls.asWidget(); 
    }
    
    // ----------------------------------------------------------------------------------------

    private void bindEventHandlers() {

        eventBus.addHandler(WorkflowSelectionChangedEvent.TYPE,
            new HandlerFor.WorkflowSelectionChangedEvent() {
                public void processEvent(WorkflowSelectionChangedEvent event) {
                   switchToWorkflow(event.getWorkflowName());
                }
            }
        );

        controls.getDOTDownloadClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new RequestReSTEvent("GET", "workflows/" + workflowName + "/graphviz"));
            }
        });

        controls.getPNGDownloadClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new RequestReSTEvent("GET", "workflows/" + workflowName + "/image"));
            }
        });
    }

    //=========================================================================================
    // Event bus methods ======================================================================

    // User switched to a new workflow; update the model and view title

    private void switchToWorkflow(final String workflowName) {
        
        this.workflowName = workflowName;

        model.renderAsGraphviz(workflowName, new Callback<String>(){
            public void onSuccess(String base64Image) {
                if(base64Image != null)
                    view.displayImage(base64Image);
            }
        });        
    }

}
