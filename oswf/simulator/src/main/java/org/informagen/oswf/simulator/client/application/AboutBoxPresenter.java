package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;

// Application - Event
import org.informagen.oswf.simulator.client.events.DisplayAboutBoxEvent;


// Google - Dependency Injection
import com.google.inject.Inject;

public class AboutBoxPresenter {

//---------------------------------------------------------------------------------------------

    public interface View {
        void setVisible(boolean visible);
    }

//---------------------------------------------------------------------------------------------

    private View view;

    @Inject
    public AboutBoxPresenter(View view) {
        this.view = view;
    }

    void display() {
        view.setVisible(true);
    }


}
