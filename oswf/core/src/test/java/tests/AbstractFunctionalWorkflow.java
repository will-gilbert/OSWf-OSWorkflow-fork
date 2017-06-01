package tests;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;


// OSWf exceptions
import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.QueryNotSupportedException;

import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.descriptors.WorkflowDescriptor;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;

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
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @see MemoryFunctionalWorkflowTest
 *
 */

public abstract class AbstractFunctionalWorkflow {
    
    // S T A T I C   F I E L D S --------------------------------------------------------------

    private static final String USER_TEST = "test user name";

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected OSWfEngine wfEngine;
    protected WorkflowDescriptor workflowDescriptor;


    // B E F O R E  ---------------------------------------------------------------------------

    // - Create workflow engine for the 'test user' as the actor
    // - Create a Security Manager, add our 'test user'
    // - Create three Roles gives these roles to our 'test user'

    @Before
    public void setup() throws Exception {
        
        wfEngine = new DefaultOSWfEngine(USER_TEST);

        SecurityManager securityManager = SecurityManager.getInstance();
        assertNotNull("Could not get UserManager", securityManager);
        
        User user = securityManager.createUser(USER_TEST);
    
        Role foos = securityManager.createRole("foos");
        Role bars = securityManager.createRole("bars");
        Role bazs = securityManager.createRole("bazs");
        
        user.addToRole(foos);
        user.addToRole(bars);
        user.addToRole(bazs);

    }


    // T E S T S  -----------------------------------------------------------------------------

    @Test
    public void exampleWorkflow() throws Exception {

        // Get the workflow name; Here the URL of the resource
        // With no configuration the workflow engine will use an in-memory store and persistence.
        // Use the URLLoader, which is the default
        // Determine that an 'initial-action' of 100 is valid

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, 100));

        // Start the workflow for our 'test user' and return the process instance id

        long piid = wfEngine.initialize(workflowName, 100);
        String workorderName = wfEngine.getWorkflowName(piid);
        workflowDescriptor = wfEngine.getWorkflowDescriptor(workorderName);

        if (logger.isDebugEnabled()) {
            logger.debug("Name of workorder: " + workorderName);
        }

        // StepConditions for Step id=1
        assertTrue("Expected step-condition permA in step 1 not found", 
            wfEngine.getStepConditions(piid)
                    .get(1)  
                    .contains(new StepCondition(1, "permA", true)))
        ;

        // Only one available step with id=1
        List currentSteps = wfEngine.getCurrentSteps(piid);
        assertEquals("Unexpected number of current steps", 1, currentSteps.size());
        assertEquals("Unexpected current step", 1, ((Step) currentSteps.get(0)).getStepId());

        // No history at the moment
        List historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 0, historySteps.size());


        // Execute action 1 in Step 1; Go to Split 1 end up in Steps 6 (Foo) and 7 (Bar)
        if (logger.isDebugEnabled()) {
            logger.debug("Perform Finish First Draft");
        }
        wfEngine.doAction(piid, 1);

        // We now have 1 history step
        List<Integer> actions = wfEngine.getAvailableActions(piid);
        assertEquals(3, actions.size());
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        // Get the first history step and check that the due date is in the past
        Step historyStep = (Step) historySteps.get(0);
        assertEquals(USER_TEST, historyStep.getActor());
        assertNull(historyStep.getDueDate());

        // Check system date, add in a 1 second fudgefactor.
        assertTrue("history step finish date " + historyStep.getFinishDate() + " is in the future!", (historyStep.getFinishDate().getTime() - 1000) < System.currentTimeMillis());
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(3, actions.size());
        logActions(actions);

        if (logger.isDebugEnabled()) {
            logger.debug("Perform: Finish Foo; Waiting at Join");
        }

        wfEngine.doAction(piid, 12);

        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 2, historySteps.size());


        if (logger.isDebugEnabled()) {
            logger.debug("Perform 'Stay in Bar'; Return to Step 7");
        }

        wfEngine.doAction(piid, 113);

        // There are two actions, 'Stay in Bar' and 'Finish Bar'
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(2, actions.size());
        assertTrue((actions.get(0) == 13) && (actions.get(1) == 113));
        logActions(actions);

        if (logger.isDebugEnabled()) {
            logger.debug("Perform 'Finish Bar'");
        }

        wfEngine.doAction(piid, 13);
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(1, actions.size());
        logActions(actions);

        if (logger.isDebugEnabled()) {
            logger.debug("Perform Finish Baz; Arriving at Join and proceeding to Step 2");
        }

        // Step 2, 'Edit Doc' has two actions 'Finish Editing (3)' and 'Requeue Editing (4)'
        wfEngine.doAction(piid, 14);
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(2, actions.size());
        logActions(actions);
    
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 5, historySteps.size());

        if (logger.isDebugEnabled()) {
            logger.debug("Perform 'Finish Editing'");
        }

        wfEngine.doAction(piid, 3);

        // Step 3, 'Review Doc' has three actions 'Peer Review (6)', 'Publish Doc (7)' and 'More Edits (5)'
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(3, actions.size());
        logActions(actions);

        if (logger.isDebugEnabled()) {
            logger.debug("Perform 'Publish Doc'");
        }

        wfEngine.doAction(piid, 7);

        // Step 5, 'Review Doc' has one action 'Publish Document (11)'
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(1, actions.size());
        logActions(actions);

        if (logger.isDebugEnabled()) {
            logger.debug("Perform 'Publish Document'");
        }

        wfEngine.doAction(piid, 11);

        // Publish the document and finish this process instance
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(0, actions.size());
        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 8, historySteps.size());
        logHistory(historySteps);
    }

    @Test
    public void exceptionOnIllegalStayInCurrentStep() throws Exception {
        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, 100));

        try {
            long workflowId = wfEngine.initialize(workflowName, 200);
            fail("initial action result specified target step of current step. Succeeded but should not have.");
        } catch (WorkflowException e) {
            // expected, no such thing as current step for initial action
        }
    }

    @Test
    public void metadataAccess() throws Exception {
        String workflowName = getWorkflowName();
        long workflowId = wfEngine.initialize(workflowName, 100);
        WorkflowDescriptor wfDesc = wfEngine.getWorkflowDescriptor(workflowName);

        Map meta = wfDesc.getMetaAttributes();
        assertTrue("missing metadata", (meta.get("workflow-meta1")).equals("workflow-meta1-value"));
        assertTrue("missing metadata", (meta.get("workflow-meta2")).equals("workflow-meta2-value"));

        meta = wfDesc.getStep(1).getMetaAttributes();
        assertTrue("missing metadata", (meta.get("step-meta1")).equals("step-meta1-value"));
        assertTrue("missing metadata", (meta.get("step-meta2")).equals("step-meta2-value"));

        meta = wfDesc.getAction(1).getMetaAttributes();
        assertTrue("missing metadata", (meta.get("action-meta1")).equals("action-meta1-value"));
        assertTrue("missing metadata", (meta.get("action-meta2")).equals("action-meta2-value"));
    }

    @Test
    public void workflowExpressionQuery() throws Exception {
        
        List workflows;
        WorkflowExpressionQuery query;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, 100));

        //-------------------   Field.OWNER  +  Context.CURRENT_STEPS ----------------------
        query = new WorkflowExpressionQuery(
            new FieldExpression(
                Context.CURRENT_STEPS, 
                Field.OWNER, Operator.EQUALS, USER_TEST
            )
        );

        try {
            workflows = wfEngine.query(query);
            assertEquals("Empty OWNER+CURRENT_STEPS", 0, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.error("Store does not support query");
            return;
        }

        long workflowId = wfEngine.initialize(workflowName, 100, new HashMap());
        workflows = wfEngine.query(query);
        assertEquals("OWNER+CURRENT_STEPS", 1, workflows.size());



        //-------------------  Field.NAME + Context.ENTRY ----------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.NAME, Operator.EQUALS,  "notexistingname")
        );
        workflows = wfEngine.query(query);
        assertEquals("empty NAME+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.NAME,Operator.EQUALS,  workflowName)
        );
        workflows = wfEngine.query(query);
        assertEquals("NAME+ENTRY", 1, workflows.size());

        //-------------------  Field.STATE + Context.ENTRY ----------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.COMPLETE)
        );
        workflows = wfEngine.query(query);
        assertEquals("empty STATE+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY,
            Field.STATE, Operator.EQUALS, ProcessInstanceState.ACTIVE)
        );
        workflows = wfEngine.query(query);
        assertEquals("STATE+ENTRY", 1, workflows.size());

        // ---------------------------  empty nested query : AND ---------------------------------
        Expression queryLeft = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, USER_TEST
        );
        
        Expression queryRight = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Finished"
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, LogicalOperator.AND));
        workflows = wfEngine.query(query);
        assertEquals("empty nested query AND", 0, workflows.size());

        // -------------------------- negated nested query: AND ----------------------------------
        queryLeft = new FieldExpression(
            Context.CURRENT_STEPS,
            Field.OWNER,  Operator.EQUALS, USER_TEST
        );
        
        queryRight = new FieldExpression(
            Context.CURRENT_STEPS,
            Field.STATUS,  Operator.EQUALS, "Finished", true
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, LogicalOperator.AND));
        workflows = wfEngine.query(query);
        assertEquals("negated nested query AND", 1, workflows.size());

        // -------------------------- nested query: AND + same context ------------------------------------------
        queryRight = new FieldExpression(
            Context.CURRENT_STEPS,
            Field.STATUS, Operator.EQUALS, "Underway"
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, LogicalOperator.AND));
                    
        workflows = wfEngine.query(query);
        assertEquals("nested query AND", 1, workflows.size());

        // ------------------------- empty nested query: OR + mixed context -------------------------------------
        queryLeft = new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.FINISH_DATE, Operator.LT, new Date()
        );
        
        queryRight = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Finished"
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, LogicalOperator.OR));

        try {
            workflows = wfEngine.query(query);
            assertEquals("empty nested query OR + mixed context", 0, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.warn("Query not supported: " + e);
        }

        // ------------------------- negated nested query: OR -------------------------------------
        queryLeft = new FieldExpression(
            Context.HISTORY_STEPS,
            Field.FINISH_DATE,  Operator.LT, new Date()
        );
        
        queryRight = new FieldExpression(
            Context.CURRENT_STEPS,
            Field.STATUS,  Operator.EQUALS, "Finished", true
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                        queryLeft, queryRight
                    }, LogicalOperator.OR));

        try {
            workflows = wfEngine.query(query);
            assertEquals("negated nested query OR", 1, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.warn("Query not supported: " + e);
        }

        // ------------------------- nested query: OR + mixed context -------------------------------------
        queryLeft = new FieldExpression(
            Context.HISTORY_STEPS,
            Field.FINISH_DATE,  Operator.LT, new Date()
        );
        
        queryRight = new FieldExpression(
            Context.ENTRY, 
            Field.NAME, Operator.EQUALS, workflowName
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.OR)
        );

        try {
            workflows = wfEngine.query(query);
            assertEquals("nested query OR + mixed context", 1, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.warn("Query not supported: " + e);
        }

        // --------------------- START_DATE+CURRENT_STEPS -------------------------------------------------
        //there should be one step that has been started
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.CURRENT_STEPS,
            Field.START_DATE,  Operator.LT, new Date(System.currentTimeMillis() + 1000))
        );

        workflows = wfEngine.query(query);
        assertEquals("Expected to find one workflow step that was started", 1, workflows.size());

        // --------------------- empty FINISH_DATE+HISTORY_STEPS -------------------------------------------
        //there should be no steps that have been completed
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.FINISH_DATE, Operator.LT, new Date())
        );
        
        workflows = wfEngine.query(query);
        assertEquals("Expected to find no history steps that were completed", 0, workflows.size());

        // =================================================================================================
        wfEngine.doAction(workflowId, 1);

        // --------------------- START_DATE+HISTORY_STEPS -------------------------------------------------
        //there should be two step that have been started
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS,
            Field.START_DATE,  Operator.LT, new Date(System.currentTimeMillis() + 1000))
        );
        
        workflows = wfEngine.query(query);
        assertEquals("Expected to find 1 workflow step in the history for entry #" + workflowId, 1, workflows.size());

        // --------------------- FINISH_DATE+HISTORY_STEPS -------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS,
            Field.FINISH_DATE,  Operator.LT, new Date(System.currentTimeMillis() + 1000))
        );
        
        workflows = wfEngine.query(query);
        assertEquals("Expected to find 1 history steps that was completed", 1, workflows.size());

        // --------------------- ACTION + HISTORY_STEPS ----------------------------------------------

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS,
            Field.ACTION, Operator.EQUALS, new Integer(1))
        );
        
        workflows = wfEngine.query(query);
        assertEquals("ACTION + HISTORY_STEPS", 1, workflows.size());

        // --------------------- STEP + HISTORY_STEPS ----------------------------------------------

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.STEP, Operator.EQUALS, new Integer(1))
        );
        
        workflows = wfEngine.query(query);
        assertEquals("STEP + HISTORY_STEPS", 1, workflows.size());

        // --------------------- ACTOR + HISTORY_STEPS --------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.ACTOR, Operator.EQUALS, USER_TEST)
        );
        
        workflows = wfEngine.query(query);
        assertEquals("ACTOR + HISTORY_STEPS", 1, workflows.size());

        //----------------------------------------------------------------------------
        // ----- some more tests using nested expressions
        long workflowId2 = wfEngine.initialize(workflowName, 100);
        wfEngine.changeEntryState(workflowId, ProcessInstanceState.SUSPENDED);
        
        queryRight = new FieldExpression(
            Context.ENTRY, 
            Field.STATE,Operator.EQUALS, ProcessInstanceState.ACTIVE
        );
        
        queryLeft = new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.SUSPENDED
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.OR)
        );
        
        workflows = wfEngine.query(query);
        assertEquals(2, workflows.size());

        queryLeft = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, USER_TEST
        );
        
        queryRight = new FieldExpression(
            Context.CURRENT_STEPS,
            Field.STATUS,  Operator.EQUALS, "Finished", true
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.AND));
                    
        workflows = wfEngine.query(query);
        assertEquals("Expected to find 2 workflows in current steps", 2, workflows.size());
    }

    @Test
    public void workflowQuery() throws Exception {
        // WorkflowQuery query = null;
        List workflows;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, 100));

        // Deprecated -- need to fix
        // try {
        //     query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
        //     workflows = wfEngine.query(query);
        //     assertEquals(0, workflows.size());
        // } catch (QueryNotSupportedException e) {
        //     logger.error("Store does not support query");
        // }
        // 
        // try {
        //     long workflowId = wfEngine.initialize(workflowName, 100, new HashMap());
        //     workflows = wfEngine.query(query);
        //     assertEquals(1, workflows.size());
        // } catch (QueryNotSupportedException e) {
        //     logger.error("Store does not support query");
        // }

        // try {
        //     WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
        //     WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
        //     query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        //     workflows = wfEngine.query(query);
        //     assertEquals(0, workflows.size());
        // 
        //     queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
        //     query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
        //     workflows = wfEngine.query(query);
        //     assertEquals(1, workflows.size());
        // } catch (QueryNotSupportedException e) {
        //     logger.error("Store does not support query");
        // }
    }


    protected String getWorkflowName() {
        return getClass().getResource("/usage/example.oswf.xml").toString();
    }

    protected void logActions(List<Integer> actions) {
        
        for (int i = 0; i < actions.size(); i++) {
            String name = workflowDescriptor.getAction(actions.get(i)).getName();
            int actionId = workflowDescriptor.getAction(actions.get(i)).getId();

            if (logger.isDebugEnabled()) {
                logger.debug("Actions Available: " + name + " id:" + actionId);
            }
        }
    }

    protected void logHistory(List<Step> steps) {

        for (int i = steps.size()-1; i >= 0; i--) {
            String name = workflowDescriptor.getAction(steps.get(i).getActionId()).getName();
            int actionId = workflowDescriptor.getAction(steps.get(i).getActionId()).getId();

            if (logger.isDebugEnabled()) {
                logger.debug("History Actions: " + name + " id:" + actionId);
            }
        }
    }
}
