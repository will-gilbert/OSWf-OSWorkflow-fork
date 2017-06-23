package org.informagen.oswf.simulator.server;


// Service Interfaces 
import org.informagen.oswf.simulator.rpc.ServiceException;
import org.informagen.oswf.simulator.rpc.OSWfService;
import org.informagen.oswf.simulator.rpc.WfDefinitionService;
import org.informagen.oswf.simulator.rpc.GraphvizService;

// DTOs
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.ProcessInstanceState;
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;
import org.informagen.oswf.simulator.dto.Overview;


// GWT - servlet
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Google Dependency Injection
import com.google.inject.Guice;
import com.google.inject.Injector;

// Java - IO
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.StringWriter;

// Java - Collections
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.List;

// Java - Util
import java.util.Date;

// J2EE
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@SuppressWarnings("serial")
public class OSWfSimulatorServlet extends RemoteServiceServlet 
                                  implements OSWfService,
                                             WfDefinitionService,
                                             GraphvizService {

    private static Logger logger = LoggerFactory.getLogger(OSWfSimulatorServlet.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    private static Injector injector = null;
 
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        startupLogger.info("Starting OSWfSimulatorServlet");

        ServletContext servletContext = servletConfig.getServletContext();
        injector = (Injector) servletContext.getAttribute("injector");
    
    }

    //=========================================================================================
    //--- OSWfService -------------------------------------------------------------------------

    public List<ProcessDescriptorSummary> fetchWorkflowListing() throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchWorkflowListing();
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
    
    public List<ProcessInstance> fetchProcessInstances(String workflowName) throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchProcessInstances(workflowName);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
        
    public List<Action> fetchInitialActions(String workflowName) throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchInitialActions(workflowName);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
        
    public ProcessInstanceState fetchProcessInstanceState(long piid)  throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchProcessInstanceState(piid);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
        
    public List<Overview> fetchWorkflowOverview(String workflowName)  throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchWorkflowOverview(workflowName);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
        
    public List<StepActions> fetchStepActions(long piid)  throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).fetchStepActions(piid);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }

    public Long startProcess(String workflowName, String actor, int initialAction, Map<String,Object> inputs) throws ServiceException {
        try {
            return injector.getInstance(OSWfService.class).startProcess(workflowName, actor, initialAction, inputs);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }

    public void doAction(String workflowName, long piid, String actor, int action, Map<String,Object> inputs) throws ServiceException {
        try {
            injector.getInstance(OSWfService.class).doAction(workflowName, piid, actor, action, inputs);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }

    //--- WfDefinitionService -----------------------------------------------------------------
    
    public String fetchWfDefinition(String workflowName) throws ServiceException {
        try {
            return injector.getInstance(WfDefinitionService.class).fetchWfDefinition(workflowName);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
    
    public String fetchWfDefinitionAsHTML(String workflowName, String style) throws ServiceException {
        try {
            return injector.getInstance(WfDefinitionService.class).fetchWfDefinitionAsHTML(workflowName, style);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }

    //--- GraphvizService ---------------------------------------------------------------------
    
    // public String renderAsGraphviz(String workflowName) throws ServiceException {
    //     try {
    //         // Add preamble so that Javascript can interpret
    //         return new StringBuffer()
    //             .append("data:image/png;base64,")
    //             .append(injector.getInstance(GraphvizService.class).renderAsGraphviz(workflowName))
    //             .toString()
    //         ; 
                        
    //     } catch (Throwable throwable) { throw createServiceException(throwable); }
    // }
     
    public String renderAsPNG(String workflowName) throws ServiceException {
        try {
            // Add preamble so that Javascript can interpret
            return new StringBuffer()
                .append("data:image/png;base64,")
                .append(injector.getInstance(GraphvizService.class).renderAsPNG(workflowName))
                .toString()
            ; 
                        
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
        
    public String renderAsSVG(String workflowName) throws ServiceException {
        try {
            // Add preamble so that Javascript can interpret
            return new StringBuffer()
                .append("data:image/svg+xml;")
                .append(injector.getInstance(GraphvizService.class).renderAsSVG(workflowName))
                .toString()
            ; 
                        
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }
    
    public String createDotNotation(String workflowName) throws ServiceException {
        try {
            return injector.getInstance(GraphvizService.class).createDotNotation(workflowName);
        } catch (Throwable throwable) { throw createServiceException(throwable); }
    }

    //--- End: Service interfaces -------------------------------------------------------------
    //=========================================================================================

    private ServiceException createServiceException(Throwable throwable) {
        return new ErrorProcessor().createServiceException(throwable);
    }




    
}





