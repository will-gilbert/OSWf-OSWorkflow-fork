package org.informagen.oswf.simulator.client.application;


// Presenter
import org.informagen.oswf.simulator.client.application.ApplicationPresenter;


//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

// SmartGWT - UI
import com.smartgwt.client.widgets.Canvas;

// SmartGWT - Layout
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;


// GWT - Constants
import com.google.gwt.dom.client.Style.Unit;

//GWT - UiBinder Support
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

public class ApplicationView implements ApplicationPresenter.View {

    private Widget widget;
    private VLayout contentContainer;
  
    public void assemble(Widget header, Widget accordion, Widget footer) {
        
        // Create the content sections ---------------------------------------------------------
        
        contentContainer = new VLayout();
        contentContainer.setWidth100();
        contentContainer.setHeight100();

        // Assemble the inner UI; Body: Accordian & Content -----------------------

        HLayout bodyContainer = new HLayout();
        bodyContainer.setWidth100();
        bodyContainer.setHeight100();
        bodyContainer.addMember(accordion);
        bodyContainer.addMember(contentContainer);

        //Assemble the outer UI; Header, Body, Footer ------------------------
        
        VLayout layoutPanel = new VLayout();
        layoutPanel.setWidth100();
        layoutPanel.setHeight100();
        
        layoutPanel.addMember(header);
        layoutPanel.addMember(bodyContainer);
        layoutPanel.addMember(footer);

        widget = layoutPanel;
    }


    public Widget asWidget() {
        return widget;
    }

    public void add(Widget widget) {
        contentContainer.addMember(widget);
    }


    public void show(Widget widget) {
        
        hideAllContentPanels();
        
        if ( contentContainer.hasMember((Canvas)widget) == false )
            contentContainer.addMember(widget);
        
        contentContainer.showMember((Canvas)widget);
    }

    public void hideAllContentPanels() {
        
        // NB: contentContainer.clear() didn't work too well.  This is much
        //   better.  It gives the illusion of instant switching between panels
        
        Canvas[] members = contentContainer.getMembers();
        for (Canvas member : members)
            contentContainer.hideMember(member);
    }
    
    // public void show(Widget widget) {
    //     
    //     if(widget == null)
    //         contentContainer.clear(); 
    //     else 
    //         contentContainer.setWidget(widget);
    // }
    // 
    // 
    // public void hideAllContentPanels() {
    //     
    //     // NB: contentContainer.clear() didn't work too well.  This is much
    //     //   better.  It gives the illusion of instant switching between panels
    //     
    //     // Canvas[] members = contentContainer.getMembers();
    //     // for (Canvas member : members)
    //     //     contentContainer.hideMember(member);
    // }



}
