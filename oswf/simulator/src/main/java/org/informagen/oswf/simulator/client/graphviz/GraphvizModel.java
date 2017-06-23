package org.informagen.oswf.simulator.client.graphviz;

// Application
import org.informagen.oswf.simulator.client.application.Callback;

// Presenter
import org.informagen.oswf.simulator.client.graphviz.GraphvizPresenter;

// RPC Services
import org.informagen.oswf.simulator.rpc.GraphvizServiceAsync;

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

public class GraphvizModel implements GraphvizPresenter.Model {

    // RPC services
    private final GraphvizServiceAsync service;

    private String currentWorkflowName = null;

    @Inject
    public GraphvizModel(GraphvizServiceAsync service) {
        this.service = service;
        
    }

    public void renderWorkflowImage(final String workflowName, final Callback<String> callback) {

        if(workflowName == null)
            return;
        
        currentWorkflowName = null;
        
        service.renderAsPNG(workflowName, new Callback<String>(){
            public void onSuccess(String base64Image) {
                currentWorkflowName = workflowName;
                callback.onSuccess(base64Image);
            }
        });
    }

}
