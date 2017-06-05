package usage;

import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Core classes
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.exceptions.InvalidActionException;
import org.informagen.oswf.ProcessInstanceState;

// OSWf Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf Store - Use in-memory Java Collection classes
import org.informagen.oswf.impl.stores.MemoryStore;

// OSWf Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// Java Util
import java.util.Collections;
import java.util.List;

// Logging
import org.slf4j.LoggerFactory;


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


/*
**  Implement workflow concept of Role and User work lists using OSWf's Step 'owner'.  This
**      usage example uses the same 'Leave Request' workflow with the addition of Step id=250
**      which allow a 'Manager' group member to 'claim' a request before moving into Step
**      id=250 and becoming the owner in Step id=250.
**
**  Claiming a request removes it from a pool of Manager group pending requests.  No other
**      manager can Approve or Deny this request.
**
**  A request in Step id=250 has three actions. The familiar actions of 'Approve' or 'Deny'
**      and a new action 'Release' which a manager can perform to return the request to
**      to the manager group work list.
**
**  Uses:  src/test/resources/usage/StepClaim.oswf.xml
**
*/

public class WorkListTest extends OSWfTestCase implements Constants {

    static OSWfConfiguration configuration;

    public WorkListTest() {
        logger = LoggerFactory.getLogger(WorkListTest.class.getName());
    }


    @BeforeClass
    public static void defineConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration();
        configuration.load(WorkListTest.class.getResource("/oswf-usage.xml"));
    }

    /*
    ** Create a set of employees in the SecurityManager
    **  Create two 'managers' in order to test claiming and releasing a work item
    **
    */

    @BeforeClass
    public static void createAuthorization() {

        SecurityManager securityManager = SecurityManager.getInstance();
            
        assertNotNull("Could not get SecurityManager", securityManager);

        // Create a set of users; add them to groups; In production
        //  this would be managed by an RDBMS
                   
        // Groups
        Role employees = securityManager.createRole("Employees");
        Role managers = securityManager.createRole("Managers");
        Role payroll = securityManager.createRole("Payroll");

        // Create 'Joe', an employee
        User joe = securityManager.createUser("Joe");
        joe.addToRole(employees);

        // Create 'Bob', a manager
        User bob = securityManager.createUser("Bob");
        bob.addToRole(employees);
        bob.addToRole(managers);

        // Create 'Charlie', another manager
        User charlie = securityManager.createUser("Charlie");
        charlie.addToRole(employees);
        charlie.addToRole(managers);
        
        // Create 'Doris Despised', the much loved Human Resources Director
        User doris = securityManager.createUser("Doris");
        doris.addToRole(employees);
        doris.addToRole(payroll);
            
    }


    @After
    public void teardown() {
        MemoryStore.reset();
    }

    // Tests ==================================================================================
    
    @Test
    public void confirmSecurity() {

        SecurityManager securityManager = SecurityManager.getInstance();

        assertNotNull(securityManager.getUser("Joe"));
        assertNotNull(securityManager.getUser("Bob"));
        assertNotNull(securityManager.getUser("Charlie"));
        assertNotNull(securityManager.getUser("Doris"));
        
        assertTrue(securityManager.getUser("Joe").hasRole("Employees"));
        
        assertTrue(securityManager.getUser("Bob").hasRole("Employees"));
        assertTrue(securityManager.getUser("Bob").hasRole("Managers"));
        
        assertTrue(securityManager.getUser("Charlie").hasRole("Employees"));
        assertTrue(securityManager.getUser("Charlie").hasRole("Managers"));
        
        assertTrue(securityManager.getUser("Doris").hasRole("Employees"));
        assertTrue(securityManager.getUser("Doris").hasRole("Payroll"));
    
    }

    @Test
    public void canCreateWorkflowEngine() {

        OSWfEngine wf;
        
        wf = new DefaultOSWfEngine("Joe");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Work List", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Bob");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Work List", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Doris");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Work List", INITIAL_ACTION));
        
    }


    @Test
    public void canStartWorkflow() throws Exception {

        OSWfEngine wf = new DefaultOSWfEngine("Joe");
        wf.setConfiguration(configuration);
        
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Work List", INITIAL_ACTION));
        
        long piid = wf.initialize("Work List", INITIAL_ACTION);
        wf.doAction(piid, REQUEST_LEAVE);
        
    }



    @Test
    public void managerClaimWorkItem() throws Exception {

        OSWfEngine joe, bob, charlie;

        // Create engines for Joe, Bob and Charlie
        joe = createOSWfEngine("Joe");
        bob = createOSWfEngine("Bob");
        charlie = createOSWfEngine("Charlie");

        long piid = joe.initialize("Work List", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        // Either Bob or Charlie can claim the request
        assertEquals(1, bob.getAvailableActions(piid).size());
        assertEquals(1, charlie.getAvailableActions(piid).size());

        // Bob claims the request
        bob.doAction(piid, CLAIM_WORK_ITEM);

        // Bob can 'Approve', 'Deny' or 'Release' the request; Charlie has nothing
        assertEquals(3, bob.getAvailableActions(piid).size());
        assertEquals(0, charlie.getAvailableActions(piid).size());

        // Bob releases the request
        bob.doAction(piid, RELEASE_WORK_ITEM);

        // Either Bob or Charlie can now claim the request as before
        assertEquals(1, bob.getAvailableActions(piid).size());
        assertEquals(1, charlie.getAvailableActions(piid).size());

        // Charlie claims the request
        charlie.doAction(piid, CLAIM_WORK_ITEM);

        // Charlie can 'Approve', 'Deny' or 'Release' the request; Bob has nothing
        assertEquals(3, charlie.getAvailableActions(piid).size());
        assertEquals(0, bob.getAvailableActions(piid).size());

        // Charlie as a Manager approves the reqeust
        charlie.doAction(piid, LINE_MANAGER_DENIES);
        assertEquals(0, charlie.getAvailableActions(piid).size());

        //joe.doAction(piid, NOTIFY_EMPLOYEE);

        assertEquals("pending", charlie.getPersistentVars(piid).getString("result"));


        assertEquals(ProcessInstanceState.ACTIVE, charlie.getProcessInstanceState(piid));


    }


    @Test
    public void workingWithWorkLists() throws Exception {

        OSWfEngine joe, bob, charlie, doris;
        List<Long> workList;
        
        // Create engines for Joe, Bob and Charlie
        joe = createOSWfEngine("Joe");
        bob = createOSWfEngine("Bob");
        charlie = createOSWfEngine("Charlie");
        doris = createOSWfEngine("Doris");

        // Have Joe create 3 requests
        joe.doAction(joe.initialize("Work List", INITIAL_ACTION), REQUEST_LEAVE);
        joe.doAction(joe.initialize("Work List", INITIAL_ACTION), REQUEST_LEAVE);
        joe.doAction(joe.initialize("Work List", INITIAL_ACTION), REQUEST_LEAVE);
                
        // Note: this workList query is not affected by the invoking actor
        assertEquals(3, joe.query(createWorkListQuery("Managers",     LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(3, bob.query(createWorkListQuery("Managers",     LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(3, charlie.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(3, doris.query(createWorkListQuery("Managers",   LINE_MANAGER_CLAIM_STEP)).size());

        // We can also query for users who are owners of the Claim step
        assertEquals(0, joe.query(createWorkListQuery("Joe",         LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(0, bob.query(createWorkListQuery("Bob",         LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(0, charlie.query(createWorkListQuery("Charlie", LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(0, doris.query(createWorkListQuery("Doris",     LINE_MANAGER_CLAIM_STEP)).size());

        // Now have Bob 'claim' one of the request
        workList = bob.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP));
        assertTrue(workList.size() > 0);
        bob.doAction(workList.get(0), CLAIM_WORK_ITEM);

        // The 'Manager's work list is reduced by 1, Bob has picked up a work item
        //  Charlie still has no work items
        assertEquals(2, bob.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(1, bob.query(createWorkListQuery("Bob",      LINE_MANAGER_REVISION_STEP)).size());
        assertEquals(0, bob.query(createWorkListQuery("Charlie",  LINE_MANAGER_REVISION_STEP)).size());

        // Now have Charlie 'claim' one of the remaining requests
        workList = charlie.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP));
        assertTrue(workList.size() > 0);
        charlie.doAction(workList.get(0), CLAIM_WORK_ITEM);

        // The 'Manager's work list is reduced by another 1, Bob still has his
        //  request and Charlie has picked up a request.  There remains a single
        //  request in 
        assertEquals(1, charlie.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(1, charlie.query(createWorkListQuery("Bob",      LINE_MANAGER_REVISION_STEP)).size());
        assertEquals(1, charlie.query(createWorkListQuery("Charlie",  LINE_MANAGER_REVISION_STEP)).size());

        // Let's have Bob decide not to work on his request; returning it to the
        //  'Manager' workList
        workList = bob.query(createWorkListQuery("Bob", LINE_MANAGER_REVISION_STEP));
        assertTrue(workList.size() > 0);
        bob.doAction(workList.get(0), RELEASE_WORK_ITEM);

        // As expected, Bob now has no work items, Chalie still has one and there
        //  are two in the Managers group
        assertEquals(2, bob.query(createWorkListQuery("Managers", LINE_MANAGER_CLAIM_STEP)).size());
        assertEquals(0, bob.query(createWorkListQuery("Bob",      LINE_MANAGER_REVISION_STEP)).size());
        assertEquals(1, bob.query(createWorkListQuery("Charlie",  LINE_MANAGER_REVISION_STEP)).size());
    }

    private OSWfEngine createOSWfEngine(String actor) {
        OSWfEngine oswfEngine = new DefaultOSWfEngine(actor);
        oswfEngine.setConfiguration(configuration);
        return oswfEngine;
    }

    /*
    ** Build a Work List based on the owner of a spefic stepId
    */

    private WorkflowExpressionQuery createWorkListQuery(String owner, int stepId) {
        
        Expression ownerExpression = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, owner
        );
        
        Expression stepExpression = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.STEP,  Operator.EQUALS,  stepId
        );
 
         WorkflowExpressionQuery query = new WorkflowExpressionQuery(
            new NestedExpression(
                new Expression[] {
                        ownerExpression, 
                        stepExpression
                }, LogicalOperator.AND)
            );
            
        return query;       
    }
    

}
