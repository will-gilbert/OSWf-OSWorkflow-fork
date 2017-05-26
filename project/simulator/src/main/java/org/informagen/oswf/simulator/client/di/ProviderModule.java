package org.informagen.oswf.simulator.client.di;


// OSWf Simulator - Services
import org.informagen.oswf.simulator.rpc.OSWfServiceAsync;
import org.informagen.oswf.simulator.rpc.GraphvizServiceAsync;


// OSWf Simulator - GIN Providers
import org.informagen.oswf.simulator.client.di.providers.EventBusProvider;
import org.informagen.oswf.simulator.client.di.providers.OSWfServiceProvider;
import org.informagen.oswf.simulator.client.di.providers.GraphvizServiceProvider;


// GWT - Event Bus
import com.google.gwt.event.shared.EventBus;

// Google Dependency Injection
import com.google.gwt.inject.client.AbstractGinModule;

// Google - Injection
import com.google.inject.Singleton;

public class ProviderModule extends AbstractGinModule {
    
    @Override
    protected void configure() {
        
        // Application Event Bus --------------------------------------------------------------
        
        bind(EventBus.class)
            .toProvider(EventBusProvider.class)
            .in(Singleton.class);

 
        // Service Providers ------------------------------------------------------------------
            
        bind(OSWfServiceAsync.class)
            .toProvider(OSWfServiceProvider.class)
            .in(Singleton.class);
            
        bind(GraphvizServiceAsync.class)
            .toProvider(GraphvizServiceProvider.class)
            .in(Singleton.class);

    }
 
}
