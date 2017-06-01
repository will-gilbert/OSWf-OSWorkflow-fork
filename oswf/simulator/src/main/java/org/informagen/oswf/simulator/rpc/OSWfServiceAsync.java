package org.informagen.oswf.simulator.rpc;

// DTO
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;

// GWT Remote Service
import com.google.gwt.user.client.rpc.AsyncCallback;

//Java Collections
import java.util.List;
import java.util.Map;

public interface OSWfServiceAsync {
    
    void fetchWorkflowListing(AsyncCallback<List<ProcessDescriptorSummary>> callback);

    void fetchProcessInstances(String workflowName, AsyncCallback<List<ProcessInstance>> callback);

    void fetchInitialActions(String workflowName, AsyncCallback<List<Action>> callback);

    void fetchProcessInstanceState(long piid, AsyncCallback<ProcessInstanceState> callback);

    void fetchWorkflowOverview(String workflowName, AsyncCallback<List<Overview>> callback);

    void fetchStepActions(long piid, AsyncCallback<List<StepActions>> callback);

    void startProcess(String workflowName, String actor, int initialActionId, Map<String,String> inputs, AsyncCallback<Long> callback);

    void doAction(String workflowName, long piid, String actor, int actionId, Map<String,String> inputs, AsyncCallback<Void> callback);

}

