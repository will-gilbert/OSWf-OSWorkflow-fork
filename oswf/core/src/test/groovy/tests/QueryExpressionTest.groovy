package tests

import org.informagen.oswf.query.Context
import org.informagen.oswf.query.Field
import org.informagen.oswf.query.Operator
import org.informagen.oswf.query.LogicalOperator
import org.informagen.oswf.query.Expression
import org.informagen.oswf.query.FieldExpression


// JUnit 4.x testing
import org.junit.Ignore
import org.junit.Test

/**
 * @author Will Gilbert, 9-Feb-2013
 */

class QueryExpressionTest {

    @Test
    void canAccessField()  {
        assert Field.OWNER
        assert 1 == Field.OWNER.getValue()
        assert 'OWNER' == Field.OWNER.getName()
    }

    @Test
    void canAccessOperator()  {
        assert Operator.EQUALS
        assert 1 == Operator.EQUALS.getValue()
        assert "==" == Operator.EQUALS.getSymbol()
        assert "equality operator" == Operator.EQUALS.getDescription()
    }

    @Test
    void canAccessLogicalOperator()  {
        assert LogicalOperator.AND
        assert 6 ==LogicalOperator.AND.getValue()
        assert 7 ==LogicalOperator.OR.getValue()
        assert "&&" ==LogicalOperator.AND.getSymbol()
        assert "||" ==LogicalOperator.OR.getSymbol()
        assert "and logical operator" ==LogicalOperator.AND.getDescription()
    }
    
    @Test
    void canAccessContext()  {
        assert Context.HISTORY_STEPS
        assert 1 == Context.HISTORY_STEPS.getValue()
        assert "history steps" == Context.HISTORY_STEPS.getDescription()
    }


    @Test
    void expressionToString()  {

        FieldExpression expression = new FieldExpression(
            Context.ENTRY,
            Field.NAME,  
            Operator.EQUALS, "workflow")

        assert "workflow entry; NAME == workflow" == expression.toString()
    }



    
}
