package org.informagen.oswf.simulator.client.di;

// OSWf Simulator - Presenters
import org.informagen.oswf.simulator.client.application.AboutBoxPresenter;
import org.informagen.oswf.simulator.client.application.ApplicationPresenter;
import org.informagen.oswf.simulator.client.workflows.WorkflowsPresenter;
import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionPresenter;
import org.informagen.oswf.simulator.client.graphviz.GraphvizPresenter;

import org.informagen.oswf.simulator.client.simulator.SimulatorPresenter;
import org.informagen.oswf.simulator.client.simulator.inputs.InputsPresenter;
import org.informagen.oswf.simulator.client.simulator.ActorPresenter;

// GWT - EventBus
import com.google.gwt.event.shared.EventBus;

// Google Dependency Injection
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.inject.client.GinModules;

@GinModules({InjectorModule.class, ProviderModule.class})
public interface Injector extends Ginjector {

    // Presenters
    AboutBoxPresenter getAboutBoxPresenter();

    ApplicationPresenter getApplicationPresenter();

    WorkflowsPresenter getWorkflowsPresenter();

    WfDefinitionPresenter getWfDefinitionPresenter();

    GraphvizPresenter getGraphvizPresenter();

    SimulatorPresenter getSimulatorPresenter();

    InputsPresenter getInputsPresenter();

    ActorPresenter getActorPresenter();
    
    // Events
    EventBus getEventBus();

}
