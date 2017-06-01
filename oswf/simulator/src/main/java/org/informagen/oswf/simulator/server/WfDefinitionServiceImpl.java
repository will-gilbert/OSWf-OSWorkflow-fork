package org.informagen.oswf.simulator.server;

// Application - RPC 
import org.informagen.oswf.simulator.rpc.WfDefinitionService;
import org.informagen.oswf.simulator.rpc.ServiceException;

// OSWf Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.util.Graphviz;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf Default Implementations & Service Providers
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;
import org.informagen.oswf.exceptions.WorkflowLoaderException;

 // Java
import java.lang.StringBuffer;
import java.lang.Process;
import java.lang.Runtime;

// Java IO
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// XML XSLT
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.servlet.http.HttpServletResponse;

// Simple Logging Facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Google DI Annotation
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class WfDefinitionServiceImpl implements WfDefinitionService {

    private static Logger logger = LoggerFactory.getLogger(WfDefinitionService.class);
    private static Logger startupLogger = LoggerFactory.getLogger("StartupLogger");

    OSWfConfiguration configuration;

    @Inject
    public WfDefinitionServiceImpl(OSWfConfiguration configuration) {
        this.configuration = configuration;
        startupLogger.info("Configuring WfDefinitionService");
    }

    public String fetchWfDefinition(String workflowName) throws ServiceException {
        try {
            return configuration.getWorkflowAsXML(workflowName);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new ServiceException(exception);
        } 
    }


    public String fetchWfDefinitionAsHTML(String workflowName, String style) throws ServiceException {

        String wfDefinition = null;
        String xsltFilename = null;
        
        if("ColorizedXML".equals(style))
            xsltFilename = "/xmlToColorizedHTML.xsl.xml";
        else if("ColorizedOSWf".equals(style))
            xsltFilename = "/oswfToColorizedHTML.xsl.xml";
 

        try {
            String xml = fetchWfDefinition(workflowName);
            
            if(xsltFilename != null)        
                wfDefinition = colorizeXML(xml, xsltFilename);
            else
                wfDefinition = xmlToHTML(xml);
                
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new ServiceException(exception);
        } 
               
        return wfDefinition;
    }

    String colorizeXML(String xml, String xsltFilename) throws TransformerConfigurationException, TransformerException {
 
        // Get the XSLT file as a resource
        InputStream xslt = getClass().getResourceAsStream(xsltFilename);
        
        // Create and configure XSLT Transformer 
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslt));
        transformer.setParameter("indent-elements", "yes");
        
        OutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        
        // Convert the XML into HTML per the XSLT file
        transformer.transform(new StreamSource(inputStream), new StreamResult(outputStream));
        
        return new String(((ByteArrayOutputStream)outputStream).toByteArray());
    }

    private String xmlToHTML(String xml) {
        
        if(xml == null)
            return "";
        
        StringBuffer stringBuffer = new StringBuffer();
        
        stringBuffer.append("<div style='font-family:monospace; white-space:nowrap;'>");

        int len = xml.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = xml.charAt(i);
            if (c == '<')
                stringBuffer.append("&lt;");
            else if (c == '\n')
                stringBuffer.append("<br />");
            else if (c == '\t')
                stringBuffer.append("&nbsp;&nbsp;");
            else if (c == ' ')
                stringBuffer.append("&nbsp;");
            else {
                 // If 7 bit pass thru otherwise convert to unicode
                int ci = 0xffff & c;
                if (ci < 160 )
                    stringBuffer.append(c);
                else {
                    stringBuffer.append("&#");
                    stringBuffer.append(new Integer(ci).toString());
                    stringBuffer.append(';');
                }
            }
        }
        
        stringBuffer.append("</div>");
        
        return stringBuffer.toString();
    }


}





