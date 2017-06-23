package org.informagen.oswf.simulator.rpc;

// RPC Exceptions
import org.informagen.oswf.simulator.rpc.ServiceException;

// GWT Remote Service
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// Java - Collections
import java.util.List;

@RemoteServiceRelativePath("oswfSimulatorService")
public interface GraphvizService extends RemoteService {

    String renderAsPNG(String workflowName) throws ServiceException;

    String renderAsSVG(String workflowName) throws ServiceException;
    
    String createDotNotation(String workflowName) throws ServiceException;
   
}
