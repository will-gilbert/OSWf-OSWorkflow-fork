package org.informagen.oswf.simulator.client.application;

public interface Constants {

  // The section title used in the accordion
    public static final String WORKFLOW_SECTION_NAME     = "Workflows";
    public static final String DESCRIPTOR_SECTION_NAME   = "Definition";
    public static final String GRAPHVIZ_SECTION_NAME     = "Flow Diagram";
    public static final String SIMULATOR_SECTION_NAME    = "Simulation";

    // Internal ID of each accordion section
    public static final String WORKFLOW_SECTION_ID      = "workflows.section";
    public static final String DESCRIPTOR_SECTION_ID    = "descriptor.section";
    public static final String GRAPHVIZ_SECTION_ID      = "graphviz.section";
    public static final String SIMULATOR_SECTION_ID     = "simulator.section";

    // Accordion section background color
    public static final String WORKFLOW_SECTION_COLOR   = "#e0f0ff";
    public static final String DESCRIPTOR_SECTION_COLOR = "#f5f4ec";
    public static final String GRAPHVIZ_SECTION_COLOR   = "#e7e3d0";
    public static final String SIMULATOR_SECTION_COLOR  = "#f9ffe7";

    // Common accordion section button width and label color
    public static final int ACCORDION_BUTTON_WIDTH           = 140; // measured in pixels


}
