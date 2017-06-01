package org.informagen.oswf.impl.loaders;

import org.informagen.oswf.WorkflowLoader;

import org.informagen.oswf.util.WorkflowLocation;

import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.util.WorkflowXMLParser;

import org.informagen.oswf.descriptors.*;

import org.informagen.oswf.util.XMLHelper;


// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.SAXException;

import org.w3c.dom.*;

import java.io.*;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.*;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.*;

public abstract class AbstractWorkflowLoader implements WorkflowLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowLoader.class);

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------
    protected Properties properties = new Properties();
    protected Map<String,WorkflowLocation> workflows = new HashMap<String,WorkflowLocation>();

    protected boolean reload;

    // C O N S T U C T O R S  ----------------------------------------------------------------- 

    public AbstractWorkflowLoader() {}

    public AbstractWorkflowLoader(Properties parameters, Map<String,Object> persistentArgs) {
        this.properties.putAll(parameters); 
        reload = Boolean.parseBoolean(properties.getProperty("reload", "false"));
    }


    // M E T H O D S  -------------------------------------------------------------------------

    @Override
    public void init(Map<String,WorkflowLocation> workflows) {
        this.workflows.putAll(workflows);
    }

    @Override
    public void initDone() throws WorkflowLoaderException {}


    @Override
    public Set<String> getWorkflowNames() {
        return workflows.keySet();
    }

    /**
     * Get a workflow descriptor given a workflow name.
     * @param name The name of the workflow to get.
     * @return The descriptor for the specified workflow.
     * @throws WorkflowLoaderException if the specified workflow name does not exist or cannot be located.
     *
     */

    @Override
    public WorkflowDescriptor getWorkflow(String name) throws WorkflowLoaderException {
        return getWorkflow(name, true);
    }

    @Override
    public String getWorkflowAsXML(String name) throws WorkflowLoaderException {
 
        WorkflowLocation workflowLocation = workflows.get(name);
        if(workflowLocation == null)
            workflowLocation = new WorkflowLocation("identifier", name);
       
        String workflowXML = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        
        try {
            inputStream = fetchProcessDefinition(workflowLocation);
            
    	    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    	    StringBuilder stringBuilder = new StringBuilder();
    	    String lineEnding = System.getProperty("line.separator");

    	    String line;
    	    while ((line = bufferedReader.readLine()) != null) 
    		    stringBuilder.append(line).append(lineEnding);
    		    
            workflowXML = stringBuilder.toString();
        } catch (Exception exception) {
            throw new WorkflowLoaderException("Error loading workflow: " + exception.getMessage(), exception);
        } finally {
            try {
                if(bufferedReader != null)
                    bufferedReader.close();

                if(inputStream != null)
                    inputStream.close();
            } catch(IOException ioException) {}
        }

        return workflowXML;
        
    }



    @Override
    public WorkflowDescriptor getWorkflow(String name, boolean validate) throws WorkflowLoaderException {

        WorkflowLocation workflowLocation = workflows.get(name);

        // Create a workflow using the 'identifier' as the locattion; 'fetchProcessDefinition'
        //   will try to do the right thing
        if (workflowLocation == null)
                workflowLocation = new WorkflowLocation("identifier", name);

        if (workflowLocation.descriptor == null || reload) {
            try {
                workflowLocation.descriptor = load(workflowLocation, validate);
            } catch (WorkflowLoaderException workflowLoaderException) {
                throw workflowLoaderException;
            } catch (Exception exception) {
                throw new WorkflowLoaderException("Error loading workflow", exception);
            }
        }
        
        if(workflowLocation.descriptor != null) {
            workflows.put(name, workflowLocation);
            workflowLocation.descriptor.setWorkflowName(name);
        }

        return workflowLocation.descriptor;
    }

    protected WorkflowDescriptor load(final WorkflowLocation workflowLocation, boolean validate) throws WorkflowLoaderException {

        WorkflowDescriptor workflowDescriptor = null;
        InputStream inputStream = null;
        
        try {
            inputStream = fetchProcessDefinition(workflowLocation);
            workflowDescriptor = WorkflowXMLParser.load(inputStream, validate);
        } catch (SAXException saxException) {
            throw new WorkflowLoaderException("XML parsing error loading workflow: " + saxException.getMessage(), saxException);
        } catch (Exception exception) {
            throw new WorkflowLoaderException("Error loading workflow: " + exception.getMessage(), exception);
        } finally {
            try {
                if(inputStream != null)
                    inputStream.close();
            } catch(IOException ioException) {}
        }

        return workflowDescriptor;
    }

    protected abstract InputStream fetchProcessDefinition(WorkflowLocation workflowLocation) throws WorkflowLoaderException; 

}
