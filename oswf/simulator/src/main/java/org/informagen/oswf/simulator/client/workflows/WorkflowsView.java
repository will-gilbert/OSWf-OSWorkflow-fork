package org.informagen.oswf.simulator.client.workflows;

// OSWf Simulator - DTO
import org.informagen.oswf.simulator.client.workflows.WorkflowsPresenter;
import org.informagen.oswf.simulator.dto.ProcessDescriptorSummary;

import com.smartgwt.client.widgets.layout.VLayout;
import com.google.gwt.user.client.ui.Widget;


// SmartGWT - ListGrid and ListGrid events
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HasRecordClickHandlers;

// SmartGWT - Enumerations
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;

// Java - Collections
import java.util.List;


//-----------------------------------------------------------------

// GWT - Core, Widgets
import com.google.gwt.core.client.GWT;

// GWT - Events
import com.google.gwt.event.shared.EventBus;


// Google Inject
import com.google.inject.Inject;

public class WorkflowsView implements WorkflowsPresenter.View {

    private final ListGrid listGrid = new ListGrid();
    private final Widget widget;
    
    @Inject
    public WorkflowsView() {
        widget = buildWidget();
    }

    // WorkflowsPresenter.View ------------------------------------------------------------------

    public HasRecordClickHandlers getWorkflowSelector() {
        return listGrid;
    }
    
    public String getSelectedWorkflow() {
        
        if(listGrid.getSelectedRecords().length == 1) {
            ListGridRecord record = listGrid.getSelectedRecords()[0];
            return record.getAttributeAsString("name");
        } else
            return null;
    }

    //public HasData<ProcessDescriptorSummary> display() { return table; }
    public Widget asWidget() { return widget; }
    //public SingleSelectionModel<ProcessDescriptorSummary> selectionModel() { return selectionModel; }


    public void setData(ListGridRecord[] data) {
        listGrid.setData(data);
    }

    //-----------------------------------------------------------------------------------------
 
    Widget buildWidget() {

        VLayout container = new VLayout();
        container.setWidth100();
        container.setHeight100();
        container.setAnimateMembers(Boolean.FALSE);
    
        container.addMember(buildWorkflowsTable());
       
       return container;
    }

    private final Widget buildWorkflowsTable() {
                  
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
        
        listGrid.setSelectionType(SelectionStyle.SINGLE);
                
        ListGridField nameField = new ListGridField("name", "Workflow Name");
        ListGridField iaCountField = new ListGridField("iaCount", "Inital Actions", 75);
        ListGridField stepCountField = new ListGridField("stepCount", "Steps",75);
        ListGridField splitCountField = new ListGridField("splitCount", "Splits",75);
        ListGridField joinCountField = new ListGridField("joinCount", "Joins", 75);
        ListGridField picountField = new ListGridField("piCount", "Process Instances", 100);

        // Supress the display of zero
        picountField.setDisplayField("piCount-display");
        picountField.setSortByDisplayField(false);

        listGrid.setFields(new ListGridField[] { 
            nameField,
            setNumericCell(picountField), 
            setNumericCell(iaCountField), 
            setNumericCell(stepCountField), 
            setNumericCell(splitCountField), 
            setNumericCell(joinCountField) 
        });
   
         return listGrid;
    }


    private ListGridField setNumericCell(ListGridField field) {
        field.setType(ListGridFieldType.INTEGER);
        field.setAlign(Alignment.CENTER);
        field.setCellAlign(Alignment.CENTER);
        
        return field;
    }


}
