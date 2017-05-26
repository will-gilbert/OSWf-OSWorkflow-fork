package org.informagen.oswf.simulator.server;
import org.informagen.oswf.OSWfConfiguration;

import org.informagen.oswf.simulator.server.Locality;

import org.informagen.oswf.simulator.rpc.OSWfService;
import org.informagen.oswf.simulator.server.OSWfServiceImpl;

import org.informagen.oswf.simulator.rpc.GraphvizService;
import org.informagen.oswf.simulator.server.GraphvizServiceImpl;

import org.informagen.oswf.simulator.rpc.WfDefinitionService;
import org.informagen.oswf.simulator.server.WfDefinitionServiceImpl;

// Google Dependency Injection
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ServiceModule extends AbstractModule {

    private final Locality locality;
    private final OSWfConfiguration oswfConfiguration;
    
    public ServiceModule(Locality locality, OSWfConfiguration oswfConfiguration) {
        this.locality = locality;
        this.oswfConfiguration = oswfConfiguration;
    }

    protected void configure() {

        bindConstant().annotatedWith(Names.named("executable.graphviz"))
                      .to(locality.getValue("executable.graphviz", "dot"));

        bind(OSWfConfiguration.class).toInstance(oswfConfiguration);
        
        // Service implementations are singletons
        bind(OSWfServiceImpl.class).in(Scopes.SINGLETON);
        bind(GraphvizServiceImpl.class).in(Scopes.SINGLETON);
        bind(WfDefinitionServiceImpl.class).in(Scopes.SINGLETON);

        // Bind interfaces to implementation classes
        bind(OSWfService.class).to(OSWfServiceImpl.class);
        bind(WfDefinitionService.class).to(WfDefinitionServiceImpl.class);
        bind(GraphvizService.class).to(GraphvizServiceImpl.class);
    }

}
