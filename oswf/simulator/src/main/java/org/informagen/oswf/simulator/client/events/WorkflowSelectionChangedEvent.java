package org.informagen.oswf.simulator.client.events;

import org.informagen.oswf.simulator.client.events.HandlerFor;

import com.google.gwt.event.shared.GwtEvent;


public class WorkflowSelectionChangedEvent extends GwtEvent<HandlerFor.WorkflowSelectionChangedEvent>  {

    public static Type<HandlerFor.WorkflowSelectionChangedEvent> TYPE = new Type<HandlerFor.WorkflowSelectionChangedEvent>();

    private String workflowName;

    public WorkflowSelectionChangedEvent(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    // GwtEvent abstract methods --------------------------------------------------------------
    @Override
    public Type<HandlerFor.WorkflowSelectionChangedEvent> getAssociatedType() {return TYPE;}
    
    @Override
    protected void dispatch(HandlerFor.WorkflowSelectionChangedEvent handler) {handler.processEvent(this);}
}
