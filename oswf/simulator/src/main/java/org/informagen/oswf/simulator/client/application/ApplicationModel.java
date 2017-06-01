package org.informagen.oswf.simulator.client.application;


// Presenter
import org.informagen.oswf.simulator.client.application.ApplicationPresenter;

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// Java - Collections
import java.util.List;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// Java Collections
import java.util.List;
import java.util.ArrayList;

// Google Inject
import com.google.inject.Inject;

public class ApplicationModel implements ApplicationPresenter.Model {

    private String currentWorkflow;

    @Inject
    public ApplicationModel() {}

    public void setCurrentWorkflow(String currentWorkflow) {
        this.currentWorkflow = currentWorkflow;
    }

    public String getCurrentWorkflow() {
        return currentWorkflow;
    }
}
