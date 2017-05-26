package org.informagen.oswf.simulator.client.events;

// Application Event Interfaces
import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;


public class DisplayActorDialogEvent extends GwtEvent<HandlerFor.DisplayActorDialogEvent>  {

    public static Type<HandlerFor.DisplayActorDialogEvent> TYPE = new Type<HandlerFor.DisplayActorDialogEvent>();

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.DisplayActorDialogEvent> getAssociatedType() { return TYPE; }

    @Override
    protected void dispatch(HandlerFor.DisplayActorDialogEvent handler) { handler.processEvent(this); }
}
