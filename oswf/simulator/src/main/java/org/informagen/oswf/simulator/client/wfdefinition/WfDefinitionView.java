package org.informagen.oswf.simulator.client.wfdefinition;

import org.informagen.oswf.simulator.client.application.AccordionPresenter;

// GWT
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Widget
import com.smartgwt.client.widgets.HTMLPane; 
import com.smartgwt.client.types.Overflow;
 
public class WfDefinitionView implements WfDefinitionPresenter.View {

    private final HTMLPane descriptorDisplay = new HTMLPane();
    private final Widget widget;
    
    private String title;

    public WfDefinitionView() {
        widget = buildWidget();
    }

    // WfDefinitionPresenter.View ----------------------------------------------------------------

    public Widget asWidget() { 
        return widget; 
    }

    public void displayDescriptor(String descriptor) { 
        descriptorDisplay.setContents(descriptor); 
    }

    //-----------------------------------------------------------------------------------------

    private Widget buildWidget() {
        descriptorDisplay.setBackgroundColor("#f5f4ec");
        descriptorDisplay.setOverflow(Overflow.SCROLL);
        
        return descriptorDisplay;
    }
    
}
