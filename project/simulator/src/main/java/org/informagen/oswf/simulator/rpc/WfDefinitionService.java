package org.informagen.oswf.simulator.rpc;

// RPC Exceptions
import org.informagen.oswf.simulator.rpc.ServiceException;

// GWT Remote Service
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// Java - Collections
import java.util.List;

@RemoteServiceRelativePath("oswfSimulatorService")
public interface WfDefinitionService extends RemoteService {

    String fetchWfDefinition(String workflowName) throws ServiceException;

    String fetchWfDefinitionAsHTML(String workflowName, String style) throws ServiceException;
   
}
