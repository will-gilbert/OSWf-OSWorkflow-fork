package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;
import org.informagen.oswf.simulator.client.application.Constants;
 
// Header & Footer
import org.informagen.oswf.simulator.client.application.HeaderPresenter;
import org.informagen.oswf.simulator.client.application.FooterPresenter;

// Application - GIN Injector
import org.informagen.oswf.simulator.client.di.Injector;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.DisplayAboutBoxEvent;
import org.informagen.oswf.simulator.client.events.DisplayInputsDialogEvent;
import org.informagen.oswf.simulator.client.events.DisplayActorDialogEvent;
import org.informagen.oswf.simulator.client.events.SwitchToPresenterEvent;
import org.informagen.oswf.simulator.client.events.InstallControlsWidgetEvent; 
import org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent;
import org.informagen.oswf.simulator.client.events.RequestReSTEvent; 

// GWT - Core,UI
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

// GWT - EventBus
import com.google.gwt.event.shared.EventBus;

// SmartGWT
import com.smartgwt.client.util.SC;

// Java - Collections
import java.util.Collection;

// Google Inject
import com.google.inject.Inject;

public class ApplicationPresenter implements Presenter, Constants {

    //=========================================================================================

    public interface View {
        void assemble(Widget north, Widget west, Widget south);
        void show(Widget widget);
        Widget asWidget();
    }
    
    public interface Model {
        void setCurrentWorkflow(String workflow);
    }
    
    //=========================================================================================

    final View view;
    final Model model;
    final EventBus eventBus;

    private Injector injector;
    
    final HeaderPresenter headerPresenter;
    

    @Inject
    public ApplicationPresenter(View view, Model model, EventBus eventBus,
                                HeaderPresenter headerPresenter, 
                                AccordionPresenter accordionPresenter, 
                                FooterPresenter footerPresenter) {
                                    
        this.view = view;
        this.model = model;
        this.eventBus = eventBus;
        this.headerPresenter = headerPresenter;

        view.assemble(
            headerPresenter.getView(),
            accordionPresenter.getView(), 
            footerPresenter.getView()
        );

        bindEvents();
    }


    void setInjector(Injector injector) {
        this.injector = injector;
    }

    public void show(Widget widget) {
        view.show(widget);
    }

    public Widget getView() {
       return view.asWidget();
    }

    // Bind (aka wire up) the Event Bus to the Front Controller
    // Implement each handler interface, which will be invoked via 'eventBus.handler<TYPE>.dispatch'
    // Each anonymous inner class should simply dispatch to a class method for handling of
    //  the event
    
    private void bindEvents() {

        // About Box Window
        eventBus.addHandler(DisplayAboutBoxEvent.TYPE,
            new HandlerFor.DisplayAboutBoxEvent() {
                public void processEvent(DisplayAboutBoxEvent event) {
                    injector.getAboutBoxPresenter().display();
                }
            }
        );

        eventBus.addHandler(WorkflowSelectionChangedEvent.TYPE,
            new HandlerFor.WorkflowSelectionChangedEvent() {
                public void processEvent(WorkflowSelectionChangedEvent event) {
                   model.setCurrentWorkflow(event.getWorkflowName());
                   updateTitle(event.getWorkflowName());
                }
            }
        );

        eventBus.addHandler(SwitchToPresenterEvent.TYPE,
            new HandlerFor.SwitchToPresenterEvent() {
                public void processEvent(SwitchToPresenterEvent event) {
                    switchToPresenter(event.sectionId);
                }
            }
        );

        eventBus.addHandler(DisplayInputsDialogEvent.TYPE,
            new HandlerFor.DisplayInputsDialogEvent() {
                public void processEvent(DisplayInputsDialogEvent event) {
                    injector.getInputsPresenter()
                            .display();
                }
            }
        );

        eventBus.addHandler(DisplayActorDialogEvent.TYPE,
            new HandlerFor.DisplayActorDialogEvent() {
                public void processEvent(DisplayActorDialogEvent event) {
                    injector.getActorPresenter()
                            .display();
                }
            }
        );

        eventBus.addHandler(RequestReSTEvent.TYPE,
            new HandlerFor.RequestReSTEvent() {
                public void processEvent(RequestReSTEvent event) {
                String url = GWT.getHostPageBaseURL() + event.getResourceName();
                Window.open(url, "_self", "");       
                }
            }
        );
  
    }


    //=========================================================================================
    // Event bus methods

    private void switchToPresenter(String presenterId) {

        Presenter presenter = null;
            
        if(WORKFLOW_SECTION_ID.equals(presenterId))
            presenter = injector.getWorkflowsPresenter();
        else if(DESCRIPTOR_SECTION_ID.equals(presenterId))
            presenter = injector.getWfDefinitionPresenter();
        else if(GRAPHVIZ_SECTION_ID.equals(presenterId))
            presenter = injector.getGraphvizPresenter();
        else if(SIMULATOR_SECTION_ID.equals(presenterId))
            presenter = injector.getSimulatorPresenter();

        if(presenter != null && presenter instanceof ContentPresenter)
            switchContent((ContentPresenter)presenter, presenterId);
    }

    
    private final void switchContent(ContentPresenter presenter, String presenterId) {
      
        // During development use 'null' to clear the currentcontent panel 
        //  when switch to unimplemented Contents
        
        if(presenter == null) {
           view.show(null); 
        } else {
           view.show(presenter.getView());
           eventBus.fireEvent(new InstallControlsWidgetEvent(presenterId, presenter.getControlsWidget()));
        }
    }

    private void updateTitle(String title) {
        headerPresenter.setTitle(title);
    }


}
