package org.informagen.oswf.simulator.client.graphviz;

import org.informagen.oswf.simulator.client.application.AccordionPresenter;

// GWT
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Image;

// SmartGWT - Widgets 
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

// SmartGWT - Constants
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.Overflow;


public class GraphvizView implements GraphvizPresenter.View {

    private VLayout layout = new VLayout(10);
    private final Widget widget;
    
    public GraphvizView() {
        widget = buildWidget();
    }

    // GraphvizPresenter.View ----------------------------------------------------------------

    public Widget asWidget() { 
        return widget; 
    }

    public void displayImage(String base64Image) {

        layout.removeMembers(layout.getMembers());
            
        if(base64Image != null) 
            layout.addMember(new Image(base64Image));
        
        layout.markForRedraw();
    }

    //-----------------------------------------------------------------------------------------

    private Widget buildWidget() {
        layout.setWidth100();
        layout.setHeight100();
        layout.setOverflow(Overflow.SCROLL);
        layout.setBackgroundColor("#f6f6DE");
        
        return layout;
    }
    
}
