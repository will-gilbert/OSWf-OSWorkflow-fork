package usage;

import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.ProcessInstance;

// OSWf - Action exceptions
import org.informagen.oswf.exceptions.InvalidActionException;

// OSWf - Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

// OSWf - Store
import org.informagen.oswf.impl.stores.MemoryStore;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// Logging
import org.slf4j.LoggerFactory;

// // Java  - Collections
import java.util.List;


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
 *  Exercises OSWf Action restrictions
 *
 *  Uses:  src/test/resources/usage/ActionRestrictions.oswf.xml
 *
 */


public class ActionRestrictionsTest extends OSWfTestCase implements Constants {

    static OSWfConfiguration configuration;
    
    public ActionRestrictionsTest() {
        logger = LoggerFactory.getLogger(ActionRestrictionsTest.class.getName());
    }

    @BeforeClass
    public static void createOSWfConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(ActionRestrictionsTest.class.getResource("/oswf-usage.xml"))
        ;
    }

    @BeforeClass
    public static void createSecurityModel() {
 
        SecurityManager securityManager = SecurityManager.getInstance();
          
        assertNotNull("Could not get SecurityManager", securityManager);

        // Create a set of users; add them to groups;
        // In production this would be managed by an RDBMS
                   
        // Groups
        Role employee = securityManager.createRole("Employees");
        Role manager = securityManager.createRole("Line Managers");
        Role hr = securityManager.createRole("HR Director");

        // Create 'Joe Average', an employee
        User joe = securityManager.createUser("Joe Average");
        joe.addToRole(employee);

        // Create 'Bob Bossman', a manager
        User bob = securityManager.createUser("Bob Bossman");
        bob.addToRole(employee);
        bob.addToRole(manager);
        
        // Create 'Doris Despised', the much loved Human Resources Director
        User doris = securityManager.createUser("Doris Despised");
        doris.addToRole(employee);
        doris.addToRole(hr);
            
    }

    @After
    public void teardown() {
        MemoryStore.reset();
    }

    // Tests ==================================================================================
    
    @Test
    public void confirmSecurityModel() {

        SecurityManager securityManager = SecurityManager.getInstance();
        
        assertNotNull(securityManager.getUser("Joe Average"));
        assertNotNull(securityManager.getUser("Bob Bossman"));
        assertNotNull(securityManager.getUser("Doris Despised"));
        
        assertTrue(securityManager.getUser("Joe Average").hasRole("Employees"));
        
        assertTrue(securityManager.getUser("Bob Bossman").hasRole("Employees"));
        assertTrue(securityManager.getUser("Bob Bossman").hasRole("Line Managers"));
        
        assertTrue(securityManager.getUser("Doris Despised").hasRole("Employees"));
        assertTrue(securityManager.getUser("Doris Despised").hasRole("HR Director"));
    
    }

    @Test
    public void canCreateRequestLeaveProcess() {

        // Any company employee can create a "Action Restrictions" process instance but
        //  consultants can't

        OSWfEngine wf;
        
        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
    
        // Non-employees i.e. contractors cannot use this workflow to create a
        //  process due to action restrictions on INITIAL_ACTION
        
        wf = new DefaultOSWfEngine("Curtis Contractor");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertFalse(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
    
    }

    @Test
    public void canRequestLeave() throws Exception {
        
        // Any company employee can request leave
        
        OSWfEngine wf;
        long piid;
        
        // Any company employee can create a "Action Restrictions" process instance
        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
                
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
 
        wf = new DefaultOSWfEngine("Curtis Contractor");
        wf.setConfiguration(configuration);
 
        try {
            piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
            fail();
        } catch(InvalidActionException invalidActionException) {
            assertTrue(true);
        }
    }


    @Test
    public void managerApproval() throws Exception {
        
        // Managers can approve employee leave requests
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        wf.doAction(piid, REQUEST_LEAVE);
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        wf.doAction(piid, LINE_MANAGER_APPROVES);

    }

    
    @Test
    public void hrDirectorApproval() throws Exception {
        
        // The HR Director can approve employee leave requests in lieu of a Manager
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        wf.doAction(piid, REQUEST_LEAVE);
  
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        wf.doAction(piid, LINE_MANAGER_APPROVES);
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        wf.doAction(piid, HUMAN_RESOURCES_APPROVES);
 
        assertProcessInstanceState(wf, piid, ProcessInstanceState.COMPLETE);
        assertProperty(wf, piid, "result", "approved");
    }

    
    @Test
    public void employeeCannotApprove() throws Exception {
        
        // An Employee cannot supply Manager or HR approvals
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        assertEquals(1, wf.getAvailableActions(piid).size());

        wf.doAction(piid, REQUEST_LEAVE);
        
        try{
            wf.doAction(piid, LINE_MANAGER_APPROVES);
            fail();
        } catch (InvalidActionException invalidActionException) {
            assertTrue(true);
        }
        
        try{
            wf.doAction(piid, HUMAN_RESOURCES_APPROVES);
            fail();
        } catch (InvalidActionException invalidActionException) {
            assertTrue(true);
        }

    }


    @Test
    public void lineManagersCannotApproveSelf() throws Exception {
                
        // Managers can approve employee leave requests
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        wf.doAction(piid, REQUEST_LEAVE);
                
        // Bob has no actions for this request even though he is a Line Manager
        assertEquals(0, wf.getAvailableActions(piid).size());
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        assertEquals(2, wf.getAvailableActions(piid).size());
        
    }


    @Test
    public void hrCannotApproveSelf() throws Exception {
                
        // Managers can approve employee leave requests
        OSWfEngine wf;
        long piid;
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Action Restrictions", INITIAL_ACTION);
        assertEquals(1, wf.getAvailableActions(piid).size());
        wf.doAction(piid, REQUEST_LEAVE);

        // Doris has no actions for this request even though she is in HR
        assertEquals(0, wf.getAvailableActions(piid).size());
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        assertEquals(2, wf.getAvailableActions(piid).size());
        
    }
 
    /*  
    **  This long test merely shows that Action Restriction cannot be used to 
    **      define step ownership, which is used to define a Task List.
    **
    **  See the last few lines of code
    */

    @Test
    public void createTaskList() throws Exception {

        OSWfEngine joe, bob, doris;
        WorkflowExpressionQuery query;
        Expression manager, hrDirector;

        manager = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER,  Operator.EQUALS, "Line Managers"
        );

        hrDirector = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "HR Director"
        );

        // Let 'Joe' create three leave requests
        joe = new DefaultOSWfEngine("Joe Average");
        joe.setConfiguration(configuration);

        long piid1 = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid1, REQUEST_LEAVE);
        
        long piid2 = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid2, REQUEST_LEAVE);
        
        long piid3 = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid3, REQUEST_LEAVE);

        // Now create workflow for 'Bob' the boss so he can approve one of
        //  the leaves for 'Joe'
        
        bob = new DefaultOSWfEngine("Bob Bossman");
        bob.setConfiguration(configuration);

        
        // Can't obtain a task list based on "Action Restrictions" 
        //  See the 'createTaskListFromStepOwnership' unit test in 'StepOwnershipTest.java'
        
        query = new WorkflowExpressionQuery(manager);
        List taskList = bob.query(query);
        assertEquals(0, taskList.size());
    
    }    

}
