package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.Presenter;
import org.informagen.oswf.simulator.client.application.Constants;

// Application - Events
import org.informagen.oswf.simulator.client.events.HandlerFor;
import org.informagen.oswf.simulator.client.events.SwitchToPresenterEvent;
import org.informagen.oswf.simulator.client.events.InstallControlsWidgetEvent; 


// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

// SmartGWT - UI
import com.smartgwt.client.widgets.Canvas;

// SmartGWT - Layout
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

// SmartGWT - SectionStack Events
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;

// SmartGWT - Enumerations
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.types.Overflow;

// Google Inject
import com.google.inject.Inject;


public class AccordionPresenter implements Presenter, Constants {


    private final EventBus eventBus;
    private final SectionStack accordion = new SectionStack();

    @Inject
    public AccordionPresenter(EventBus eventBus) {
        this.eventBus = eventBus;
                
        buildAccordion();
        
        accordion.addSection(createAccordionSection(
            WORKFLOW_SECTION_NAME, 
            WORKFLOW_SECTION_ID, 
            WORKFLOW_SECTION_COLOR
        ));
        
        accordion.addSection(createAccordionSection(
            DESCRIPTOR_SECTION_NAME, 
            DESCRIPTOR_SECTION_ID, 
            DESCRIPTOR_SECTION_COLOR
        ));
        
        accordion.addSection(createAccordionSection(
            GRAPHVIZ_SECTION_NAME, 
            GRAPHVIZ_SECTION_ID, 
            GRAPHVIZ_SECTION_COLOR
        ));
        
        accordion.addSection(createAccordionSection(
            SIMULATOR_SECTION_NAME, 
            SIMULATOR_SECTION_ID, 
            SIMULATOR_SECTION_COLOR
        ));

        bindHandlers();
        bindEvents();
        
    }

    // Presenter interface --------------------------------------------------------------------
    
    public Widget getView() {
        return accordion;
    }
    
    public Widget getWidget() {
        return accordion;
    }

    //-----------------------------------------------------------------------------------------

    private void buildAccordion() {
              
        // Accordion Behavior 
        accordion.setVisibilityMode(VisibilityMode.MUTEX);
        accordion.setOverflow(Overflow.HIDDEN);
        accordion.setShowExpandControls(Boolean.FALSE);
        accordion.setShowResizeBar(Boolean.TRUE);
        accordion.setCanReorderSections(Boolean.FALSE);
        accordion.setCanResizeSections(Boolean.FALSE);
        
        // Animation currently doesn't appear to work
        accordion.setAnimateSections(Boolean.TRUE);
        accordion.setAnimateMemberTime(300);
        
        // Accordion Size 
        accordion.setWidth(ACCORDION_BUTTON_WIDTH + 10);  
        accordion.setHeight100();
    }

    private void bindHandlers() {

        // Accordion click handler
        
        accordion.addSectionHeaderClickHandler(
        
            new SectionHeaderClickHandler() {
                public void onSectionHeaderClick(SectionHeaderClickEvent event) {
                   
                   // If a section is already open, ignore this click event
                   if(accordion.sectionIsExpanded(event.getSection().getName())) {
                       event.cancel();
                       return;
                   }
                   
                   switchToPresenter(event.getSection().getName());
                }
            }
        );
    }
    
    private void bindEvents() {
 
        eventBus.addHandler(InstallControlsWidgetEvent.TYPE,
            new HandlerFor.InstallControlsWidgetEvent() {
                public void processEvent(InstallControlsWidgetEvent event) {
                    installControlPanel(event.sectionId, event.widget);
                }
            }
        );

    }

   void switchToPresenter(String sectionId) {
        eventBus.fireEvent(new SwitchToPresenterEvent(sectionId));
    }


    private void installControlPanel(String sectionId, Widget widget) {
       
        // This section is not in this accordion
        if(accordion.getSection(sectionId) == null)
            return;
            
        Canvas[] items = accordion.getSection(sectionId).getItems();       

        VLayout container = (VLayout)items[0];
        
        for(Canvas member : container.getMembers()) {
            container.removeMember(member);
        }

        if(widget != null)
            container.addMember(widget); 
    }

     private SectionStackSection createAccordionSection(String title, String id, String color) {
        
        SectionStackSection sectionStackSection = new SectionStackSection(
            new StringBuilder()
                .append("<span style='font-size:larger;font-weight:bold;'>")
                .append(title)
                .append("</span>")
                .toString()
          ); 

        sectionStackSection.setName(id);
        sectionStackSection.addItem(createStackSection(color));

        return sectionStackSection;
    }


    private Canvas createStackSection(String color) {
        VLayout stackSection = new VLayout(5);
        stackSection.setBackgroundColor(color);
        stackSection.setAlign(VerticalAlignment.TOP);
        stackSection.setLayoutTopMargin(5);
        stackSection.setLayoutLeftMargin(5);
        stackSection.setWidth100();  
        stackSection.setHeight100();        
        return stackSection;
    }


}
