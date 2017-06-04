package org.informagen.oswf;

import org.informagen.oswf.OSWfEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * From the WfMC Glosayry, page 47
 *   http://www.wfmc.org/standards/docs/TC-1011_term_glossary_v3.pdf
 *
 */ 
 
public enum ProcessInstanceState implements OSWfEnum {

    // 'enum' definitions with value, name
    INITIATED( 0, "INITIATED", "The process instance has been created, but may not yet be running"),
    RUNNING(   1, "RUNNING",   "The process instance has started but no action have been executed"),
    ACTIVE(    2, "ACTIVE",    "The process instance is underway because one or more actions have been executed"),
    SUSPENDED( 3, "SUSPENDED", "The process instance is quiescent; no further activities are available until it is active or running"),
    COMPLETED( 4, "COMPLETED", "The process instance has achieved its completion conditions"),
    TERMINATED(5, "TERMINATED","The execution of the process has been stopped (abnormally) due to error or user request"),
    ARCHIVED(  6, "ARCHIVED",  "The process instance has been placed in an indefinite archive state"),
    UNKNOWN(   7, "UNKNOWN",   "State returned when an state exception is thrown");

    private int value;
    private String name;
    private String description;
    private static Map<Integer, ProcessInstanceState> valueToState;
 
    // Constructor
    private ProcessInstanceState(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }
 
    public static ProcessInstanceState getProcessInstanceState(int value) {
        if (valueToState == null) 
            initializeMapping();

        return valueToState.get(value);
    }
 
    private static void initializeMapping() {
        valueToState = new HashMap<Integer, ProcessInstanceState>();
        for (ProcessInstanceState state : ProcessInstanceState.values()) 
            valueToState.put(state.value, state);
    }
 
    @Override
    public int getValue() {
        return value;
    }
 
    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ProcessInstanceState");
        sb.append("{value=").append(value);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
