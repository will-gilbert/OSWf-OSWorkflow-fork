package org.informagen.oswf.simulator.client.wfdefinition;

// Application
import org.informagen.oswf.simulator.client.application.Callback;

// Presenter
import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionPresenter;

// RPC Services
import org.informagen.oswf.simulator.rpc.WfDefinitionServiceAsync;

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// Java Collections
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;

public class WfDefinitionModel implements WfDefinitionPresenter.Model {

    // RPC services
    private final WfDefinitionServiceAsync service;
    private String currentWorkflowName = null;

    @Inject
    public WfDefinitionModel(WfDefinitionServiceAsync service) {
        this.service = service;
    }

    public void fetchWfDefinitionAsHTML(final String workflowName, final String style, final Callback<String> callback) {

        if(workflowName == null)
            return;

        currentWorkflowName = null;
            
        service.fetchWfDefinitionAsHTML(workflowName, style, new Callback<String>(){
            public void onSuccess(String xml) {
                currentWorkflowName = workflowName;
                callback.onSuccess(xml);
            }
        });
    }
}
