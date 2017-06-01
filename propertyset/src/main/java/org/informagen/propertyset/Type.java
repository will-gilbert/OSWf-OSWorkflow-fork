package org.informagen.propertyset;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
 
public enum Type {

    // 'enum' definitions with value and name
    BOOLEAN(1, "BOOLEAN"),
    INT(2, "INT"),
    LONG(3, "LONG"),
    DOUBLE(4, "DOUBLE"),
    STRING(5, "STRING"),
    TEXT(6, "TEXT"),
    DATE(7, "DATE"),
    OBJECT(8, "OBJECT"),
    XML(9, "XML"),
    DATA(10, "DATA"),
    PROPERTIES(11, "PROPERTIES");

    private int value;
    private String name;
    private static Map<Integer, Type> valueToType;
 
    // Constructor
    private Type(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Collection<Type> getTypes() {
        if (valueToType == null) 
            initializeMapping();
        return valueToType.values();
        
    }
 
    public static Type getType(int value) {
        if (valueToType == null) 
            initializeMapping();

        return valueToType.get(value);
    }
 
 
    public static Type getType(final String name) {
        
        for (Type type : Type.values()) 
           if(type.name.equals(name))
                return type;

        return null;
    }
 
 
    private static void initializeMapping() {
        valueToType = new HashMap<Integer, Type>();
        for (Type type : Type.values()) 
            valueToType.put(type.value, type);
    }
 
    public int getValue() {
        return value;
    }
 
    public String getName() {
        return name;
    }
 
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
