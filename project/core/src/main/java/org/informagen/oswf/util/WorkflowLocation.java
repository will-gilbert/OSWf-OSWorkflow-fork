package org.informagen.oswf.util;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import java.io.File;

import java.net.URL;
import java.net.MalformedURLException;

public class WorkflowLocation {

    // type: 'resource', 'url', 'file', 'identifier'              
    public final String type; 
    public final String location;
    public WorkflowDescriptor descriptor;

    public WorkflowLocation(String type, String location) { 
        this.type = type;
        this.location = location;
    }

}
