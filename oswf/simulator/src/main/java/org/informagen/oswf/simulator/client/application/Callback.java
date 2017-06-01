package org.informagen.oswf.simulator.client.application;

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

import org.informagen.oswf.simulator.rpc.ServiceException;
import com.google.gwt.user.client.rpc.AsyncCallback;

// SmartGWT - Utilities
import com.smartgwt.client.util.SC;


public abstract class Callback<T> implements AsyncCallback<T> {

    public void onFailure(Throwable throwable) {
 
        String prefix = "";
        String severity = null;
        
        // Treat ServiceExceptions
        if (throwable instanceof ServiceException) {
            if( "error".equals(((ServiceException)throwable).getSeverity()) ) {
                prefix = "<strong>Server Error:</strong> ";
                severity = "error";
            } 
        }
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(prefix).append(throwable.getMessage()); 
        if(throwable.getCause() != null)
            buffer.append(": ").append(throwable.getCause().getMessage());
        
        if("error".equals(severity))
            SC.warn(buffer.toString());
        else
            SC.warn(buffer.toString());
    }
    
    public abstract void onSuccess(T result);
}