package org.informagen.oswf.simulator.client.application;

import org.informagen.oswf.simulator.client.application.FooterPresenter;

// GWT - Widgets
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Anchor;

// SmartGWT - Widgets, Layout, Type
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.types.Alignment;


public class FooterView implements FooterPresenter.View {

    final ToolStripButton aboutButton = new ToolStripButton();

    private final Label worksWithLbl = new Label("Works with <strong>Firefox</strong>, <strong>Safari</strong> or <strong>Chrome</strong> browsers");
    private final String mailtoHref = "mailto:\"Will Gilbert\"<gilbert@informagen.com>?Cc=&Subject=OSWf Simulator";
    private final Anchor mailtoLink = new Anchor("E-Mail to Will Gilbert");
 
    final Widget widget;

    //-----------------------------------------------------------------------------------------
    
    public FooterView() {
        
        HLayout layout = new HLayout(0);
        
        ToolStrip toolStrip = new ToolStrip();
        toolStrip.setHeight(16);
        toolStrip.setWidth100();

        // Footer styling
        toolStrip.setBorder("1px solid black");
        toolStrip.setBackgroundColor("white");

        // "Works with...browsers" label
        worksWithLbl.setWrap(false);
        worksWithLbl.setAlign(Alignment.LEFT);
 
        // "E-Mail" link
        mailtoLink.setHref(mailtoHref);
        mailtoLink.setWordWrap(false);

        // Assemble footer
        toolStrip.addSpacer(16);
        toolStrip.addMember(worksWithLbl);
        toolStrip.addFill();
        toolStrip.addMember(mailtoLink);
        toolStrip.addSpacer(16);
       
        widget = toolStrip;
    }

    public Widget asWidget() {
        return widget;
    }

}
