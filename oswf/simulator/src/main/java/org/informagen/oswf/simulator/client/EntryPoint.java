package org.informagen.oswf.simulator.client;

// OSWf Simulator - Application
import org.informagen.oswf.simulator.client.application.Application;

// GWT - Core, UI
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.Window;

/**
 * Entry point for the application.  Name it 'EntryPoint' so that it pretty
 *   obvious this is where you start
 */
 
public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

	public void onModuleLoad() {
 
        Window.setTitle("OSWf Simulator");

        // Remove the 'eye candy' from the startup HTML page
        RootPanel.getBodyElement().removeChild(RootPanel.get("logo").getElement());
        RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
        
        // Create the 'Application' and inject the entire browser page as the GWT frame
        new Application(RootLayoutPanel.get());

	}

}
