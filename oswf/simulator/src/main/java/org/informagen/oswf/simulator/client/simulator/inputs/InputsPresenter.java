package org.informagen.oswf.simulator.client.simulator.inputs;

// Application
import org.informagen.oswf.simulator.client.application.Callback;
import org.informagen.oswf.simulator.client.application.SimpleCallback;
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

// Java Collections
import java.util.Map;

// Google Inject
import com.google.inject.Inject;


public class InputsPresenter implements Presenter {

    // MVP Interfaces =========================================================================

    public interface View {
        Map<String,String> getInputs();
        HasClickHandlers submitClickable();
        void setVisible(boolean visible);
        Widget asWidget();
    }

    public interface Model {
        void setInputs(Map<String,String> inputs);
    }
        
    //=========================================================================================

    private final View view;
    private final Model model;
        
    @Inject
    public InputsPresenter(View view, Model model) {
        this.view = view;
        this.model = model;

        bindViewHandlers();        
    }

    public void display() {
        view.setVisible(true);
    }


    // Presenter interface -------------------------------------------------------------
   
    public Widget getView() { return view.asWidget(); }
    
    // ----------------------------------------------------------------------------------------


    private void bindViewHandlers() {
        
        view.submitClickable().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                model.setInputs(view.getInputs());
                view.setVisible(false);
            }
        });

    }


    //=========================================================================================


}
