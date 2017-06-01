package org.informagen.oswf.query;

import org.informagen.oswf.OSWfEnum;

import java.util.HashMap;
import java.util.Map;
 
public enum Field implements OSWfEnum {

    // 'enum' definitions with value, name, description
    OWNER(1, "OWNER"),
    START_DATE(2, "START_DATE"),
    FINISH_DATE(3, "FINISH_DATE"),
    ACTION(4,"ACTION"),
    STEP(5, "STEP"),
    ACTOR(6, "ACTOR"),
    STATUS(7, "STATUS"),
    NAME(8, "NAME"),
    STATE(9, "STATE"),
    DUE_DATE(10,"DUE_DATE");

    private int value;
    private String name;
    private static Map<Integer, Field> valueToField;
 
    // Constructor
    private Field(int value, String name) {
        this.value = value;
        this.name = name;
    }
 
    public static Field getField(int value) {
        if (valueToField == null) 
            initializeMapping();

        return valueToField.get(value);
    }
 
    private static void initializeMapping() {
        valueToField = new HashMap<Integer, Field>();
        for (Field field : Field.values()) 
            valueToField.put(field.value, field);
    }
 
    @Override
    public int getValue() {
        return value;
    }
 
    @Override
    public String getName() {
        return name;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Field");
        sb.append("{value=").append(value);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
