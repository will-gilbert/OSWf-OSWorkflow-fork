package org.informagen.oswf.query;

import org.informagen.oswf.OSWfEnum;

import java.util.HashMap;
import java.util.Map;
 
public enum Operator implements OSWfEnum {

    // 'enum' definitions with value, name, description and symbol
    EQUALS(    1, "EQUALS",     "equality operator",     "=="),
    LT(        2, "LT",         "less than operator",    "<"),
    GT(        3, "GT",         "greater than operator", ">"),
    NOT_EQUALS(5, "NOT_EQUALS", "not equals operator",   "!=");

    private int value;
    private String symbol;
    private String name;
    private String description;
    private static Map<Integer, Operator> valueToOperator;
 
    // Constructor
    private Operator (int value, String name, String description, String symbol) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.symbol = symbol;
    }
 
    public static Operator getOperator(int value) {
        if (valueToOperator == null) 
            initializeMapping();

        return valueToOperator.get(value);
    }
 
    private static void initializeMapping() {
        valueToOperator = new HashMap<Integer, Operator>();
        for (Operator operator  : Operator.values()) 
            valueToOperator.put(operator.value, operator);
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
 
    public String getSymbol() {
        return symbol;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Operator");
        sb.append("{value=").append(value);
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
