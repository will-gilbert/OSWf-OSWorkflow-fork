package org.informagen.oswf.simulator.client.simulator;

import org.informagen.oswf.simulator.client.application.SimpleCallback;

// DTO
import org.informagen.oswf.simulator.dto.Action;
import org.informagen.oswf.simulator.dto.StepActions;

// GWT - Core and Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// SmartGWT - Widgets, Layouts, Events
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ClickEvent;

// SmartGWT - Form items & events
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectOtherItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.events.HasChangedHandlers;

// SmartGWT - Constants
import com.smartgwt.client.types.Alignment;

// Java - Collections
import java.util.List;

public class SimulatorControls implements SimulatorPresenter.Controls {

    private final VStack layout = new VStack();
    private final Button inputsButton = new Button("Set Action Inputs");
    private final SelectOtherItem actorSelector = new SelectOtherItem("actor");

    private VStack initialActionsPanel = new VStack(5);
    private VStack stepActionsPanel = new VStack(5);

    private final Widget widget;

    public SimulatorControls() {
        widget = buildWidget();
        mediate();
    }

    // SimulatorPresenter.Controls ------------------------------------------------------------

    @Override
    public HasChangedHandlers actorSelector() {
        return actorSelector;
    }

    public String getActor() {
        return actorSelector.getValueAsString();
    }

    public HasClickHandlers getInputsClickable() {
        return inputsButton;
    }

    public void clearInitialActions() {
        initialActionsPanel.removeMembers(initialActionsPanel.getMembers());
    }

    public void clearStepActions() {
        stepActionsPanel.removeMembers(stepActionsPanel.getMembers());
    }

    public void mediate() {
        enableButton(inputsButton, true);
    }

    public Widget asWidget() {
        return widget;
    }

    // END: SimulatorPresenter.Controls ---------------------------------------------------

    private Widget buildWidget() {
        
        layout.setLayoutMargin(5);
        inputsButton.setWidth100();

        layout.addMember(createSpacer(5));
        layout.addMember(inputsButton);
        layout.addMember(createSpacer(10));
        layout.addMember(createActorSelector());
        layout.addMember(initialActionsPanel);
        layout.addMember(stepActionsPanel);
        
        return layout;
    }

    Widget createSpacer(int space) {
        
        LayoutSpacer layoutSpacer = new LayoutSpacer();
        layoutSpacer.setHeight(10);

        return layoutSpacer;
    }

    void enableButton(Button button, boolean enabled) {
        if(enabled) 
            button.enable();
        else
            button.disable();
    }


    public void installInitialActions(List<Action> actions, final SimpleCallback<Integer> callback) {
                 
        clearInitialActions();

        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setHeight(10);
        initialActionsPanel.addMember(spacer);

        Label label = new Label("Workflow<br>Initial Actions");
        label.setStyleName("oswf-InitalActions-label");
        label.setHeight(24);
        initialActionsPanel.addMember(label);
        
        for(Action action : actions) {
           
            // Create the button and add it to the panel
            String actionName = new StringBuilder()
                .append("<span style='font-size:smaller;'>")
                .append("InitialAction: ").append(action.actionId)
                .append("</span>").append("<br>")
                .append(action.name)
            .toString();
                
            Button button = new Button(actionName);
            button.setStyleName("oswf-InitialAction-button");
            button.setHeight(30);
            button.setWidth100();
            initialActionsPanel.addMember(button);
            
            // Bind the button to the Presenter via a SimpleCallback
            //  which returns the actionId
            
            final Integer actionId = action.actionId;
            
            button.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    callback.execute(actionId);
                }
            });
        }
        
    }

    public void installStepActions(List<StepActions> actions, final SimpleCallback<Integer> callback) {
        
       clearStepActions();
 
       LayoutSpacer spacer = new LayoutSpacer();
       spacer.setHeight(10);
       stepActionsPanel.addMember(spacer);
       
        for(StepActions stepActions : actions) {
            
            // Add the Step name to the panel
            String stepName = new StringBuilder()
                .append("<span style='font-size:smaller;'>")
                .append("Step: ").append(stepActions.getStepId())
                .append("</span>").append("<br>")
                .append(stepActions.getStepName())
            .toString();

            Label stepLabel = new Label(stepName);
            stepLabel.setStyleName("oswf-StepName-label");
            stepLabel.setHeight(24);
            stepActionsPanel.addMember(stepLabel);
            
            // List the Step Action beneath the Step Name, then add each action the
            //  way which add the Initial Action above
            for(Action action : stepActions.getActions()) {
                
                String actionName = new StringBuilder()
                    .append("<span style='font-size:smaller;'>")
                    .append("Action: ").append(action.actionId)
                    .append("</span>").append("<br>")
                    .append(action.name)
                .toString();

                Button button = new Button(actionName);
                button.setStyleName("oswf-StepAction-button");
                button.setHeight(30);
                button.setWidth100();
                stepActionsPanel.addMember(button);
                
                final Integer actionId = action.actionId;
                button.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        callback.execute(actionId);
                    }
                });
            }
        }
    }


    Widget createActorSelector() {
        
        DynamicForm form = new DynamicForm(); 
        form.setWidth100();
        form.setNumCols(2); 
        
        // Hacky way to create a label in a form....
        StaticTextItem actorLabel = new StaticTextItem("actor-label");
        actorLabel.setShowTitle(false);
        actorLabel.setColSpan(2);
        actorLabel.setAlign(Alignment.LEFT);
        actorLabel.setValue("Choose Actor:");
        
        String[] names = {
            "Simulator", 
            "Alice", 
            "Bob", 
            "Charlie", 
            "Dave", 
            "E-Mail System"
        };

        actorSelector.setWidth(130);
        actorSelector.setShowTitle(false);
        actorSelector.setValueMap(names);
        actorSelector.setMultiple(false);
                       
        form.setFields(actorLabel, actorSelector);
        
        // Preselect the first name
        actorSelector.setValue(names[0]);
        
        return form;
    }




}
