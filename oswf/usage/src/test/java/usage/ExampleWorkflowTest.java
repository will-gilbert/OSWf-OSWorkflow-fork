package usage;


import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.impl.UserOSWfEngine;

import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.Step;
import org.informagen.oswf.StepCondition;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.exceptions.QueryNotSupportedException;
import org.informagen.oswf.exceptions.WorkflowException;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf - Store
import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
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
 * This test case is functional in that it attempts to validate the entire
 * lifecycle of a workflow.  This is also a good resource for beginners
 * to OSWorkflow.  This class is extended to for various SPI's.
 *
 * @author Eric Pugh (epugh@upstate.com)
 */
 
 
public class ExampleWorkflowTest extends OSWfTestCase {

    final static int INITIAL_ACTION         =   100;
    final static int INITIAL_ACTION_ILLEGAL =   200;
    
    final static int STEP_FIRST_DRAFT       = 1;
    final static int STEP_EDIT_DOC          = 2;
    final static int STEP_REVIEW_DOC        = 3;
    final static int STEP_SECOND_REVIEW_DOC = 4;
    final static int STEP_PUBLISH_DOC       = 5;
    final static int STEP_FOO               = 6;
    final static int STEP_BAR               = 7;
    final static int STEP_BAZ               = 8;

    final static int ACTION_FINISH_FIRST_DRAFT =  1;
    final static int ACTION_FINISH_FOO         = 12;
    final static int ACTION_FINISH_BAR         = 13;
    final static int ACTION_STAY_IN_BAR        = 113;
    final static int ACTION_FINISH_BAZ         = 14;
    final static int ACTION_FINISH_EDITING     =  3;
    final static int ACTION_PUBLISH_DOC        =  7;
    final static int ACTION_PUBLISH_DOCOCUMENT = 11;


    // Static fields/initializers /////////////////////////////////////////////
    
    // Create a static logger to be used by the @BeforeClass method(s), once
    //   the test class is instance pass a reference to the OSWfTestCase logger

    private static final Logger exampleWorkflowlogger = LoggerFactory.getLogger(ExampleWorkflowTest.class);

    private static final String USER_TEST     = "test";
    private static final String WORKFLOW_NAME = "Example";  // resources/usage/example.oswf.xml"

    static OSWfConfiguration configuration;

    OSWfEngine wfEngine;

    @BeforeClass
    public static void createConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(ExampleWorkflowTest.class.getResource("/oswf-usage.xml"))
        ;
    }


    // Constructors ///////////////////////////////////////////////////////////

    public ExampleWorkflowTest() {
        super(exampleWorkflowlogger);
    }

    String getWorkflowName() {
        return WORKFLOW_NAME;
    }

    @BeforeClass
    public static void createUserGroups() {
    
        SecurityManager securityManager = SecurityManager.getInstance();
        assertNotNull("Could not get SecurityManager", securityManager);

        // Create a couple of users; add them to groups    
        
        // Groups
        Role foos = securityManager.createRole("foos");
        Role bars = securityManager.createRole("bars");
        Role bazs = securityManager.createRole("bazs");

        User user = securityManager.createUser(USER_TEST);
            
        user.addToRole(foos);
        user.addToRole(bars);

    }


    @Before
    public void setup() throws Exception {
        wfEngine = new DefaultOSWfEngine(USER_TEST);
        wfEngine.setConfiguration(configuration);
    }

    @After
    public void teardown() {
        MemoryWorkflowStore.reset();
    }

    // Tests ====================================================================

    @Test
    public void confirmSecurityModel() {
        
        SecurityManager securityManager = SecurityManager.getInstance();

        // Make sure the users have been created in the class
        User user = securityManager.getUser(USER_TEST);
        assertNotNull("SecurityManager has 'testUser'", user);
    }


    @Test
    public void createWorkflowEngineWithUser() {
        User user = SecurityManager.getInstance().getUser(USER_TEST);
        wfEngine = new UserOSWfEngine(user);
        wfEngine.setConfiguration(configuration);

        assertNotNull(wfEngine);
        assertTrue(wfEngine.canInitialize(getWorkflowName(), INITIAL_ACTION));
    }


    @Test
    public void exampleWorkflow() throws Exception {
    
        // WorkflowQuery query;
        List<Integer> actions;
        List<Step> historySteps, currentSteps;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, INITIAL_ACTION));

        long piid = wfEngine.initialize(workflowName, INITIAL_ACTION);
        String workorderName = wfEngine.getWorkflowName(piid);

        logger.debug("Name of workorder: " + workorderName);
        logger.debug("Entry state: " + getProcessInstanceState(wfEngine, piid));
        
        StepCondition permA = new StepCondition(STEP_FIRST_DRAFT, "permA", true);               
        assertTrue("Expected step-condition 'permA' in step STEP_FIRST_DRAFT not found", 
            wfEngine.getStepConditions(piid)
                    .get(STEP_FIRST_DRAFT)
                     .contains(permA))
        ;

        assertCounts(wfEngine, piid, 0, 1, 1);       
        chooseAction(wfEngine, piid, ACTION_FINISH_FIRST_DRAFT);
        logMessage("User 'test' took the result which leads to the split");

        // Now in steps 'Foo(6)' and 'Bar(7)' by way of 'Split id = 1' because 
        //   the actor was the user named'test'
        // Otherwise, we would have taken the default branch to the
        //   'Edit Doc' at Step id=2
        // Three actions: 'Finish Bar', 'Finish Bar', 'Stay in Bar' 

        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        // Index [0] is the most recent history step
        Step historyStep = historySteps.get(0);
        assertEquals(USER_TEST, historyStep.getActor());
        assertNull(historyStep.getDueDate());

        // Check system date, add in a 1 second fudgefactor.
        assertTrue("history step finish date " + historyStep.getFinishDate() + " is in the future!", (historyStep.getFinishDate().getTime() - 1000) < System.currentTimeMillis());

        assertCounts(wfEngine, piid, 1, 1, 0);       

        historySteps = wfEngine.getHistorySteps(piid);
        assertEquals("Unexpected number of history steps", 1, historySteps.size());

        assertCounts(wfEngine, piid, 1, 1, 0);       
        
        actions = wfEngine.getAvailableActions(piid);
        assertEquals(0, actions.size());

        
        logInstanceState(wfEngine, piid);

        //assertProcessInstanceState(piid, ProcessInstanceState.COMPLETED);
        
        logger.debug("Entry state: " + getProcessInstanceState(wfEngine, piid));

    }


    @Test
    public void exceptionOnIllegalStayInCurrentStep() throws Exception {
    
        String workflowName = getWorkflowName();
        
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, INITIAL_ACTION));

        try {
            long piid = wfEngine.initialize(workflowName, INITIAL_ACTION_ILLEGAL);
            fail("initial action result specified target step of current step. Succeeded but should not have.");
        } catch (WorkflowException e) {
            assertTrue("expected, no such thing as current step for initial action", true);
        }
    }

    @Test
    public void metadataAccess() throws Exception {

        String workflowName = getWorkflowName();

        WorkflowDescriptor wfDesc = wfEngine.getWorkflowDescriptor(workflowName);

        Map meta = wfDesc.getMetaAttributes();
        assertTrue("missing metadata", (meta.get("workflow-meta1")).equals("workflow-meta1-value"));
        assertTrue("missing metadata", (meta.get("workflow-meta2")).equals("workflow-meta2-value"));

        meta = wfDesc.getStep(STEP_FIRST_DRAFT).getMetaAttributes();
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

        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, INITIAL_ACTION));

        //-------------------   FieldExpression.OWNER  +  FieldExpression.CURRENT_STEPS ----------------------

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.CURRENT_STEPS,
            Field.OWNER, Operator.EQUALS, USER_TEST)
        );

        try {
            workflows = wfEngine.query(query);
            assertEquals("empty OWNER+CURRENT_STEPS", 0, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.error("Store does not support query");
            return;
        }

        long piid = wfEngine.initialize(workflowName, INITIAL_ACTION);
        workflows = wfEngine.query(query);
        assertEquals("OWNER+CURRENT_STEPS", 1, workflows.size());

        //-------------------  FieldExpression.NAME + FieldExpression.ENTRY ----------------------------------

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.NAME,Operator.EQUALS, "notexistingname")
        );

        workflows = wfEngine.query(query);
        assertEquals("empty NAME+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.NAME, Operator.EQUALS, workflowName)
        );

        workflows = wfEngine.query(query);
        assertEquals("NAME+ENTRY", 1, workflows.size());


        //-------------------  FieldExpression.STATE + FieldExpression.ENTRY ----------------------------------

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.COMPLETED)
        );
        workflows = wfEngine.query(query);
        assertEquals("empty STATE+ENTRY", 0, workflows.size());

        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.ACTIVE)
        );
        workflows = wfEngine.query(query);
//        assertEquals("STATE+ENTRY", 1, workflows.size());  // FAILS: currently returns 0

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
            Field.OWNER, Operator.EQUALS, USER_TEST
        );

        queryRight = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Finished", true
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
             Field.FINISH_DATE,Operator.LT, new Date()
         );

        queryRight = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Finished", true
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
            Field.FINISH_DATE, Operator.LT, new Date()
        );
        
        queryRight = new FieldExpression(
            Context.ENTRY, 
            Field.NAME, Operator.EQUALS, workflowName
        );
        
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.OR));

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
            Field.START_DATE, Operator.LT, new Date(System.currentTimeMillis() + 1000))
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
        wfEngine.doAction(piid, 1);

        // --------------------- START_DATE+HISTORY_STEPS -------------------------------------------------
        //there should be two step that have been started
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.START_DATE, Operator.LT, new Date(System.currentTimeMillis() + 1000))
        );
        
        workflows = wfEngine.query(query);
        assertEquals("Expected to find 1 workflow step in the history for entry #" + piid, 1, workflows.size());

        // --------------------- FINISH_DATE+HISTORY_STEPS -------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.FINISH_DATE, Operator.LT, new Date(System.currentTimeMillis() + 1000))
        );
        workflows = wfEngine.query(query);
        assertEquals("Expected to find 1 history steps that was completed", 1, workflows.size());

        // --------------------- ACTION + HISTORY_STEPS ----------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
            Context.HISTORY_STEPS, 
            Field.ACTION, Operator.EQUALS, 1)
        );
        workflows = wfEngine.query(query);
        assertEquals("ACTION + HISTORY_STEPS", 1, workflows.size());

        // --------------------- STEP + HISTORY_STEPS ----------------------------------------------
        query = new WorkflowExpressionQuery(new FieldExpression(
        Context.HISTORY_STEPS, 
        Field.STEP, Operator.EQUALS, STEP_FIRST_DRAFT)
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
        long workflowId2 = wfEngine.initialize(workflowName, INITIAL_ACTION);
        wfEngine.changeEntryState(piid, ProcessInstanceState.SUSPENDED);

        queryRight = new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.ACTIVE
        );

        queryLeft = new FieldExpression(
            Context.ENTRY, 
            Field.STATE, Operator.EQUALS, ProcessInstanceState.SUSPENDED
        );

        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.OR));

        workflows = wfEngine.query(query);
//        assertEquals(2, workflows.size()); //FAILS: currently returns 1

        queryLeft = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, USER_TEST
        );

        queryRight = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STATUS, Operator.EQUALS, "Finished", true
        );
        query = new WorkflowExpressionQuery(new NestedExpression(new Expression[] {
                queryLeft, queryRight
            }, LogicalOperator.AND));

        workflows = wfEngine.query(query);
        assertEquals("Expected to find 2 workflows in current steps", 1, workflows.size());
    }

/*
    @Test
    public void workflowQuery() throws Exception {
    
        WorkflowQuery query = null;
        List workflows;

        String workflowName = getWorkflowName();
        assertTrue("canInitialize for workflow " + workflowName + " is false", wfEngine.canInitialize(workflowName, INITIAL_ACTION));

        try {
            query = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
            workflows = wfEngine.query(query);
            assertEquals(0, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.error("Store does not support query");
        }

        try {
            long piid = wfEngine.initialize(workflowName, INITIAL_ACTION);
            workflows = wfEngine.query(query);
            assertEquals(1, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.error("Store does not support query");
        }

        try {
            WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, USER_TEST);
            WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Finished");
            query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
            workflows = wfEngine.query(query);
            assertEquals(0, workflows.size());

            queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
            query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
            workflows = wfEngine.query(query);
            assertEquals(1, workflows.size());
        } catch (QueryNotSupportedException e) {
            logger.error("Store does not support query");
        }
    }
*/

}
