package org.informagen.oswf.simulator.client.workflows;


// Application
import org.informagen.oswf.simulator.client.application.Callback;

// Presenter
import org.informagen.oswf.simulator.client.workflows.WorkflowsPresenter;

// DTO
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;

// RPC Services
import org.informagen.oswf.simulator.rpc.OSWfServiceAsync;

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;


// Java - Collections
import java.util.List;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// Java Collections
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;

public class WorkflowsModel implements WorkflowsPresenter.Model {

    // RPC services
    private final OSWfServiceAsync service;

    @Inject
    public WorkflowsModel(OSWfServiceAsync service) {
        this.service = service;
    }

    public void fetchWorkflowListing(Callback<List<ProcessDescriptorSummary>> callback) {
       service.fetchWorkflowListing(callback);
   }

}
