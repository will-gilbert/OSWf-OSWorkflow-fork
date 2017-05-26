package org.informagen.oswf.simulator.client.application;


// Application - GIN Injector
import org.informagen.oswf.simulator.client.di.Injector;

// Application - Events
import org.informagen.oswf.simulator.client.events.SwitchToPresenterEvent;

// SmartGWT
import com.smartgwt.client.util.SC;


//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasWidgets;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

public class Application  {

    private final Injector injector = GWT.create(Injector.class);

    public Application(final HasWidgets gwtFrame) {
                
        // Build the application scaffolding; Header, Footer, Accordion
        //    and content presenter panel
        // Fill the entire browser window with the application
        
        injector.getApplicationPresenter().setInjector(injector);
        gwtFrame.add(injector.getApplicationPresenter().getView());

        // Instance both the WF Listing, WF Definition, Graphviz and Simulator 
        //    presenters so that they can send/receive events between each other
        
        injector.getWorkflowsPresenter();
        injector.getWfDefinitionPresenter();
        injector.getGraphvizPresenter();
        injector.getSimulatorPresenter();
        
        // Start with the Workflow listings content
        injector.getEventBus().fireEvent(new SwitchToPresenterEvent("workflows.section"));
    }

}
