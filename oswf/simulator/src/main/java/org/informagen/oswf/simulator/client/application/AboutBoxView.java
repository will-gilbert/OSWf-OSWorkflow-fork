package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.AboutBoxPresenter;

// GWT - Widgets
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Widgets
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.HTMLFlow;

//SmartGWT - Layout
import com.smartgwt.client.widgets.layout.VLayout;


public class AboutBoxView implements AboutBoxPresenter.View {

    final Window window;
    final Widget widget;
      
    public AboutBoxView() {
        window = buildWindow();
        widget = buildWidget();
    }

    // AboutBoxPresenter.View -------------------------------------------------------------------
   
    public void setVisible(boolean visible) {
        
        if (visible) {
            window.restore();
            window.show();
        } else
            window.hide();
    }

    public Widget asWidget() {
        return widget;
    }

    //-----------------------------------------------------------------------------------------

    private Window buildWindow() {
        
        Window window = new Window();
        window.setTitle("About OSWf Simulator");
        window.setWidth(400);
        window.setHeight(225);
        window.setIsModal(true);
        window.setShowModalMask(true);
        window.centerInPage();

        window.setShowMinimizeButton(false);
        window.setShowResizer(false);
        window.setShowStatusBar(true);
        window.setShowMaximizeButton(false);
        window.setCanDragResize(true);  

        return window;
    }

    private Widget buildWidget() {
        VLayout content = new VLayout(10);
        content.addMember(createTechnologies());
        window.addItem(content);
        return window;
    }

    Widget createTechnologies() {
        
        HTMLFlow htmlPane = new HTMLFlow();  
        htmlPane.setWidth100();  
        htmlPane.setShowEdges(false);  

        String contents = "This application was built using the following Open Source technologies: " +  
                "<ul>"+
                "<li><a href='http://code.google.com/webtoolkit/' target='_blank'>Google Web Toolkit (GWT) v2.8.1</a></li>" +  
                "<li><a href='http://www.smartclient.com/' target='_blank'>SmartGWT v5.0 from Isomorphic Software</a></li>" + 
                "<li>Also Hibernate/EJB3, Maven and Google Guice &amp; GIN</li>" +  
                "</ul>";  
        htmlPane.setContents(contents);       
        
       return htmlPane; 
    }


}
