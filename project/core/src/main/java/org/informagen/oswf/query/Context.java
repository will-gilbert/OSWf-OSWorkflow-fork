package org.informagen.oswf.query;

import org.informagen.oswf.OSWfEnum;

import java.util.HashMap;
import java.util.Map;
 
public enum Context implements OSWfEnum {


    // 'enum' definitions with value, name, description
    HISTORY_STEPS(1, "HISTORY_STEPS", "history steps"),
    CURRENT_STEPS(2, "CURRENT_STEPS", "current steps"),
    ENTRY(3, "ENTRY", "workflow entry");


    private int value;
    private String name;
    private String description;
    private static Map<Integer, Context> valueToContext;
 
    // Constructor
    private Context (int value, String name, String description) {
        this.value = value;
        this.description = description;
    }
 
    public static Context getContext(int value) {
        if (valueToContext == null) 
            initializeMapping();

        return valueToContext.get(value);
    }
 
    private static void initializeMapping() {
        valueToContext = new HashMap<Integer, Context>();
        for (Context context  : Context.values()) 
            valueToContext.put(context.value, context);
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
        sb.append("Context");
        sb.append("{value=").append(value);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
