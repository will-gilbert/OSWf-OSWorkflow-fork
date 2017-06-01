package org.informagen.oswf.query;

import org.informagen.oswf.query.Field;

import java.io.Serializable;

/**
 * Workflow Query.
 * A workflow expression-based query is constructed by specifying a number of expressions in the query.
 * Currently queries can only have one operator act on them. Either the expressions are either evaluated
 * with an OR, whereby the first expression that passes results in inclusion of a result, or with an AND,
 * whereby all expressions must return true for a result to be included.
 *
 * @author Christine Zimmermann
 */
public class WorkflowExpressionQuery implements Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 5810528106491875046L;
    public static final int SORT_NONE = 0;
    public static final int SORT_ASC = 1;
    public static final int SORT_DESC = -1;

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Expression expression = null;
    private Field orderBy;
    private int sortOrder;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public WorkflowExpressionQuery() {
    }

    /**
     * Create a WorkflowExpressionQuery that consists of one expression.
     */
    public WorkflowExpressionQuery(Expression expression) {
        this.expression = expression;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setOrderBy(Field orderBy) {
        this.orderBy = orderBy;
    }

    public Field getOrderBy() {
        return orderBy;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
