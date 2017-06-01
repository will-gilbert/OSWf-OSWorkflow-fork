package org.informagen.oswf.simulator.rpc;

// DTO
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;

// RPC Exceptions
import org.informagen.oswf.simulator.rpc.ServiceException;

// GWT Remote Service
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// Java - Collections
import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("oswfSimulatorService")
public interface OSWfService extends RemoteService {
    
    List<ProcessDescriptorSummary> fetchWorkflowListing() throws ServiceException;

    List<ProcessInstance> fetchProcessInstances(String workflowName) throws ServiceException;

    List<Action> fetchInitialActions(String workflowName)  throws ServiceException;

    ProcessInstanceState fetchProcessInstanceState(long piid) throws ServiceException;
    
    List<Overview> fetchWorkflowOverview(String workflowName)  throws ServiceException;

    List<StepActions> fetchStepActions(long piid) throws ServiceException;

    Long startProcess(String workflowName, String actor, int initialActionId, Map<String,Object> inputs) throws ServiceException;
    
    void doAction(String workflowName,long piid, String actor, int actionId, Map<String,Object> inputs) throws ServiceException;

   
}
