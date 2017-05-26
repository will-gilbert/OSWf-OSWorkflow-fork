package org.informagen.oswf.simulator.client.wfdefinition;

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

public class WfDefinitionControls implements WfDefinitionPresenter.Controls {

    private final VStack layout = new VStack(5);

    private final RadioGroupItem colorizeItem = new RadioGroupItem();
    private final Button downloadButton = new Button("Download");

    private final Widget widget;

    public WfDefinitionControls() {
        widget = buildWidget();
        mediate();
    }

    // WfDefinitionPresenter.Controls ------------------------------------------------------------

    public String getRendering() {
        return colorizeItem.getValue().toString();
    }

    public HasChangedHandlers getRenderer() {
        return colorizeItem;
    }

    public HasClickHandlers getDownloadClickable() {
        return downloadButton;
    }
    
    public void mediate() {}

    public Widget asWidget() {
        return widget;
    }

    // END: SimulatorPresenter.Controls ---------------------------------------------------

    private Widget buildWidget() {
        
        layout.setLayoutMargin(5);
        
        layout.addMember(buildColorizerSelector());
        layout.addMember(createSpacer(10));
        layout.addMember(buildDownloadButton());
        
        return layout;
    }


    Widget buildColorizerSelector() {
        
        DynamicForm form = new DynamicForm(); 
        form.setWidth100();
        form.setNumCols(2);

        LinkedHashMap<String, String> styleMap = new LinkedHashMap<String, String>();  
        styleMap.put("ColorizedOSWf", "OSWf Colorizer");  
        styleMap.put("ColorizedXML",  "XML Colorizer");  
        styleMap.put("XMLasHTML",     "No Colorizer");  

        // Default to 'Colorized as OSWf
        colorizeItem.setDefaultValue("ColorizedOSWf");  
        colorizeItem.setShowTitle(false);  
        colorizeItem.setValueMap(styleMap); 
        colorizeItem.setColSpan(2); 
        colorizeItem.setWidth("140px");
                               
        form.setFields(colorizeItem);
        return form;
    }

    Widget createSpacer(int size) {
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setHeight(size);
        return spacer;
    }

    Widget buildDownloadButton() {
        downloadButton.setWidth100();
        return downloadButton;
    }

}
