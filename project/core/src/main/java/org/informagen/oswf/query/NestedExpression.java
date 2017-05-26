package org.informagen.oswf.query;

import org.informagen.oswf.query.LogicalOperator;

/**
 * Nested expressions are used when constructing a workflow query.
 * A nested expression consists of:
 * <li>one or more expressions: Each of them can again be a NestedExpression.
 * <li>operator: The operator used to evaluate the value of the nested expression
 *     from the specified sub expressions.
 *
 * @author Christine Zimmermann
 */
public class NestedExpression extends Expression {


    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Expression[] expressions = null;
    private LogicalOperator expressionOperator = LogicalOperator.AND;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public NestedExpression() {}

    /**
    * Create a NestedExpression that consists of multiple expressions.
    * @param expressions an array of expressions for this query.
    * @param operator {@link NestedExpression#AND} or {@link NestedExpression#OR}.
    */
    public NestedExpression(Expression[] expressions, LogicalOperator operator) {
        this.expressions = expressions;
        this.expressionOperator = operator;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Expression getExpression(int index) {
        return expressions[index];
    }

    /**
     * Get the number of expressions in this query.
     */
    public int getExpressionCount() {
        return expressions.length;
    }

    public void setExpressionOperator(LogicalOperator expressionOperator) {
        this.expressionOperator = expressionOperator;
    }

    /**
     * @return {@link LogicalOperator#AND} if all the expressions must match,
     * or {@link LogicalOperator#OR} if only one must match.
     */
    public LogicalOperator getExpressionOperator() {
        return this.expressionOperator;
    }

    public void setExpressions(Expression[] expressions) {
        this.expressions = expressions;
    }

    public boolean isNested() {
        return true;
    }
}
