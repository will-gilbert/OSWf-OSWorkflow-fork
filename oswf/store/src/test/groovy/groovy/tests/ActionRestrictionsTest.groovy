package groovy.tests;

import support.OSWfHibernateTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

// Action exceptions
import org.informagen.oswf.exceptions.InvalidActionException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.Step;

import org.informagen.oswf.PeristentVarsStore;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// OSWf Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

// OSWf delegate which installs a Custom TypedMap mapping
import org.informagen.oswf.impl.HibernateTypedMapStore;

// Java Util
import java.util.Collection;
import java.util.Collections;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Basic workflow flow
 */


public class ActionRestrictionsTest extends support.OSWfHibernateTestCase implements usage.Constants {

    public static final String RDBMS_CONFIGURATION = System.getProperty("rdbms-configuration");


    OSWfConfiguration configuration;

    public ActionRestrictionsTest() {
        super("oswf-store.cfg.xml", RDBMS_CONFIGURATION);
    }

    @BeforeClass
    public static void createSecurityModel() {
    
        def securityManager = SecurityManager.getInstance();         
        assert securityManager

        // Create a set of users; Add them to groups
        //
        // In production this would be managed by an RDBMS
        //  by creating Users and Roles from the native security system
                   
        // Roles
        Role employee = securityManager.createRole("Employee");
        Role manager = securityManager.createRole("Line Manager");
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


    @Before
    public void hibernateConfiguration() throws Exception {
                    
        configuration = new DefaultOSWfConfiguration()
            .load(getClass().getResource("/oswf-hibernate.xml"))
            .addPersistenceArg("sessionFactory", getSessionFactory())
        ;
    }


    @After
    public void teardown() {
        closeSession();
        closeSessionFactory();
    }

    // Tests ==================================================================================
    
    @Test
    void verifySecurityModel() {

        SecurityManager securityManager = SecurityManager.getInstance()
        
        assert securityManager.getUser('Joe Average')
        assert securityManager.getUser('Bob Bossman')
        assert securityManager.getUser('Doris Despised')
        
        assert securityManager.getUser('Joe Average').hasRole('Employee')
        
        assert securityManager.getUser('Bob Bossman').hasRole('Employee')
        assert securityManager.getUser('Bob Bossman').hasRole('Line Manager')
        
        assert securityManager.getUser('Doris Despised').hasRole('Employee')
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
                
        def joe, piid
        
        // Any company employee can create a "Action Restrictions" process instance

        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration)
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        joe.doAction(piid, REQUEST_LEAVE)
                
        joe = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration)
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        joe.doAction(piid, REQUEST_LEAVE)
        
        joe = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration)
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION)
        assert piid
        joe.doAction(piid, REQUEST_LEAVE)
 
        joe = new DefaultOSWfEngine("Curtis Contractor").setConfiguration(configuration)
 
        try {
            piid = joe.initialize("Action Restrictions", INITIAL_ACTION)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }
    }

    @Test
    void managerApproval() {
        
        // Managers can approve or deny employee leave requests
        
        def joe, bob, piid

        // Joe requests leave
        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration)
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        // The persistent variable from the workflow
        assert "pending" == joe.getPersistentVars(piid).getString("result")

        // The Line Manage 'logs in' to the workflow
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        assert 2 == bob.getCurrentSteps(piid).size()

        // The process instance exists in 2 steps; This has nothing to
        //   to do with Bob's actions. Any OSWfEngine instance would give 
        //   the same steps.

        def wd = bob.getWorkflowDescriptor('Action Restrictions')
        def steps =  bob.getCurrentSteps(piid).collect {
            wd.getStep(it.stepId).name
        }

        // The 2 steps for this process instance
        assert steps.contains('Line Manager Approval')
        assert steps.contains('HR Manager Approval')

        // On the other hand, here are Bob's actions vs. Joe's
        def actions =  bob.getAvailableActions(piid).sort()
        assert actions == [LINE_MANAGER_APPROVES, LINE_MANAGER_DENIES]
        assert [] == joe.getAvailableActions(piid)

        // 'Bob Bossman' can not perform the HR approval action
        try {
            bob.doAction(piid, HUMAN_RESOURCES_APPROVES)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }

        // But can not perform the Manager approval action
        bob.doAction(piid, LINE_MANAGER_APPROVES)

        // Process Instance has not yet completed
        // Even though the manager has approved the process instance is waiting for 
        //  the Human Resources approval.

        assert ProcessInstanceState.ACTIVE == bob.getProcessInstanceState(piid)

        // Joe can check on the progress of the request
        assert "pending" == joe.getPersistentVars(piid).getString("result")
    }

    
    @Test
    void humanResourcesApproval() {
        
        // The HR Director can approve employee leave requests in lieu of a Manager
        
        def joe, doris, piid

        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);
        piid = joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        assert "pending" == joe.getPersistentVars(piid).getString("result")
          
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);

        def actions =  doris.getAvailableActions(piid).sort()
        assert actions == [HUMAN_RESOURCES_APPROVES, HUMAN_RESOURCES_DENIES]


        // 'Doris Despised' can not perform the Line Manger approval action
        try {
            doris.doAction(piid, LINE_MANAGER_APPROVES)
            fail()
        } catch(InvalidActionException invalidActionException) {
        }

        // But can not perform the HR Manager approval action
        doris.doAction(piid, HUMAN_RESOURCES_APPROVES);

        // Process Instance has not yet completed
        // Even though the HR Manager has approved the process instance is waiting for 
        //  the Line Manager approval. Here 'Joe' checks the progress

        assert ProcessInstanceState.ACTIVE == joe.getProcessInstanceState(piid)
        assert "pending" == joe.getPersistentVars(piid).getString("result")
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
        assert [LINE_MANAGER_APPROVES, LINE_MANAGER_DENIES] == bob.getAvailableActions(piid).sort() 
        assert [HUMAN_RESOURCES_APPROVES, HUMAN_RESOURCES_DENIES] == doris.getAvailableActions(piid).sort() 

        // Both approve
        bob.doAction(piid, LINE_MANAGER_APPROVES)
        doris.doAction(piid, HUMAN_RESOURCES_APPROVES)

        // Joe checks the progress and 'Yeah' he gets the day off.
        assert ProcessInstanceState.COMPLETED == doris.getProcessInstanceState(piid)
        assert "approved" == joe.getPersistentVars(piid).getString("result")
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

        // Joe checks the progress.
        assert "denied" == joe.getPersistentVars(piid).getString("result")
        assert ProcessInstanceState.COMPLETED == doris.getProcessInstanceState(piid)
    }


    @Test
    void hrApprovesManagerDenies() {

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
        bob.doAction(piid, LINE_MANAGER_DENIES)
        doris.doAction(piid, HUMAN_RESOURCES_APPROVES)

        // Joe checks the progress.
        assert ProcessInstanceState.COMPLETED == joe.getProcessInstanceState(piid)
        assert "denied" == joe.getPersistentVars(piid).getString("result")
    }


 
    /*  
    **  This long test merely shows that Action Restriction cannot be used to 
    **     define step ownership, which is used to define a Task List.
    **
    **  See 'StepOwnershipTest' and 'WorkListTest' for examples.
    */

    @Test
    public void cannotCreateWorkList() throws Exception {

        def joe, bob, doris;

        def piids = []

        // Create some actors
        joe = new DefaultOSWfEngine("Joe Average") .setConfiguration(configuration);
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration);
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration);

        // Let 'Joe' create three leave requests; 
        piids << joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);
        
        piids <<  joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);
        
        piids <<  joe.initialize("Action Restrictions", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);

        
        // Can't obtain a work list based on "Action Restrictions" roles

        Expression manager = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER,  Operator.EQUALS, "Line Manager"
        );
        
        WorkflowExpressionQuery query = new WorkflowExpressionQuery(manager);
        def workList = bob.query(query);
        assert piids != workList
 

        Expression hrDirector = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "HR Director"
        );

        query = new WorkflowExpressionQuery(hrDirector);
        workList = doris.query(query);
        assert piids != workList
   
    }    

}
