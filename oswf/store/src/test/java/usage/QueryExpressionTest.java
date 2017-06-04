package usage;

import org.informagen.oswf.testing.OSWfHibernateTestCase;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.OSWfConfiguration;

import org.informagen.oswf.exceptions.QueryNotSupportedException;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;


import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import org.informagen.oswf.Step;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * This test case is desinged to gain some understanding of OSWorkflow's
 *   and OSUser's implementation of security and maybe create some notion
 *   of a swimlane.
 *
 * This test case is based on the original 'ExampleWorkflowTest' from the
 *   OSWorkflow distribution
 *
 * @author Will Gilbert 
 */
 
 
public class QueryExpressionTest extends OSWfHibernateTestCase implements LeaveRequest {

    private static final String DBCONFIG = "H2.hibernate.xml";
//    private static final String DBCONFIG = "MySQL.hibernate.xml";

    // Class fields ///////////////////////////////////////////////////////////


    OSWfEngine wfEngine;
    OSWfEngine janeWorkflow;


    // Constructors ///////////////////////////////////////////////////////////

    public QueryExpressionTest() {
        super("oswf-store.cfg.xml",
              "oswf-propertyset.cfg.xml",
              DBCONFIG);
        
        
        // 'log' is declared in the superclass
        logger = LoggerFactory.getLogger(QueryExpressionTest.class.getName());
    }


    @Before
    public void setUp() throws Exception {
    
        OSWfConfiguration config = new DefaultOSWfConfiguration()
            .load(getClass().getResource("/oswf.xml"))
            .addPersistenceArg("sessionFactory", getSessionFactory())
        ;

        // Create a workflow engine from which "test" can create process instances
        wfEngine = new DefaultOSWfEngine("test");
        wfEngine.setConfiguration(config);
        
        wfEngine = wfEngine;
        
        janeWorkflow = new DefaultOSWfEngine("jane.doe");
        janeWorkflow.setConfiguration(config);

    }


    @After
    public void teardown() {
        closeSession();
        closeSessionFactory();
    }

    // Tests ==================================================================================
    
    // NB: A 'query' method returns the same result from an instance of a 'Workflow' the 
    //     workflow 'actor' has no influence on the query results.


    /* 
     *  How many process instances with name 'XXX' are in the Entry (processinstance) table
     *
     *      processinstance.name EQ 'XXX'       returns 0
     *      processinstance.name EQ 'Holiday'   returns 1, then 2
     */

    @Test
    public void entryByName() throws Exception {

        WorkflowExpressionQuery query;
        FieldExpression expression;

        // How many process instances with name 'XXX'
        expression = new FieldExpression(Context.ENTRY, 
            Field.NAME, Operator.EQUALS, "XXX");

        query = new WorkflowExpressionQuery(expression);
        assertEquals(0, wfEngine.query(query).size());


        // How many process instances with name 'Holiday'
        expression = new FieldExpression(Context.ENTRY, 
            Field.NAME, Operator.EQUALS, "Holiday");

        query = new WorkflowExpressionQuery(expression);

        // We should have no PIs in the WorkflowStore
        assertEquals(0, wfEngine.query(query).size());

        // Have user 'test' instance a process; count goes to one
        wfEngine.initialize("Holiday", INITIAL_ACTION);
        assertEquals(1, wfEngine.query(query).size());

        // Have user 'jave.doe' instance a process; count goes to two
        janeWorkflow.initialize("Holiday", INITIAL_ACTION);
        assertEquals(2, wfEngine.query(query).size());

    }


    /* 
     *  How many current steps belong to user "test" ===========================================
     *
     *     currentsteps.owner EQ 'test'
     */

    @Test
    public void currentStepsByOwner() throws Exception {

        WorkflowExpressionQuery query;
        FieldExpression expression;

        expression = new FieldExpression(Context.CURRENT_STEPS,
            Field.OWNER, Operator.EQUALS, "test");

        query = new WorkflowExpressionQuery(expression);
        
        // Initial state: we should have no PIs with current steps
        assertEquals(0, wfEngine.query(query).size());

        // Instance a process and query again; 
        // All workflow engines should report the same result: 1
        long piid1 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        assertTrue(piid1 > 0);
        assertEquals(1, wfEngine.query(query).size());
        assertEquals(1, wfEngine.query(query).size());
        assertEquals(1, janeWorkflow.query(query).size());

        // Instance another process and query again
        long piid2 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        assertTrue(piid2 > 0);
        assertEquals(2, wfEngine.query(query).size());
        assertEquals(2, janeWorkflow.query(query).size());

        // Have 'jane.doe' instance a process; should not change query results
        long piid3 = janeWorkflow.initialize("Holiday", INITIAL_ACTION);
        assertTrue(piid3 > 0);
        assertEquals(2, wfEngine.query(query).size());
        assertEquals(2, janeWorkflow.query(query).size());

        finishWorkflow(piid1);

        assertEquals(1, wfEngine.query(query).size());
        assertEquals(1, janeWorkflow.query(query).size());

        finishWorkflow(piid2);

        assertEquals(0, wfEngine.query(query).size());
        assertEquals(0, janeWorkflow.query(query).size());
        
        finishWorkflow(piid3);

        assertEquals(0, wfEngine.query(query).size());
        assertEquals(0, janeWorkflow.query(query).size());

    }


    /* 
     *  How many history steps belong to user "test" ===========================================
     *
     *     historysteps.actor EQ 'test'
     */

    @Test
    public void historyStepsByOwner() throws Exception {

        WorkflowExpressionQuery currentQuery, historyQuery;
        FieldExpression current, history;

        current = new FieldExpression(Context.CURRENT_STEPS,
            Field.OWNER, Operator.EQUALS, "test");
                                         
        history = new FieldExpression(Context.HISTORY_STEPS,
            Field.OWNER, Operator.EQUALS, "test");

        currentQuery = new WorkflowExpressionQuery(current);
        historyQuery = new WorkflowExpressionQuery(history);
        
        // We should have no process instances hence no history steps
        assertEquals(0, getSession().createQuery("FROM HibernateCurrentStep").list().size());
        assertEquals(0, getSession().createQuery("FROM HibernateHistoryStep").list().size());
        assertEquals(0, wfEngine.query(currentQuery).size());
        assertEquals(0, wfEngine.query(historyQuery).size());

        // Instance a process instance and query again; 
        // Both workflow engines will report the same result, 0
        
        long piid = wfEngine.initialize("Holiday", INITIAL_ACTION);
        
        // Still no history steps; process is waiting in current step: 100 owned by 'test'
        assertEquals(1, getSession().createQuery("FROM HibernateCurrentStep").list().size());
        assertEquals(0, getSession().createQuery("FROM HibernateHistoryStep").list().size());
        assertEquals(1, wfEngine.query(currentQuery).size());
        assertEquals(0, wfEngine.query(historyQuery).size());

        // Instance execute an action to move from step 100 to step 200;
        //   Step 100 will be moved to history
        
        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        
        // Should have one history step; Step 100, current step: 200
        assertEquals(0, getSession().createQuery("FROM HibernateCurrentStep WHERE owner = 'test'").list().size());
        assertEquals(1, getSession().createQuery("FROM HibernateHistoryStep WHERE owner = 'test'").list().size());
        assertEquals(0, wfEngine.query(currentQuery).size());
        assertEquals(1, wfEngine.query(historyQuery).size());

        // Finish this process instance
        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);
        assertEquals(ProcessInstanceState.COMPLETED, wfEngine.getProcessInstanceState(piid));

        // Should have two history steps; Steps 100 and 200; no current steps
        assertEquals(0, getSession().createQuery("FROM HibernateCurrentStep").list().size());
        assertEquals(2, getSession().createQuery("FROM HibernateHistoryStep").list().size());
        assertEquals(0, wfEngine.query(currentQuery).size());
        assertEquals(1, wfEngine.query(historyQuery).size());
        
        

    }



    /* How many process instances are in a state
     *
     *  processinstance.state == 'integer value of state'
     */


    @Test
    public void entryContextByState() throws Exception {

        WorkflowExpressionQuery countQuery, activatedQuery, completedQuery;
        FieldExpression expression, activated, completed;



        activated = new FieldExpression(Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.ACTIVE);

        completed = new FieldExpression(Context.ENTRY,
            Field.STATE, Operator.EQUALS, ProcessInstanceState.COMPLETED);
                                         
        activatedQuery = new WorkflowExpressionQuery(activated);
        completedQuery = new WorkflowExpressionQuery(completed);

        // Start with no process instances
        assertEquals(0, wfEngine.query(activatedQuery).size());
        assertEquals(0, wfEngine.query(completedQuery).size());
        

        // Create three process instances
        long piid1 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        long piid2 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        long piid3 = wfEngine.initialize("Holiday", INITIAL_ACTION);

        assertEquals(3, wfEngine.query(activatedQuery).size());
        assertEquals(0, wfEngine.query(completedQuery).size());

        finishWorkflow(piid1);
        finishWorkflow(piid2);
        
        assertEquals(1, wfEngine.query(activatedQuery).size());
        assertEquals(2, wfEngine.query(completedQuery).size());
        
        finishWorkflow(piid3);
        
        assertEquals(0, wfEngine.query(activatedQuery).size());
        assertEquals(3, wfEngine.query(completedQuery).size());
    }


    @Test
    public void OrQuery() throws Exception {

        WorkflowExpressionQuery leftQuery, rightQuery, orQuery;
        Expression left, right, or;

        left = new FieldExpression(Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "test");
                                                   
                                                   
        right = new FieldExpression(Context.CURRENT_STEPS,
            Field.OWNER, Operator.EQUALS, "jane.doe");

        leftQuery = new WorkflowExpressionQuery(left);
        rightQuery = new WorkflowExpressionQuery(right);

        Expression[] expressions = {left, right};
        
        or = new NestedExpression(expressions, LogicalOperator.OR);
        orQuery = new WorkflowExpressionQuery(or);

        // Let 'test' create two PIs, Jane will create one; combined for three
        long piid1 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        long piid2 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        janeWorkflow.initialize("Holiday", INITIAL_ACTION);

        assertEquals(2, wfEngine.query(leftQuery).size());
        assertEquals(1, wfEngine.query(rightQuery).size());
        assertEquals(3, wfEngine.query(orQuery).size());
       
        // Move Test's first PI to the next step
        wfEngine.doAction(piid1, REQUEST_HOLIDAYS);

        assertEquals(1, wfEngine.query(leftQuery).size());
        assertEquals(1, wfEngine.query(rightQuery).size());
        assertEquals(2, wfEngine.query(orQuery).size());

        // Move test's second PI to the next step
        wfEngine.doAction(piid2, REQUEST_HOLIDAYS);

        assertEquals(0, wfEngine.query(leftQuery).size());
        assertEquals(1, wfEngine.query(rightQuery).size());
        assertEquals(1, wfEngine.query(orQuery).size());


    }


    @Test
    public void suspendedState() throws Exception {


        WorkflowExpressionQuery activeQuery, suspendedQuery;
        Expression active, suspended;

        active = new FieldExpression(Context.ENTRY,
            Field.STATE, Operator.EQUALS,ProcessInstanceState.ACTIVE);
        
        suspended = new FieldExpression(Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.SUSPENDED);


        activeQuery = new WorkflowExpressionQuery(active);
        suspendedQuery = new WorkflowExpressionQuery(suspended);


        // Create two PIs, then suspend one
        long piid = wfEngine.initialize("Holiday", INITIAL_ACTION);
        wfEngine.initialize("Holiday", INITIAL_ACTION);

        assertEquals(2, wfEngine.query(activeQuery).size());

        wfEngine.changeEntryState(piid, ProcessInstanceState.SUSPENDED);

        assertEquals(1, wfEngine.query(activeQuery).size());
        assertEquals(1, wfEngine.query(suspendedQuery).size());
        
        // Reactive the suspended PI
        wfEngine.changeEntryState(piid, ProcessInstanceState.ACTIVE);

        assertEquals(2, wfEngine.query(activeQuery).size());
        assertEquals(0, wfEngine.query(suspendedQuery).size());

    }


    @Test
    public void suspendedSteps() throws Exception {
        
        WorkflowExpressionQuery underwayQuery,  activatedQuery, availableQuery;
        Expression underway, activated, availableSteps;
                                        
        underway = new FieldExpression(Context.CURRENT_STEPS,
            Field.STATUS, Operator.EQUALS, "Underway");
        
        activated = new FieldExpression(Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.ACTIVE);

        
        underwayQuery = new WorkflowExpressionQuery(underway);
        activatedQuery = new WorkflowExpressionQuery(activated);

/*
        // Cannot query across contexts i.e. Steps and entries
        Expression[] expressions = {underway, activated};
        availableSteps = new NestedExpression(expressions, LogicalOperator.AND);
        availableQuery = new WorkflowExpressionQuery(availableSteps);
*/

        // Create two PIs, then suspend one
        long piid1 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        long piid2 = wfEngine.initialize("Holiday", INITIAL_ACTION);
 
        assertEquals(2, wfEngine.query(underwayQuery).size());
        assertEquals(2, wfEngine.query(activatedQuery).size());
 
        wfEngine.changeEntryState(piid1, ProcessInstanceState.SUSPENDED);

        assertEquals(2, wfEngine.query(underwayQuery).size());
        assertEquals(1, wfEngine.query(activatedQuery).size());

    }


    @Test
    public void andQuery() throws Exception {
    
        WorkflowExpressionQuery leftQuery, rightQuery, andQuery;
        Expression left, right, and;

        // Create two process instances
        long piid1 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        long piid2 = wfEngine.initialize("Holiday", INITIAL_ACTION);
        
        left = new FieldExpression(Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "test");
                                        
        leftQuery = new WorkflowExpressionQuery(left);
        assertEquals(2, wfEngine.query(leftQuery).size());
                                                    
        right = new FieldExpression(Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Underway");

        rightQuery = new WorkflowExpressionQuery(right);
        assertEquals(2, wfEngine.query(rightQuery).size());

        Expression[] expressions = {left, right};
        and = new NestedExpression(expressions, LogicalOperator.AND);
                                         
        andQuery = new WorkflowExpressionQuery(and);
        assertEquals(2, wfEngine.query(andQuery).size());
  
        // Move process instance 1 to the next step; 
        //  Now only one step with owner 'test', but two steps 'underway'
        wfEngine.doAction(piid1, REQUEST_HOLIDAYS);
  
        assertEquals(1, wfEngine.query(leftQuery).size());
        assertEquals(2, wfEngine.query(rightQuery).size());
        assertEquals(1, wfEngine.query(andQuery).size());
  
    }


    private void finishWorkflow(long piid) throws Exception {

        // Verify intial state
        assertEquals(ProcessInstanceState.ACTIVE, wfEngine.getProcessInstanceState(piid));

        wfEngine.doAction(piid, REQUEST_HOLIDAYS);
        wfEngine.doAction(piid, LINE_MANAGER_APPROVES);

        // Ensure successful completion
        assertProperty(wfEngine, piid, "result", "approved");
        assertEquals(ProcessInstanceState.COMPLETED, wfEngine.getProcessInstanceState(piid));

    }


}
