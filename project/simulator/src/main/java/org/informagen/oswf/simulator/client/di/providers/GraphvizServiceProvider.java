package org.informagen.oswf.simulator.client.di.providers;

// RPC Service
import org.informagen.oswf.simulator.rpc.GraphvizService; 
import org.informagen.oswf.simulator.rpc.GraphvizServiceAsync; 

// Google Dependency Injection
import com.google.inject.Provider;

// GWT - Core,UI
import com.google.gwt.core.client.GWT;
  
public class GraphvizServiceProvider implements Provider<GraphvizServiceAsync> {

    // Global application singleton
    private static final GraphvizServiceAsync rpcService = GWT.create(GraphvizService.class); 

    //@Override
    public GraphvizServiceAsync get() {
        return rpcService;
    }
}
