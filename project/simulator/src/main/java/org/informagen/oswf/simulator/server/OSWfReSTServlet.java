package org.informagen.oswf.simulator.server;

// Application - RPC 
import org.informagen.oswf.simulator.rpc.GraphvizService;
import org.informagen.oswf.simulator.rpc.WfDefinitionService;
import org.informagen.oswf.simulator.rpc.ServiceException;

// OSWf Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.util.Graphviz;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.propertyset.util.Base64;

// OSWf Default Implementations & Service Providers
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

// Service Interfaces 
import org.informagen.oswf.simulator.rpc.ServiceException;
import org.informagen.oswf.simulator.rpc.OSWfService;
import org.informagen.oswf.simulator.rpc.WfDefinitionService;
import org.informagen.oswf.simulator.rpc.GraphvizService;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - IO
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

// Java - Collections
import java.util.List;
import java.util.ArrayList;

// J2EE
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

// Google Dependency Injection
import com.google.inject.Injector;

@SuppressWarnings("serial")
public class OSWfReSTServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(OSWfReSTServlet.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    private static Injector injector = null;
     
    public void init(ServletConfig servletConfig) throws ServletException {

        startupLogger.info("Starting OSWfReSTServlet");

        ServletContext servletContext = servletConfig.getServletContext();
        injector = (Injector) servletContext.getAttribute("injector");
    }


    // GET --> ReST 'Fetch' or CRUD 'select'
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Parse out the ReST tokens into a list
        List<String> tokens = new ArrayList<String>();
        tokens.add(request.getServletPath().split("/")[1]);
        tokens.addAll(parseReST(request.getPathInfo()));

       if( "workflows".equals(tokens.get(0)) ) {
           dispatchWorkflowsGET(tokens, response);
      } 
        
    }

    List<String> parseReST(String pathInfo) {
        List<String> list = new ArrayList<String>();
        
        for(String token : pathInfo.split("/"))
            if(token.isEmpty() == false)
                list.add(token);
                
        return list;
    }

    void dispatchWorkflowsGET(List<String> tokens, HttpServletResponse response) throws ServletException {
                   
        if(tokens.size() == 2 && tokens.get(1).endsWith(".oswf.xml"))
            downloadWorkflowDescription(response, tokens.get(1).replaceAll(".oswf.xml$",""));
        else if(tokens.size() == 2 && tokens.get(1).endsWith(".xml"))
            downloadWorkflowDescription(response, tokens.get(1).replaceAll(".xml$",""));
        else if(tokens.size() == 2 && tokens.get(1).endsWith(".dot"))
            downloadGraphvizFile(response, tokens.get(1).replaceAll(".dot$",""));
        else if(tokens.size() == 2 && tokens.get(1).endsWith(".png"))
            downloadGraphvizPNG(response, tokens.get(1).replaceAll(".png$",""));
        else if("definition".equals(tokens.get(2)))
            downloadWorkflowDescription(response, tokens.get(1));
        else if("graphviz".equals(tokens.get(2)))
            downloadGraphvizFile(response, tokens.get(1));
        else if("image".equals(tokens.get(2)))
            downloadGraphvizPNG(response, tokens.get(1));
        
    }
    
    public void downloadWorkflowDescription(HttpServletResponse response, String workflowName) throws ServletException {
     
        try {

            String xml = injector.getInstance(WfDefinitionService.class).fetchWfDefinition(workflowName);
            
            PrintWriter writer = response.getWriter();
            response.setContentType("application/force-download");
            response.setContentLength(xml.length());
            response.setHeader("Content-Transfer-Encoding", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + workflowName + ".oswf.xml\"");
        
            BufferedReader reader = new BufferedReader(new StringReader(xml)); 
            String line;
            while((line = reader.readLine()) != null) {
                writer.print(line);
                writer.print("\n");
            }
            
        } catch (Exception exception) {
            throw new ServletException(exception);
        }
        
    }

    public void downloadGraphvizFile(HttpServletResponse response, String workflowName) throws ServletException {

        try {

            String dot = injector.getInstance(GraphvizService.class).createDotNotation(workflowName);
            
            PrintWriter writer = response.getWriter();
            response.setContentType("application/force-download");
            response.setContentLength(dot.length());
            response.setHeader("Content-Transfer-Encoding", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + workflowName + ".dot\"");
        
            BufferedReader reader = new BufferedReader(new StringReader(dot)); 
            String line;
            while((line = reader.readLine()) != null) {
                writer.print(line);
                writer.print("\n");
            }
            
        } catch (Exception exception) {
            throw new ServletException(exception);
        }
    }        

    public void downloadGraphvizPNG(HttpServletResponse response, String workflowName) throws ServletException {

        try {

            // Base64 encoded PNG
            String base64PNG = injector.getInstance(GraphvizService.class).renderAsGraphviz(workflowName);            
            byte[] bytes = Base64.getInstance().decode(base64PNG);
            
            response.setContentType("application/force-download");
            response.setContentLength(bytes.length);
            response.setHeader("Content-Transfer-Encoding", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + workflowName + ".png\"");

            response.getOutputStream().write(bytes);        
            
        } catch (Exception exception) {
            throw new ServletException(exception);
        }
    }        
    
}





