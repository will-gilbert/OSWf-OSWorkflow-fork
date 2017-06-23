package org.informagen.oswf.simulator.rpc;

// GWT Remote Service
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GraphvizServiceAsync {

    void renderAsPNG(String workflowName, AsyncCallback<String> callback);

    void renderAsSVG(String workflowName, AsyncCallback<String> callback);

    void createDotNotation(String workflowName, AsyncCallback<String> callback);

}

