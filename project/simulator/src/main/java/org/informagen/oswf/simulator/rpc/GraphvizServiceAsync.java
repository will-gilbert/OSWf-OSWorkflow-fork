package org.informagen.oswf.simulator.rpc;

// GWT Remote Service
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GraphvizServiceAsync {

    void renderAsGraphviz(String workflowName, AsyncCallback<String> callback);

    void createDotNotation(String workflowName, AsyncCallback<String> callback);

}

