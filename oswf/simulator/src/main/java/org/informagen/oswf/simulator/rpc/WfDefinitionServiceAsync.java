package org.informagen.oswf.simulator.rpc;

// GWT Remote Service
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WfDefinitionServiceAsync {

    void fetchWfDefinition(String workflowName, AsyncCallback<String> callback);

    void fetchWfDefinitionAsHTML(String workflowName, String style, AsyncCallback<String> callback);

}

