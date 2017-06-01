package org.informagen.oswf.simulator.client.simulator;

// Application
import org.informagen.oswf.simulator.client.application.Presenter;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;

//-----------------------------------------------------------------
// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Events
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// SmartGWT - Form events
import com.smartgwt.client.widgets.form.fields.events.HasKeyPressHandlers;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;

// SmartGWT - Types
import com.smartgwt.client.types.KeyNames;

// Dependency Injection
import com.google.inject.Inject;


public class ActorPresenter implements Presenter {

    // MVP Interfaces =========================================================================

    public interface View {
        String getActor();

        HasKeyPressHandlers getActorField();
        HasClickHandlers submitClickable();
        HasClickHandlers cancelClickable();

        void clear();
        void setVisible(boolean visible);
        Widget asWidget();
    }

    public interface Model {
        void setActor(String actor);
    }
        
    //=========================================================================================

    private final View view;
    private final Model model;
        
    @Inject
    public ActorPresenter(View view, Model model) {
        
        this.view = view;
        this.model = model;

        bindViewHandlers();        
    }

    public void display() {
        view.setVisible(true);
    }


    // Presenter interface -------------------------------------------------------------
   
    public Widget getView() { 
        return view.asWidget(); 
    }
    
    // ----------------------------------------------------------------------------------------
    private void bindViewHandlers() {

        view.getActorField().addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if(event.getKeyName().equals(KeyNames.ENTER))
                    doSubmit();
            }  
        }); 

        
        view.submitClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doSubmit();
            }
        });

        view.cancelClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                view.setVisible(false);
            }
        });

    }


    //=========================================================================================


    private void doSubmit() {
        model.setActor(view.getActor());
        view.setVisible(false);
    }

}
