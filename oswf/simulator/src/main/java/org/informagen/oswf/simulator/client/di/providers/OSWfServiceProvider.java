package org.informagen.oswf.simulator.client.di.providers;

// RPC Service
import org.informagen.oswf.simulator.rpc.OSWfService; 
import org.informagen.oswf.simulator.rpc.OSWfServiceAsync; 

// Google Dependency Injection
import com.google.inject.Provider;

// GWT - Core,UI
import com.google.gwt.core.client.GWT;
  
public class OSWfServiceProvider implements Provider<OSWfServiceAsync> {

    // Global application singleton
    private static final OSWfServiceAsync rpcService = GWT.create(OSWfService.class); 

    //@Override
    public OSWfServiceAsync get() {
        return rpcService;
    }
}
