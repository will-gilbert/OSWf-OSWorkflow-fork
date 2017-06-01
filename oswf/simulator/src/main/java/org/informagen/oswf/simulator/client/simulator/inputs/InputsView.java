package org.informagen.oswf.simulator.client.simulator.inputs;

// Presenter
import org.informagen.oswf.simulator.client.simulator.inputs.InputsPresenter;


// GWT - Core, Widgets, Command
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

// SmartGWT - Widgets, Layout, Events
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickEvent;  
import com.smartgwt.client.widgets.events.ClickHandler;  

// SmartGWT - ToolStrip
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;

// SmartGWT - ListGrid and ListGrid events
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

// SmartGWT - Form
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.HasKeyPressHandlers;

// SmartGWT - Enumeration Types
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;

// Java Collections
import java.util.Map;
import java.util.HashMap;


public class InputsView implements InputsPresenter.View {

    final Window window = new Window();
    
    final ToolStrip toolStrip = new ToolStrip();
    final ToolStripButton clearAllButton = new ToolStripButton("Clear All");
    final ToolStripButton newButton = new ToolStripButton("Add Input");

    // Inputs list
    final ListGrid listGrid = new ListGrid(); 
    final ListGridField nameField = new ListGridField("name", "Name", 100);
    final ListGridField valueField = new ListGridField("value", "Value");


     // Button bar, use IButton to add then to the tab order
    final IButton submitBtn = new IButton("Done");
    final IButton cancelBtn = new IButton("Cancel");

    final Widget widget;
    
    public InputsView() {
        buildWindow();
        widget = buildWidget();
        bindViewHandlers();
    }

    // InputsPresenter.View ------------------------------------------------------------------

    public Map<String,String> getInputs() {
        Map<String,String> inputs = new HashMap<String,String>();
        
        listGrid.endEditing();
        
        for(ListGridRecord record : listGrid.getRecords())
            inputs.put(record.getAttribute("name"), record.getAttribute("value"));
                
        return inputs;
    }

    public HasClickHandlers submitClickable() { 
        return submitBtn; 
    }

    public void setVisible(boolean visible) {
        
        listGrid.endEditing();
        
        if (visible)
            window.show();
        else
            window.hide();
    }

    public Widget asWidget() { 
        return widget; 
    }


    void bindViewHandlers() {

        cancelBtn.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                setVisible(false);
            }  
        });  

        clearAllButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                listGrid.setData(new ListGridRecord[0]);
            }  
        });  

        newButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                listGrid.startEditingNew();  
            }  
        });  

    }
    
    //-----------------------------------------------------------------------------------------

    void buildWindow() {

        window.setTitle("Action Inputs as Name/Value Pairs");
        window.setWidth(400);
        window.setHeight(350);
        window.setIsModal(true);
        window.setShowModalMask(true);
        window.centerInPage();

        window.setShowCloseButton(false);
        window.setShowMinimizeButton(false);
        window.setShowResizer(false);
        window.setShowStatusBar(false);
        window.setShowMaximizeButton(false);
        window.setCanDragResize(false);
        
    }
 
    Widget buildWidget() {
        
        VLayout content = new VLayout();
        content.setBackgroundColor("beige");

        content.addMember(createToolStrip());
        content.addMember(createSpacer(5));
        content.addMember(createNameValueGrid());
        content.addMember(createSpacer(10));
        content.addMember(createButtonBar());
        
        window.addItem(content);
        
        return window;
    }

    private Widget createToolStrip() {

        toolStrip.setWidth100();
        toolStrip.addSpacer(4);    

        toolStrip.addMember(clearAllButton);
        toolStrip.addSeparator();
        toolStrip.addMember(newButton);
         
        return toolStrip;
    }

    Widget createNameValueGrid() {

        listGrid.setWidth100();
        listGrid.setHeight100();
        listGrid.setShowAllRecords(true);
        listGrid.setAlternateRecordStyles(true);
        listGrid.setCanDragSelectText(false);
        listGrid.setCanEdit(true);  
        listGrid.setCanRemoveRecords(true);  
        listGrid.setEditEvent(ListGridEditEvent.CLICK);  
        listGrid.setEmptyMessage("Use 'Add Input' to create a new Name/Value pair");

        listGrid.setFields(nameField, valueField);

        return listGrid;
    }

    Widget createSpacer(int space) {
        
        LayoutSpacer layoutSpacer = new LayoutSpacer();
        layoutSpacer.setHeight(10);

        return layoutSpacer;
    }

    Widget createButtonBar() {
        
        HLayout buttonBar = new HLayout(15);
        buttonBar.setLayoutMargin(15);
        buttonBar.setAlign(Alignment.RIGHT);
        buttonBar.addMember(cancelBtn);
        buttonBar.addMember(submitBtn);
        
        return buttonBar;
    }


    void focusInputField(final FormItem focusItem) {
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                // Reset the search field for next time
                focusItem.focusInItem();
            }
        });
        
    }

}


