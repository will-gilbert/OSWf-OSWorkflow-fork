package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.HeaderPresenter;


// GWT
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Widgets
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;

// SmartGWT - Layouts
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

// SmartGWT - Events
import com.smartgwt.client.widgets.events.HasClickHandlers;

// SmartGWT - Tool Strip
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

// SmartGWT - Enumerated Types
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;

public class HeaderView implements HeaderPresenter.View {

    final Label title = new Label();
 
    final Widget widget;
    final ImgButton homeButton = new ImgButton();
    final ToolStripButton aboutButton = new ToolStripButton();

    //-----------------------------------------------------------------------------------------
      
    public HeaderView() {
        widget = buildWidget();
    }

    // HeaderPresenter.View -------------------------------------------------------------------

    public void setTitle(String string) {
        title.setContents(string);
    }
    
    public HasClickHandlers getHomeButton() {
        return homeButton;
    }

    public HasClickHandlers getAboutButton() {
        return aboutButton;
    }

    public Widget asWidget() {
        return widget;
    }

    //-----------------------------------------------------------------------------------------
        
    private Widget buildWidget() {
           
        VLayout layout = new VLayout();
        layout.setWidth100();
        
        ToolStrip toolStrip = new ToolStrip();
        toolStrip.setHeight(59);
        toolStrip.setWidth100();
        
        homeButton.setSrc("OSWf-Square.png");
        homeButton.setWidth(49);
        homeButton.setHeight(49);
        homeButton.setValign(VerticalAlignment.BOTTOM);
        homeButton.setShowRollOver(false);
        homeButton.setShowDownIcon(false);
        homeButton.setShowDown(false);
        homeButton.setPrompt("Click to go to the OSWf Home Page");
      
        // Application title; provide enough space so it doesn't have to wrap
        title.setStyleName("oswf-Title");
        title.setWidth(400);

        // From here to the 'About' button use a springy filler
        aboutButton.setTitle("About");
        aboutButton.setIcon("icons/about.png");

        // Assemble the header
        toolStrip.addSpacer(6);
        toolStrip.addMember(homeButton);
        toolStrip.addSpacer(100);
        toolStrip.addMember(title);
        toolStrip.addFill();
        toolStrip.addButton(aboutButton);
        toolStrip.addSpacer(6);

        // Finished building the top bar
        layout.addMember(toolStrip);
        
        return layout;
    }
    
}
