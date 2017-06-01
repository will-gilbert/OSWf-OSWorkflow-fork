package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;

// GWT
import com.google.gwt.user.client.ui.Widget;

public abstract interface ContentPresenter extends Presenter {

    // Can return 'null' meaning no controls widget used
    Widget getControlsWidget();

}
