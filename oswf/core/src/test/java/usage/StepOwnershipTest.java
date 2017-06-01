package usage;

import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Core classes
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.InvalidActionException;

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
import org.informagen.oswf.query.FieldExpression;
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


/**
 *  Exercises OSWf Step ownership
 *
 *  Uses:  src/test/resources/usage/StepOwnership.oswf.xml
 *
 */

public class StepOwnershipTest extends OSWfTestCase implements Constants {

    static OSWfConfiguration configuration;

    public StepOwnershipTest() {
        super(LoggerFactory.getLogger(StepOwnershipTest.class.getName()));
    }


    @BeforeClass
    public static void defineConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration();
        configuration.load(StepOwnershipTest.class.getResource("/oswf-usage.xml"));
    }


    @BeforeClass
    public static void createAuthorization() {

        SecurityManager securityManager = SecurityManager.getInstance();
            
        assertNotNull("Could not get SecurityManager", securityManager);

        // Create a set of users; add them to groups; In production
        //  this would be managed by an RDBMS
                   
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
    public void confirmSecurity() {

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
    public void canUseRequestLeaveWorkflow() {

        // Any company employee can create a "Step Ownership" process instance

        OSWfEngine wf;
        
        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Step Ownership", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Step Ownership", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue(wf.canInitialize("Step Ownership", INITIAL_ACTION));
    
        // Non-employees i.e. contractor cannot use this workflow
        // But can initiate via the 'Contractor' initial action
        wf = new DefaultOSWfEngine("Curtis Contractor");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertFalse(wf.canInitialize("Step Ownership", INITIAL_ACTION));
        assertTrue(wf.canInitialize("Step Ownership", CONTRACTOR_ACTION));
    
    }

    @Test
    public void requestLeave() throws Exception {
        
        // Any company employee can request leave
        
        OSWfEngine wf;
        long piid;
        
        // Any company employee can create a "Step Ownership" process instance
        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
                
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
        assertTrue(piid > 0);
        wf.doAction(piid, REQUEST_LEAVE);
    }


    @Test
    public void managerApproval() throws Exception {
        
        // Managers can approve employee leave requests
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
        wf.doAction(piid, REQUEST_LEAVE);
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);

        logAvailableActions(wf, piid);
        wf.doAction(piid, LINE_MANAGER_APPROVES);

    }

    
    @Test
    public void hrDirectorApproval() throws Exception {

        logMessage("hrDirectorApproval");
        
        // The HR Director can approve employee leave requests in lieu of a Manager
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
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
    public void employeeApproval() throws Exception {
        
        // An Employee cannot supply Manager or HR approvals
        
        OSWfEngine wf;
        long piid;

        wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);
        piid = wf.initialize("Step Ownership", INITIAL_ACTION);
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

    
    @Ignore
    public void claimStepOwnership() throws Exception {



    }


    @Test
    public void createTaskList() throws Exception {

        OSWfEngine joe, bob, doris;
        WorkflowExpressionQuery query;
        Expression manager, hrDirector;
        long piid;

        manager = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "Line Managers"
        );
                                                   
        hrDirector = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "HR Director"
        );
                                                   

        // Have 'Joe' create three leave requests
        joe = new DefaultOSWfEngine("Joe Average");
        joe.setConfiguration(configuration);

        piid = joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);
        
        piid = joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);
        
        piid = joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        // Now create workflow for 'Bob the boss' so he can approve one of
        //  the leaves for 'Joe'
        
        bob = new DefaultOSWfEngine("Bob Bossman");
        bob.setConfiguration(configuration);
        
        // Bob approves one of the requests on his task list
        query = new WorkflowExpressionQuery(manager);
        List<Long> taskList = bob.query(query);
        assertTrue(taskList.size() > 0);
        
        piid = ((Long)taskList.get(0)).longValue();
        bob.doAction(piid, LINE_MANAGER_APPROVES);

 
        // There are 2 'Line Manager' steps available
        assertEquals(2, bob.query(query).size());
    
        // There are 3 'HR Director' steps available
        query = new WorkflowExpressionQuery(hrDirector);
        assertEquals(3, bob.query(query).size());
    
    }

    

}
