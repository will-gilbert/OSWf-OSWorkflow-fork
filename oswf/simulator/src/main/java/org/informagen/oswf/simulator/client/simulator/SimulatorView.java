package org.informagen.oswf.simulator.client.simulator;

// Presenter
import org.informagen.oswf.simulator.client.simulator.SimulatorPresenter;

// DTO
import org.informagen.oswf.simulator.dto.ProcessInstance;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.Step;
import org.informagen.oswf.simulator.dto.ProcessVariable;
import org.informagen.oswf.simulator.dto.Overview;

//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

// GWT - Events
import com.google.gwt.event.shared.EventBus;

// SmartGWT - Widgets, Layout
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

// SmartGWT - ListGrid and ListGrid events
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HasRecordClickHandlers;

// SmartGWT - Tab and TabSet
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;


// SmartGWT - Constants
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;


// Java - Collections
import java.util.List;

public class SimulatorView implements SimulatorPresenter.View {

    // Overall structure of this View
    //  - Container holds the content of the view will be returned
    //       as the 'Widget'
    //
    //  - UpperPanel holds the current Process Instance's Current Step
    //      History Step and Process Variables table along with the
    //      process state.
    //
    //  - LowerPanel holds the list of all Process Instances, Summary of
    //      wait time for each step for current workflow
    
    private final VLayout container = new VLayout();
    private final VLayout upperPanel = new VLayout();
    private final VLayout lowerPanel = new VLayout();


    // Upper Panel Tables
    private final ListGrid currentStepsGrid = new ListGrid();
    private final ListGrid historyStepsGrid = new ListGrid();
    private final ListGrid processVariablesGrid = new ListGrid();
    
    // Lower Panel Tables
    private final ListGrid processInstancesGrid = new ListGrid();
    private final ListGrid overviewGrid = new ListGrid();

    // Lower panel Tab 
    private final TabSet tabSet = new TabSet();
    private final Tab processInstancessTab = new Tab("Process Instances");
    private final Tab stepSummaryTab = new Tab("Step Summaries");
 
    private final Widget widget;

    private Long currentPIID = null;
    
    public SimulatorView() { 
        widget = buildWidget();
    }

    // SimulatorPresenter.View ------------------------------------------------------------------

    public void setCurrentPIID(long piid) {
        this.currentPIID = piid;
    }

    public void setProcessInstancesData(ListGridRecord[] data) {
        processInstancesGrid.setData(data);
        selectProcessInstance(currentPIID);
    }

    public void setCurrentStepsData(ListGridRecord[] data) {
        currentStepsGrid.setData(data);
    }

    public void setHistoryStepsData(ListGridRecord[] data) {
        historyStepsGrid.setData(data);
    }

    public void setProcessVariablesData(ListGridRecord[] data) {
        processVariablesGrid.setData(data);
    }               

    public void setWorkflowOverviewData(ListGridRecord[] data) {
        overviewGrid.setData(data);
    }

    public HasRecordClickHandlers getProcessInstanceSelector() {
        return processInstancesGrid;
    }

    public Long getSelectedProcessInstance() {
                
        if(processInstancesGrid.getSelectedRecords().length == 1) {
            ListGridRecord record = processInstancesGrid.getSelectedRecords()[0];
            currentPIID = record.getAttributeAsLong("piid");
            return currentPIID;
        } else
            return null;
    }

    public Widget asWidget() { 
        return widget; 
    }

    public void selectProcessInstance(Long piid) {
        
        if (piid == null)
            return;
            
        currentPIID = piid;

        for(ListGridRecord record : processInstancesGrid.getRecords()) {
            if(piid.equals(record.getAttributeAsLong("piid"))) {
                processInstancesGrid.selectRecord(record);
                break;
            }
        }
    }



    //-----------------------------------------------------------------------------------------

    private Widget buildWidget() {

        container.setWidth100();
        container.setHeight100();
        container.setBackgroundColor("#f6f6DE");
        container.setAnimateMembers(Boolean.FALSE);
    
        container.addMember(buildUpperPanel());
        container.addMember(buildLowerPanel());
        upperPanel.setShowResizeBar(true); 

        return container;
    }

    private final Canvas buildUpperPanel() {
        upperPanel.addMember(buildCurrentStepsGrid());
        upperPanel.addMember(buildHistoryStepsGrid());
        upperPanel.addMember(buildProcessVariablesGrid());
        return upperPanel;
    }

    private final Canvas buildLowerPanel() {
        
        tabSet.setWidth100();  
        tabSet.setHeight100();
        
        // Process Instances -------------------------------------
        VLayout tabLayout = new VLayout();
        tabLayout.setWidth100();
        tabLayout.setHeight100();
        tabLayout.addMember(buildProcessInstancesGrid());
        processInstancessTab.setPane(tabLayout);

        // Group Administrators ---------------------------------
        tabLayout = new VLayout();
        tabLayout.setWidth100();
        tabLayout.setHeight100();
        tabLayout.addMember(buildOverviewGrid());
        stepSummaryTab.setPane(tabLayout);

        // Add the tab panels in order, left to right -----------
        tabSet.addTab(processInstancessTab);
        tabSet.addTab(stepSummaryTab);
        
        lowerPanel.addMember(tabSet);
        return lowerPanel;
    }


    private final Widget buildCurrentStepsGrid() {
                  
        commonGridSettings(currentStepsGrid);
        
        ListGridField nameField = new ListGridField("name", "Current Steps");
        ListGridField statusField = new ListGridField("status", "Step Status", 120);
        ListGridField ownerField = new ListGridField("owner", "Owner", 120);
        ListGridField createdField = new ListGridField("created", "DateCreated", 120);
        ListGridField dueField = new ListGridField("due","Date Due", 120);

        currentStepsGrid.setFields(new ListGridField[] { 
            leftField(nameField), 
            leftField(statusField), 
            leftField(ownerField), 
            leftField(dueField), 
            leftField(createdField)
        });
   
        return currentStepsGrid;
    }

    private final Widget buildHistoryStepsGrid() {
                  
        commonGridSettings(historyStepsGrid);
        
        ListGridField nameField = new ListGridField("name", "History Steps");
        ListGridField statusField = new ListGridField("status", "Step Status", 120);
        ListGridField actorField = new ListGridField("actor", "Actor", 120);
        ListGridField actionField = new ListGridField("action","Action", 120);
        ListGridField finishedField = new ListGridField("finished","Date Finished", 120);

        historyStepsGrid.setFields(new ListGridField[] { 
            leftField(nameField), 
            leftField(statusField), 
            leftField(actorField), 
            leftField(actionField), 
            leftField(finishedField)
        });
   
        return historyStepsGrid;
    }


    private final Widget buildProcessVariablesGrid() {
                  
        commonGridSettings(processVariablesGrid);
        
        ListGridField nameField = new ListGridField("name", "Process Instance Variable", 160);
        ListGridField typeField = new ListGridField("type", "Data Type", 120);
        ListGridField valueField = new ListGridField("value", "Value");

        processVariablesGrid.setFields(new ListGridField[] { 
            leftField(nameField), 
            leftField(typeField), 
            leftField(valueField)
        });
   
        return processVariablesGrid;
    }


    private final Widget buildProcessInstancesGrid() {
                  
        commonGridSettings(processInstancesGrid);
        processInstancesGrid.setSelectionType(SelectionStyle.SINGLE);
        
        ListGridField nameField = new ListGridField("piid", "Process Instance ID", 120);
        ListGridField stateField = new ListGridField("state", "Process Instance State");
        ListGridField currentField = new ListGridField("current", "Current Steps", 100);
        ListGridField historyField = new ListGridField("history", "History Steps", 100);

        processInstancesGrid.setFields(new ListGridField[] { 
            integerField(nameField), 
            leftField(stateField), 
            integerField(currentField), 
            integerField(historyField)
        });
   
        return processInstancesGrid;
    }


    private final Widget buildOverviewGrid() {
                  
        commonGridSettings(overviewGrid);
        
        ListGridField idField = new ListGridField("id", "Step No.", 100);
        ListGridField nameField = new ListGridField("name", "Step Name");
        ListGridField currentField = new ListGridField("current", "Current", 100);
        ListGridField maxField = new ListGridField("max", "Max. Pending", 100);
        ListGridField avgField = new ListGridField("avg", "Avg. Pending", 100);
        ListGridField historyField = new ListGridField("history", "History", 100);

        // Supress the display of zero
        currentField.setDisplayField("current-display");
        currentField.setSortByDisplayField(false);

        // Supress the display of zero
        historyField.setDisplayField("history-display");
        historyField.setSortByDisplayField(false);
        
        // Supress the display of zero
        maxField.setDisplayField("max-display");
        maxField.setSortByDisplayField(false);
        
        // Supress the display of zero
        avgField.setDisplayField("avg-display");
        avgField.setSortByDisplayField(false);

        overviewGrid.setFields(new ListGridField[] { 
            integerField(idField), 
            leftField(nameField), 
            integerField(currentField), 
            integerField(maxField), 
            integerField(avgField),
            integerField(historyField)
        });
   
        return overviewGrid;
    }

    ListGridField leftField(ListGridField field) {
        field.setCellAlign(Alignment.LEFT); // Header Cell Alignment
        field.setAlign(Alignment.LEFT);   // Table Cell Alignment
        return field;
    }

    ListGridField integerField(ListGridField field) {
        field.setType(ListGridFieldType.INTEGER);
        field.setCellAlign(Alignment.CENTER);
        field.setAlign(Alignment.CENTER);
        return field;
    }


    void commonGridSettings(ListGrid listGrid) {
        listGrid.setWidth100();
        listGrid.setShowAllRecords(true);
        listGrid.setShowRowNumbers(false);
        listGrid.setCanExpandMultipleRecords(false);
        listGrid.setWrapCells(true);
        listGrid.setFixedRecordHeights(false);
        listGrid.setCanExpandRecords(false);
        listGrid.setCanMultiSort(true);
        listGrid.setCanResizeFields(true);
        listGrid.setAlternateRecordStyles(true);
        listGrid.setShowGroupSummary(true);
        listGrid.setShowHeaderMenuButton(false);
        listGrid.setSelectionType(SelectionStyle.NONE);
    }
}
