package org.informagen.oswf.simulator.client.events;

import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;


public class SwitchToPresenterEvent extends GwtEvent<HandlerFor.SwitchToPresenterEvent>  {

    public static Type<HandlerFor.SwitchToPresenterEvent> TYPE = new Type<HandlerFor.SwitchToPresenterEvent>();

    public String sectionId;
    
    public SwitchToPresenterEvent(String sectionId) {
        this.sectionId = sectionId;
    }

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.SwitchToPresenterEvent> getAssociatedType() { return TYPE; }

    @Override
    protected void dispatch(HandlerFor.SwitchToPresenterEvent handler) { handler.processEvent(this); }
}
