package org.informagen.oswf.simulator.server;


import org.informagen.oswf.simulator.server.OSWfServiceImpl;

// Service Interfaces 
import org.informagen.oswf.simulator.rpc.ServiceException;
import org.informagen.oswf.simulator.rpc.OSWfService;

// DTOs
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.Action;

// GWT - servlet
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - IO
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.StringWriter;

// Java - Collections
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

// Java - Util
import java.util.Date;

// J2EE
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


@SuppressWarnings("serial")
public class ErrorProcessor  {

    private static Logger logger = LoggerFactory.getLogger(ErrorProcessor.class);
       
    public ErrorProcessor() {}

    public ServiceException createServiceException(Throwable throwable) {

        if(throwable instanceof ServiceException) {
            ServiceException serviceException = (ServiceException)throwable;
            if((serviceException.getSeverity() != null) && ("error".equals(serviceException.getSeverity()) == false)) {
                logger.warn(serviceException.getMessage());
                return serviceException;
            }
        }                
        
        logger.error((throwable.getMessage() == null) ? throwable.toString() : throwable.getMessage());
        
        return new ServiceException(createExceptionMessage(throwable));
    }

    private String createExceptionMessage(Throwable throwable) {

        if(throwable.getCause() != null)
            throwable = throwable.getCause();
                
        // Create error message
        StringBuffer buffer = new StringBuffer();
        buffer.append(throwable.getMessage() == null ? throwable.toString() : throwable.getMessage());
        
        // Send a limited stack trace to the UI; HTML formatted
        buffer.append("<br><br><strong>Stack Trace for ")
              .append(new Date().toString())
              .append(":</strong><br>");
        
        for(StackTraceElement element : throwable.getStackTrace()) {
            
            String className = element.getClassName();

            // Stop when we reach our servlet; the rest will be just web container cruft    
            if(className.endsWith("Servlet"))
                break;

            int index = className.lastIndexOf('.');
            buffer.append("<span style='color:#ff0000;'>")
                  .append(className.substring(index+1))
                  .append("</span>.")
                  .append(element.getMethodName())
                  .append(" at line ")
                  .append(element.getLineNumber())
                  .append("<br>")
            ;
        }
        
        return buffer.toString();
    }
    

    
}
