package org.informagen.oswf.simulator.client.di;

// OSWf Simulator - Controllers

import org.informagen.oswf.simulator.client.application.AboutBoxPresenter;
import org.informagen.oswf.simulator.client.application.AboutBoxView;

import org.informagen.oswf.simulator.client.application.ApplicationPresenter;
import org.informagen.oswf.simulator.client.application.ApplicationView;
import org.informagen.oswf.simulator.client.application.ApplicationModel;

import org.informagen.oswf.simulator.client.application.HeaderPresenter;
import org.informagen.oswf.simulator.client.application.HeaderView;

import org.informagen.oswf.simulator.client.application.AccordionPresenter;

import org.informagen.oswf.simulator.client.application.FooterPresenter;
import org.informagen.oswf.simulator.client.application.FooterView;

import org.informagen.oswf.simulator.client.workflows.WorkflowsPresenter;
import org.informagen.oswf.simulator.client.workflows.WorkflowsView;
import org.informagen.oswf.simulator.client.workflows.WorkflowsModel;

import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionPresenter;
import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionView;
import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionControls;
import org.informagen.oswf.simulator.client.wfdefinition.WfDefinitionModel;

import org.informagen.oswf.simulator.client.graphviz.GraphvizPresenter;
import org.informagen.oswf.simulator.client.graphviz.GraphvizView;
import org.informagen.oswf.simulator.client.graphviz.GraphvizControls;
import org.informagen.oswf.simulator.client.graphviz.GraphvizModel;

import org.informagen.oswf.simulator.client.simulator.SimulatorPresenter;
import org.informagen.oswf.simulator.client.simulator.SimulatorView;
import org.informagen.oswf.simulator.client.simulator.SimulatorControls;
import org.informagen.oswf.simulator.client.simulator.SimulatorModel;

import org.informagen.oswf.simulator.client.simulator.inputs.InputsPresenter;
import org.informagen.oswf.simulator.client.simulator.inputs.InputsView;

import org.informagen.oswf.simulator.client.simulator.ActorPresenter;
import org.informagen.oswf.simulator.client.simulator.ActorView;


// GWT - Event Bus
import com.google.gwt.event.shared.EventBus;

// Google Dependency Injection
import com.google.gwt.inject.client.AbstractGinModule;

// Google Injection
import com.google.inject.Singleton;

public class InjectorModule extends AbstractGinModule {

    @Override
    protected void configure() {

        // Sharted Singletons -----------------------------------------------------------------

        bind(ApplicationModel.class).in(Singleton.class);
        bind(SimulatorModel.class).in(Singleton.class);

       
        // MVP Diads/Triads -------------------------------------------------------------------
        
        bind(AboutBoxPresenter.class).in(Singleton.class);
        bind(AboutBoxPresenter.View.class).to(AboutBoxView.class);
        
        bind(ApplicationPresenter.class).in(Singleton.class);
        bind(ApplicationPresenter.View.class).to(ApplicationView.class);
        bind(ApplicationPresenter.Model.class).to(ApplicationModel.class);

        bind(HeaderPresenter.class).in(Singleton.class);
        bind(HeaderPresenter.View.class).to(HeaderView.class);

        bind(AccordionPresenter.class).in(Singleton.class);

        bind(FooterPresenter.class).in(Singleton.class);
        bind(FooterPresenter.View.class).to(FooterView.class);
        
        bind(WorkflowsPresenter.class).in(Singleton.class);
        bind(WorkflowsPresenter.View.class).to(WorkflowsView.class);
        bind(WorkflowsPresenter.Model.class).to(WorkflowsModel.class);

        bind(WfDefinitionPresenter.class).in(Singleton.class);
        bind(WfDefinitionPresenter.View.class).to(WfDefinitionView.class);
        bind(WfDefinitionPresenter.Model.class).to(WfDefinitionModel.class);
        bind(WfDefinitionPresenter.Controls.class).to(WfDefinitionControls.class);

        bind(GraphvizPresenter.class).in(Singleton.class);
        bind(GraphvizPresenter.View.class).to(GraphvizView.class);
        bind(GraphvizPresenter.Model.class).to(GraphvizModel.class);
        bind(GraphvizPresenter.Controls.class).to(GraphvizControls.class);
        
        bind(SimulatorPresenter.class).in(Singleton.class);
        bind(SimulatorPresenter.View.class).to(SimulatorView.class);
        bind(SimulatorPresenter.Model.class).to(SimulatorModel.class);
        bind(SimulatorPresenter.Controls.class).to(SimulatorControls.class);

        bind(InputsPresenter.class).in(Singleton.class);
        bind(InputsPresenter.View.class).to(InputsView.class);
        bind(InputsPresenter.Model.class).to(SimulatorModel.class);

        bind(ActorPresenter.class).in(Singleton.class);
        bind(ActorPresenter.View.class).to(ActorView.class);
        bind(ActorPresenter.Model.class).to(SimulatorModel.class);



    }
 
}
