package groovy.usage;

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


class ActionRestrictionsTest implements usage.Constants {

    static OSWfConfiguration configuration;
    
    @BeforeClass
    static void createOSWfConfiguration() throws Exception {

        // Use in memory store/property set; Load the configuration
        //   in order to access workflows by name.

        configuration = new MemoryOSWfConfiguration()
            .load(ActionRestrictionsTest.class.getResource("/oswf-usage.xml"))
        ;
    }

    @BeforeClass
    static void createSecurityModel() {
 
        def securityManager = SecurityManager.getInstance();
          
        assert securityManager

        // Create a set of users; Add them to groups
        //
        // In production this would be managed by an RDBMS
        //  by createing Users and Roles from the native security system
                   
        // Roles
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
    void teardown() {
        MemoryStore.reset();
    }

    // Tests ==================================================================================
    
    @Test
    void verifySecurityModel() {

        SecurityManager securityManager = SecurityManager.getInstance()
        
        assert securityManager.getUser('Joe Average')
        assert securityManager.getUser('Bob Bossman')
        assert securityManager.getUser('Doris Despised')
        
        assert securityManager.getUser('Joe Average').hasRole('Employees')
        
        assert securityManager.getUser('Bob Bossman').hasRole('Employees')
        assert securityManager.getUser('Bob Bossman').hasRole('Line Managers')
        
        assert securityManager.getUser('Doris Despised').hasRole('Employees')
        assert securityManager.getUser('Doris Despised').hasRole('HR Director')
    
    }

    @Test
    void canCreateRequestLeaveProcess() {

        // Any company employees can create a "Action Restrictions" process instance.
        //  Contractors and consultants can't.

        def engine
        
        engine = new DefaultOSWfEngine('Joe Average').setConfiguration(configuration)
        assert engine
        assert engine.canInitialize('Action Restrictions', INITIAL_ACTION)
        
        engine = new DefaultOSWfEngine('Bob Bossman').setConfiguration(configuration)
        assert engine
        assert engine.canInitialize('Action Restrictions', INITIAL_ACTION)
        
        engine = new DefaultOSWfEngine('Doris Despised').setConfiguration(configuration)
        assert engine
        assert engine.canInitialize('Action Restrictions', INITIAL_ACTION)
    
        // Non-employees i.e. contractors cannot use this workflow to create a
        //  process due to 'action restrictions' on INITIAL_ACTION
        
        engine = new DefaultOSWfEngine('Charlie Contractor').setConfiguration(configuration)
        assert engine
        assert engine.canInitialize('Action Restrictions', INITIAL_ACTION) == false
    
    }

    @Test
    void employeesRequestLeave() {
                
        def engine, piid
        
        // Any company employee can create a "Action Restrictions" process instance

        engine = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration)
        piid = engine.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        engine.doAction(piid, REQUEST_LEAVE)
                
        engine = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration)
        piid = engine.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        engine.doAction(piid, REQUEST_LEAVE)
        
        engine = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration)
        piid = engine.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        engine.doAction(piid, REQUEST_LEAVE)
 
        engine = new DefaultOSWfEngine("Curtis Contractor").setConfiguration(configuration)
 
        try {
            piid = engine.initialize("Action Restrictions", INITIAL_ACTION)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }
    }


    @Test
    void managerApproval() {
        
        // Managers can approve employee leave requests
        
        def wfEngine, piid

        wfEngine = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        piid = wfEngine.initialize("Action Restrictions", INITIAL_ACTION);
        wfEngine.doAction(piid, REQUEST_LEAVE);

        assert "pending" == wfEngine.getPersistentVars(piid).getString("result")


        wfEngine = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        assert 2 == wfEngine.getCurrentSteps(piid).size()

        def wd = wfEngine.getWorkflowDescriptor('Action Restrictions')
        wfEngine.getCurrentSteps(piid).each {
            println wd.getStep(it.stepId).name
        }

        // 'Bob Bossman' can not perform the HR approval action
        try {
            wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }

        // But can not perform the Manager approval action
        wfEngine.doAction(piid, LINE_MANAGER_APPROVES)

        // Process Instance has not yet completed
        // Even though the manager has approved the process instance is waiting for 
        //  the Human Resources approval.

        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
        assert "pending" == wfEngine.getPersistentVars(piid).getString("result")
    }

    
    @Test
    void humanResourcesApproval() {
        
        // The HR Director can approve employee leave requests in lieu of a Manager
        
        def wfEngine, piid

        wfEngine = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        piid = wfEngine.initialize("Action Restrictions", INITIAL_ACTION);
        wfEngine.doAction(piid, REQUEST_LEAVE);

        assert "pending" == wfEngine.getPersistentVars(piid).getString("result")
          
        wfEngine = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);
        assert 2 == wfEngine.getCurrentSteps(piid).size()

        def wd = wfEngine.getWorkflowDescriptor('Action Restrictions')
        wfEngine.getCurrentSteps(piid).each {
            println wd.getStep(it.stepId).name
        }

        // 'Doris Despised' can not perform the Line Manger approval action
        try {
            wfEngine.doAction(piid, LINE_MANAGER_APPROVES)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }

        // But can not perform the Manager approval action
        wfEngine.doAction(piid, HUMAN_RESOURCES_APPROVES);

        // Process Instance has not yet completed
        // Even though the manager has approved the process instance is waiting for 
        //  the Human Resources approval
        assert ProcessInstanceState.ACTIVE == wfEngine.getProcessInstanceState(piid)
        assert "pending" == wfEngine.getPersistentVars(piid).getString("result")
    }

    
    @Test
    void employeeCannotApprove() {
        
        // An Employee cannot supply Manager or HR approvals
        
        def joe, piid

        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION);
        
        assert 1 == joe.getAvailableActions(piid).size()

        joe.doAction(piid, REQUEST_LEAVE);

        // The workflow has two current step but no actions available to 'Joe'
        assert 2 == joe.getCurrentSteps(piid).size()
        assert 0 == joe.getAvailableActions(piid).size()
        
        // Joe can't execute any actions, approvals or denials
        try{
            joe.doAction(piid, LINE_MANAGER_APPROVES);
            fail();
        } catch (InvalidActionException invalidActionException) {
            assertTrue(true);
        }
        
        try{
            joe.doAction(piid, HUMAN_RESOURCES_APPROVES);
            fail();
        } catch (InvalidActionException invalidActionException) {
            assertTrue(true);
        }

    }


    @Test
    public void lineManagersCannotApproveSelf() throws Exception {
                
        // Managers can't approve their own leave requests
        def bob, doris, piid

        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        piid = bob.initialize("Action Restrictions", INITIAL_ACTION);
        bob.doAction(piid, REQUEST_LEAVE);
                
        // Bob has no actions for this request even though he is a Line Manager
        assert 0 == bob.getAvailableActions(piid).size()
        
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);
        assert 2 == doris.getAvailableActions(piid).size() 

        doris.doAction(piid, HUMAN_RESOURCES_APPROVES)

        // Both Bob and Doris see the same process instance results
        assert ProcessInstanceState.ACTIVE == bob.getProcessInstanceState(piid)
        assert "pending" == doris.getPersistentVars(piid).getString("result")
 
        assert ProcessInstanceState.ACTIVE == doris.getProcessInstanceState(piid)
        assert "pending" == doris.getPersistentVars(piid).getString("result")
    }


    @Test
    void hrCannotApproveSelf() throws Exception {
                
        // HR Directors can't approve their own leave requests
        def doris, bob, piid;
        
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);
        piid = doris.initialize("Action Restrictions", INITIAL_ACTION);
        assertEquals(1, doris.getAvailableActions(piid).size());
        doris.doAction(piid, REQUEST_LEAVE);

        // Doris has no actions for this request even though she is in HR
        assertEquals(0, doris.getAvailableActions(piid).size());
        
        bob = new DefaultOSWfEngine("Bob Bossman");
        bob.setConfiguration(configuration);
        assertEquals(2, bob.getAvailableActions(piid).size());
        
        bob.doAction(piid, LINE_MANAGER_APPROVES)

        // Both Bob and Doris see the same process instance results
        assert ProcessInstanceState.ACTIVE == bob.getProcessInstanceState(piid)
        assert "pending" == doris.getPersistentVars(piid).getString("result")
 
        assert ProcessInstanceState.ACTIVE == doris.getProcessInstanceState(piid)
        assert "pending" == doris.getPersistentVars(piid).getString("result")
       
    }

    @Test
    void bothApprove() {

        // Managers can't approve their own leave requests
        def joe, bob, doris, piid

        // Create engines for each of the actors
        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);

        piid = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        assert "pending" == joe.getPersistentVars(piid).getString("result")

        // Both Bob and Doris have two actions, 'Approve' or 'Deny'
        assert 2 == bob.getAvailableActions(piid).size() 
        assert 2 == doris.getAvailableActions(piid).size() 

        // Both approve
        bob.doAction(piid, LINE_MANAGER_APPROVES)
        doris.doAction(piid, HUMAN_RESOURCES_APPROVES)

        assert "approved" == joe.getPersistentVars(piid).getString("result")
        assert ProcessInstanceState.COMPLETED == doris.getProcessInstanceState(piid)
    }

    @Test
    void managerApprovesHRDenies() {

        // Managers can't approve their own leave requests
        def joe, bob, doris, piid

        // Create engines for each of the actors
        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);

        piid = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        assert "pending" == joe.getPersistentVars(piid).getString("result")

        // Both Bob and Doris have two actions, 'Approve' or 'Deny'
        assert 2 == bob.getAvailableActions(piid).size() 
        assert 2 == doris.getAvailableActions(piid).size() 

        // Both approve
        bob.doAction(piid, LINE_MANAGER_APPROVES)
        doris.doAction(piid, HUMAN_RESOURCES_DENIES)

        assert "denied" == joe.getPersistentVars(piid).getString("result")
        assert ProcessInstanceState.COMPLETED == doris.getProcessInstanceState(piid)
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
