package org.informagen.oswf.simulator.client.workflows;

// Application
import org.informagen.oswf.simulator.client.application.Callback;
import org.informagen.oswf.simulator.client.application.ContentPresenter;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent;

// DTO
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

// SmartGWT - ListGrid widget and events
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HasRecordClickHandlers;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;


// Java - Collections
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;


public class WorkflowsPresenter implements ContentPresenter {

    // MVP Interfaces =========================================================================

    public interface View {

        HasRecordClickHandlers getWorkflowSelector();
        String getSelectedWorkflow();

        void setData(ListGridRecord[] data);
        Widget asWidget();
    }

    public interface Model {
        //ListDataProvider<ProcessDescriptorSummary> getDataProvider();
        void fetchWorkflowListing(Callback<List<ProcessDescriptorSummary>> callback);
    }
        
    //=========================================================================================

    private final View view;
    private final Model model;
    private final EventBus eventBus;
        
    @Inject
    public WorkflowsPresenter(View view, Model model, EventBus eventBus) {
        this.view = view;
        this.model = model;
        this.eventBus = eventBus;
                        
        // Bind handler for various sources
        bindViewHandlers();
         
        // Load the available process descriptors aka workflows now,
        //    this list won't change so we can fetch it in the constructor.
        
        fetchWorkflowListing();
    }


    // ContentPresenter interface -------------------------------------------------------------
    
    public Widget getView() { 
        return view.asWidget(); 
    }
    
    public Widget getControlsWidget() { return null; }
    
    // ----------------------------------------------------------------------------------------
 
    void fetchWorkflowListing() {
        model.fetchWorkflowListing(new Callback<List<ProcessDescriptorSummary>>() {
            public void onSuccess(List<ProcessDescriptorSummary> summaries) {
                fillSummariesTable(summaries);
            }
        });
    }

    
    private void bindViewHandlers() {

        view.getWorkflowSelector().addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent clickEvent) {
                String workflowName = view.getSelectedWorkflow();
                WorkflowSelectionChangedEvent event = new WorkflowSelectionChangedEvent(workflowName);
                eventBus.fireEvent(event);
            }  
                
        });  

    }

    private final void fillSummariesTable(List<ProcessDescriptorSummary> summaries) {        
        
        ListGridRecord[] array = new ListGridRecord[summaries.size()];
        
        int i=0;
        for(ProcessDescriptorSummary summary : summaries) {

            ListGridRecord listGridRecord = new ListGridRecord();
 
            listGridRecord.setAttribute("name", summary.name);
            listGridRecord.setAttribute("iaCount", summary.initialActionCount);
            listGridRecord.setAttribute("stepCount", summary.stepCount);
            listGridRecord.setAttribute("splitCount", summary.splitCount);
            listGridRecord.setAttribute("joinCount", summary.joinCount);
            listGridRecord.setAttribute("piCount", summary.piCount);
            listGridRecord.setAttribute("piCount-display", summary.piCount != 0 ? summary.piCount : "");
                         
            array[i++] = listGridRecord;
        }
        
        view.setData(array);
        
        eventBus.fireEvent(new WorkflowSelectionChangedEvent(null));
    }

}
