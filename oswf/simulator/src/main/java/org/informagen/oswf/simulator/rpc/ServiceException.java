package org.informagen.oswf.simulator.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.lang.Exception;

public class ServiceException extends Exception implements IsSerializable {

    private String severity = "error";

    public ServiceException() {}
    
    public ServiceException(String message) {
        this(message, "error");
    }
    
    public ServiceException(Throwable throwable) {
        this(throwable.getMessage());
    }

    public ServiceException(String message, String severity) {
        super(message);
        setSeverity(severity);
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getSeverity() {
        return severity;
    }
}
