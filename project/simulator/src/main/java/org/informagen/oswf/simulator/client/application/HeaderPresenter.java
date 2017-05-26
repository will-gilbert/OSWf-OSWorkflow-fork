package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;

// Application - Event
import org.informagen.oswf.simulator.client.events.DisplayAboutBoxEvent;

// GWT - Core
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

// SmartGWT - Click Events
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;


// Google Inject
import com.google.inject.Inject;


public class HeaderPresenter implements Presenter {

//---------------------------------------------------------------------------------------------

    public interface View {
        
        void setTitle(String string);
        
        HasClickHandlers getHomeButton();
        HasClickHandlers getAboutButton();
                
        Widget asWidget();
    }


//---------------------------------------------------------------------------------------------

    private View view;
    private EventBus eventBus;

    @Inject
    public HeaderPresenter(View view, EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;
                
        bindViewHandlers();
    }

    public Widget getView() {
       return view.asWidget();
    }


    void bindViewHandlers() {

        // Header buttons ===================================================================

        view.getHomeButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open("http://oswf.sourceforge.net/", "home", null);
            }
        });

        view.getAboutButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                 eventBus.fireEvent(new DisplayAboutBoxEvent());
            }
        });

    }

    public void setTitle(String title) {
        view.setTitle(title);
    }


}
