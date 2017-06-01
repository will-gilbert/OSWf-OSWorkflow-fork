package org.informagen.oswf.impl.loaders;

import org.informagen.oswf.WorkflowLoader;
import org.informagen.oswf.impl.loaders.AbstractWorkflowLoader;

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

public class URLLoader extends AbstractWorkflowLoader {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowLoader.class);

    // C O N S T U C T O R S  ----------------------------------------------------------------- 

    public URLLoader() {}

    public URLLoader(Properties parameters, Map<String,Object> persistentArgs) {
        super(parameters, persistentArgs);
    }


    protected InputStream fetchProcessDefinition(WorkflowLocation workflowLocation) throws WorkflowLoaderException {

        InputStream inputStream = null;
        String type = workflowLocation.type;
        String location = workflowLocation.location;

        try {
            if("resource".equals(type)) {
                inputStream = Thread.currentThread().getContextClassLoader().getResource(location).openStream();
            } else if ("url".equals(type)) {
                inputStream = new URL(location).openStream();
            } else if ("file".equals(type)) {
                File file = new File(location);
                inputStream = file.toURI().toURL().openStream();
            } else if ("identifier".equals(type)) {
                inputStream = new URL(location).openStream();
            }
        } catch (Exception exception) {
            throw new WorkflowLoaderException(exception);
        }

        return inputStream;
    }

}
