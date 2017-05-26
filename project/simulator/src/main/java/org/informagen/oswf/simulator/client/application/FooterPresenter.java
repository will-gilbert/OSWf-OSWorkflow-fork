package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;

// GWT
import com.google.gwt.user.client.ui.Widget;

// Google Inject
import com.google.inject.Inject;

public class FooterPresenter implements Presenter {

//---------------------------------------------------------------------------------------------

    public interface View {
        Widget asWidget();
    }

//---------------------------------------------------------------------------------------------

    private final View view;

    @Inject
    public FooterPresenter(View view) {
        this.view = view;
    }

    public Widget getView() {
       return view.asWidget();
    }

}
