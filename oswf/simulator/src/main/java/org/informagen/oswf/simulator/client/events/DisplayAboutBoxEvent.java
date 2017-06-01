package org.informagen.oswf.simulator.client.events;

// Application Event Interfaces
import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;


public class DisplayAboutBoxEvent extends GwtEvent<HandlerFor.DisplayAboutBoxEvent>  {

    public static Type<HandlerFor.DisplayAboutBoxEvent> TYPE = new Type<HandlerFor.DisplayAboutBoxEvent>();

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.DisplayAboutBoxEvent> getAssociatedType() { return TYPE; }

    @Override
    protected void dispatch(HandlerFor.DisplayAboutBoxEvent handler) { handler.processEvent(this); }
}
