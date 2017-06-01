package usage;

import org.informagen.oswf.testing.OSWfHibernateTestCase;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;

// Action exceptions
import org.informagen.oswf.exceptions.InvalidActionException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.typedmap.TypedMap;

import org.informagen.oswf.Step;

import org.informagen.oswf.TypedMapStore;

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


public class ActionRestrictionsTest extends OSWfHibernateTestCase implements Constants {

     private static final String RDBMS_CONFIGURATION = "H2.hibernate.xml";
//    private static final String RDBMS_CONFIGURATION = "MySQL.hibernate.xml";


    OSWfConfiguration configuration;

    public ActionRestrictionsTest() {
        super("oswf-store.cfg.xml",
              "oswf-propertyset.cfg.xml",
              RDBMS_CONFIGURATION);
    }

    @BeforeClass
    public static void createAuthorization() {
    
        SecurityManager securityManager = SecurityManager.getInstance();
        assertNotNull("Could not get SecurityManager", securityManager);

        // Create a set of users; add them to roles;
        // In production this would be managed by an RDBMS
           
        // Create a set of users; add them to roles;
        // In production this would be managed by an RDBMS
                   
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


    @Before
    public void hibernateConfiguration() throws Exception {
                    
        configuration = new DefaultOSWfConfiguration()
            .load()
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
    public void confirmAuthorization() {

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
        assertTrue("canInitialize is false", wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Bob Bossman");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue("canInitialize is false", wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        
        wf = new DefaultOSWfEngine("Doris Despised");
        wf.setConfiguration(configuration);
        assertNotNull(wf);
        assertTrue("canInitialize is false", wf.canInitialize("Action Restrictions", INITIAL_ACTION));
        assertTrue(wf.canInitialize("Action Restrictions", INITIAL_ACTION));
    
    
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
        } catch(InvalidActionException invalidRoleException) {
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
    public void employeeApproval() throws Exception {
        
        // An Employee cannot supply manager or HR approvals
        
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


        manager = new FieldExpression(Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "Line Managers"
        );
                                                   

        hrDirector = new FieldExpression(Context.CURRENT_STEPS,
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
