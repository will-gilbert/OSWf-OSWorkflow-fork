package org.informagen.oswf.simulator.client.simulator;

// Presenter
import org.informagen.oswf.simulator.client.simulator.ActorPresenter;

//-----------------------------------------------------------------

// GWT - Core, Widgets, Command
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

// SmartGWT - Widgets, Layout
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.HLayout;


// SmartGWT - Events
import com.smartgwt.client.widgets.events.HasClickHandlers;

// SmartGWT - Form
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.HasKeyPressHandlers;

// SmartGWT - Enumeration Types
import com.smartgwt.client.types.Alignment;

// Google Inject
import com.google.inject.Inject;

public class ActorView implements ActorPresenter.View {

    final Window window = new Window();
    final TextItem actorItem = new TextItem("actor", "Actor"); 

     // Button bar, use IButton to add then to the tab order
    final IButton submitBtn = new IButton("OK");
    final IButton cancelBtn = new IButton("Cancel");

    final Widget widget;
    
    public ActorView() {
        buildWindow();
        widget = buildWidget();
    }

    // ActorPresenter.View ------------------------------------------------------------------

    public String getActor() {
        Object value = actorItem.getValue();
        return (value == null) ? "" : ((String)value).trim();
    }
    
    public HasKeyPressHandlers getActorField() { 
        return actorItem; 
    }

    public HasClickHandlers submitClickable() { 
        return submitBtn; 
    }
    
    public HasClickHandlers cancelClickable() {
        return cancelBtn;
    }

    public void clear() {
        actorItem.setValue("");
    }

    public void setVisible(boolean visible) {
        if (visible) {
            focusInputField(actorItem);
            window.show();
        } else
            window.hide();
    }

    public Widget asWidget() { 
        return widget; 
    }

    //-----------------------------------------------------------------------------------------

    void buildWindow() {

        window.setTitle("Actor: Username, System or Resource");
        window.setWidth(300);
        window.setHeight(150);
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
         
        VLayout content = new VLayout(10);
        content.setBackgroundColor("beige");
        content.setLayoutMargin(10);

        content.addMember(createEntryForm());
        content.addMember(createButtonBar());
        
        window.addItem(content);
        
        return window;
    }

    Widget createEntryForm() {
        DynamicForm form = new DynamicForm(); 
        form.setWidth100();
        form.setNumCols(2);
        form.setAutoFocus(true);
        
        actorItem.setSelectOnFocus(true);
        actorItem.setWidth("50em");
        
        form.setFields(actorItem);

        return form;
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
