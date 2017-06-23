package org.informagen.oswf.simulator.client.graphviz;

import org.informagen.oswf.simulator.client.application.SimpleCallback;

// DTO
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;

// GWT - Core and Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Widgets, Layouts, Events
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// SmartGWT - Form items & events
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.events.HasChangedHandlers;

// SmartGWT - Constants
import com.smartgwt.client.types.VerticalAlignment;

// Java - Collections
import java.util.List;
import java.util.LinkedHashMap;

public class GraphvizControls implements GraphvizPresenter.Controls {

    private final VStack layout = new VStack(5);

    private final Button dotDownloadButton = new Button("Download as 'Graphviz'");
    private final Button pngDownloadButton = new Button("Download as 'PNG'");
    private final Button svgDownloadButton = new Button("Download as 'SVG'");

    private final Widget widget;

    public GraphvizControls() {
        widget = buildWidget();
        mediate();
    }

    // GraphvizPresenter.Controls ------------------------------------------------------------

    public HasClickHandlers getDOTDownloadClickable() {
        return dotDownloadButton;
    }

    public HasClickHandlers getPNGDownloadClickable() {
        return pngDownloadButton;
    }

    public HasClickHandlers getSVGDownloadClickable() {
        return svgDownloadButton;
    }
    
    public void mediate() {}

    public Widget asWidget() {
        return widget;
    }

    // END: SimulatorPresenter.Controls ---------------------------------------------------

    private Widget buildWidget() {
        
        layout.setLayoutMargin(5);
        
        layout.addMember(buildButton(dotDownloadButton));
        layout.addMember(buildButton(pngDownloadButton));
        layout.addMember(buildButton(svgDownloadButton));
        
        return layout;
    }

    private Widget buildButton(Button button) {
        button.setWidth100();
        return button;
    }


}
