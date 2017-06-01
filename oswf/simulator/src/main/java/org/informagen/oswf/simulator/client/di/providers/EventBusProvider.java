package org.informagen.oswf.simulator.client.di.providers;

// GWT - Event Bus
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

// Google Dependency Injection
import com.google.inject.Provider;
  
public class EventBusProvider implements Provider<EventBus> {

    // Global application singleton
    private static final EventBus eventBus = new SimpleEventBus();

    @Override
    public EventBus get() {
        return eventBus;
    }
}
