package org.informagen.oswf.query;

import org.informagen.oswf.OSWfEnum;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;


/**
 * Field expressions are used when constructing a workflow query on the fields
 * of persistent workflow instances like (START_DATE, OWNER,....).
 * Field expressions have three attributes. These are:
 * <li>operator: This is the operator to apply on the expression.
 * <li>field: The workflow field to test agains
 * <li>Context: The context to search in, which can be one history, current steps, or a workflow instance.
 *
 * @author Christine Zimmermann
 */
public class FieldExpression extends Expression {


    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Object value;
    private Context context;
    private Field field;
    private Operator operator;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    private FieldExpression() {}

    public FieldExpression(Context context, Field field, Operator operator, Object value) {
        this.field = field;
        this.context = context;
        this.operator = operator;
        
        if(value instanceof OSWfEnum)
            this.value = ((OSWfEnum)value).getValue();
        else
            this.value = value;

    }

    public FieldExpression(Context context, Field field, Operator operator, Object value, boolean negate) {
        this(context, field, operator, value);
        super.negate = negate;
    }


    @Deprecated
    public FieldExpression(Field field, Context context, Operator operator, Object value) {
        this(context, field, operator, value);
    }

    @Deprecated
    public FieldExpression(Field field, Context context, Operator operator, Object value, boolean negate) {
        this(field, context, operator, value);
        super.negate = negate;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    protected void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public boolean isNested() {
        return false;
    }

    protected void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    
    public String toString() {
        return new StringBuffer()
            .append(context.getDescription()).append("; ")
            .append(field.getName()).append(" ")
            .append(operator.getSymbol()).append(" ")
            .append((value != null) ? value.toString() : "null")
            .toString()
        ;
        
    }
    
    
}
