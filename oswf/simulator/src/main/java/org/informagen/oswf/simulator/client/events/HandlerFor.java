package org.informagen.oswf.simulator.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface HandlerFor {


    // Dialog boxes ---------------------------------------------------------------------------
    
    public interface DisplayAboutBoxEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.DisplayAboutBoxEvent event);
    }
    
    public interface DisplayInputsDialogEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.DisplayInputsDialogEvent event);
    }
    
    public interface DisplayActorDialogEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.DisplayActorDialogEvent event);
    }


    // Content Switch Events ------------------------------------------------------------------

    public interface SwitchToPresenterEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.SwitchToPresenterEvent event);
    }

    public interface InstallControlsWidgetEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.InstallControlsWidgetEvent event);
    }

    public interface WorkflowSelectionChangedEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.WorkflowSelectionChangedEvent event);
    }

    public interface RequestReSTEvent extends EventHandler {
        void processEvent(org.informagen.oswf.simulator.client.events.RequestReSTEvent event);
    }


            
}
