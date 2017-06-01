package org.informagen.oswf.simulator.client.events;

// Application Event Interfaces
import org.informagen.oswf.simulator.client.events.HandlerFor;

// GWT
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.shared.GwtEvent;

public class InstallControlsWidgetEvent extends GwtEvent<HandlerFor.InstallControlsWidgetEvent> {

    public static Type<HandlerFor.InstallControlsWidgetEvent> TYPE = new Type<HandlerFor.InstallControlsWidgetEvent>();

    public final String sectionId;
    public final Widget widget;
    
    public InstallControlsWidgetEvent(String sectionId, Widget widget) {
        this.sectionId = sectionId;
        this.widget = widget;
    }
  
    public Widget getControlsWidget() {
        return widget;
    }
       
    // GwtEvent abstract methods --------------------------------------------------------------
    @Override 
    public Type<HandlerFor.InstallControlsWidgetEvent> getAssociatedType() { return TYPE; }
    
    @Override 
    protected void dispatch(HandlerFor.InstallControlsWidgetEvent handler) { handler.processEvent(this);}
}
