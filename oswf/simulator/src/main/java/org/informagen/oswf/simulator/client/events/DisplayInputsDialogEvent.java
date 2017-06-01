package org.informagen.oswf.simulator.client.events;

// Application Event Interfaces
import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;


public class DisplayInputsDialogEvent extends GwtEvent<HandlerFor.DisplayInputsDialogEvent>  {

    public static Type<HandlerFor.DisplayInputsDialogEvent> TYPE = new Type<HandlerFor.DisplayInputsDialogEvent>();

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.DisplayInputsDialogEvent> getAssociatedType() { return TYPE; }

    @Override
    protected void dispatch(HandlerFor.DisplayInputsDialogEvent handler) { handler.processEvent(this); }
}
