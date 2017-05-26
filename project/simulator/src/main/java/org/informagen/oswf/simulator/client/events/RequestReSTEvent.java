package org.informagen.oswf.simulator.client.events;

import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.URL;


public class RequestReSTEvent extends GwtEvent<HandlerFor.RequestReSTEvent>  {

    public static Type<HandlerFor.RequestReSTEvent> TYPE = new Type<HandlerFor.RequestReSTEvent>();

    private String command;
    private String resourceName;

    public RequestReSTEvent(String command, String plainResourceName) {
        this.command = command;
        this.resourceName = URL.encode(plainResourceName);
    }

    public String getCommand() {
        return command;
    }

    public String getResourceName() {
        return resourceName;
    }

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.RequestReSTEvent> getAssociatedType() {return TYPE;}
    
    @Override
    protected void dispatch(HandlerFor.RequestReSTEvent handler) {handler.processEvent(this);}
}
