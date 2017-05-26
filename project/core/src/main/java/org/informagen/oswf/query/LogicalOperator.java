package org.informagen.oswf.query;

import org.informagen.oswf.OSWfEnum;

import java.util.HashMap;
import java.util.Map;
 
public enum LogicalOperator implements OSWfEnum  {

    // 'enum' definitions with value, name, description and symbol
    AND(6, "AND", "and logical operator", "&&"),
    OR( 7, "OR",  "or logical operator",  "||")
    ;

    private int value;
    private String name;
    private String symbol;
    private String description;
    private static Map<Integer, LogicalOperator> valueToOperator;
 
    // Constructor
    private LogicalOperator(int value, String name, String description, String symbol) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.symbol = symbol;
    }
 
    public static LogicalOperator getOperator(int value) {
        if (valueToOperator == null) 
            initializeMapping();

        return valueToOperator.get(value);
    }
 
    private static void initializeMapping() {
        valueToOperator = new HashMap<Integer, LogicalOperator>();
        for (LogicalOperator operator  : LogicalOperator.values()) 
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
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
