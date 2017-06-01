package tests;


import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.FieldExpression;


// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * @author Will Gilbert, 9-Feb-2013
 */

public class QueryExpressionTest {

    @Test
    public void canAccessField()  {
        assertNotNull(Field.OWNER);
        assertEquals(1, Field.OWNER.getValue());
        assertEquals("OWNER", Field.OWNER.getName());
    }

    @Test
    public void canAccessOperator()  {
        assertNotNull(Operator.EQUALS);
        assertEquals(1, Operator.EQUALS.getValue());
        assertEquals("==", Operator.EQUALS.getSymbol());
        assertEquals("equality operator", Operator.EQUALS.getDescription());
    }

    @Test
    public void canAccessLogicalOperator()  {
        assertNotNull(LogicalOperator.AND);
        assertEquals(6, LogicalOperator.AND.getValue());
        assertEquals(7, LogicalOperator.OR.getValue());
        assertEquals("&&", LogicalOperator.AND.getSymbol());
        assertEquals("||", LogicalOperator.OR.getSymbol());
        assertEquals("and logical operator", LogicalOperator.AND.getDescription());
    }
    
    @Test
    public void canAccessContext()  {
        assertNotNull(Context.HISTORY_STEPS);
        assertEquals(1, Context.HISTORY_STEPS.getValue());
        assertEquals("history steps", Context.HISTORY_STEPS.getDescription());
    }


    @Test
    public void expressionToString()  {

        FieldExpression expression = new FieldExpression(
            Context.ENTRY,
            Field.NAME,  Operator.EQUALS, "workflow");

        assertEquals("workflow entry; NAME == workflow", expression.toString());
    }



    
}
