package groovy.tests;


import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.ProcessInstanceState;
import org.informagen.oswf.exceptions.WorkflowException;
import org.informagen.oswf.exceptions.InvalidActionException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;


import org.informagen.oswf.exceptions.InvalidInputException;


import org.informagen.oswf.Step;

import org.informagen.oswf.PeristentVarsStore;


// OSWf - Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

// OSWf - Query
import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.Context;
import org.informagen.oswf.query.Field;
import org.informagen.oswf.query.Operator;
import org.informagen.oswf.query.LogicalOperator;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;


// OSWf delegate which installs a Custom PersistentVars mapping
import org.informagen.oswf.impl.HibernatePersistentVarsStore;

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

public class StepOwnershipTest extends support.OSWfHibernateTestCase implements usage.Constants {

    public static final String RDBMS_CONFIGURATION = "H2.hibernate.xml"; //System.getProperty("rdbms-configuration");

    OSWfConfiguration configuration;

    @BeforeClass
    public static void createAuthorization() {

        SecurityManager securityManager = SecurityManager.getInstance()
            
        assertNotNull("Could not get SecurityManager", securityManager)

        // Create a set of users; add them to groups; In production
        //  this would be managed by an RDBMS
                   
        // Groups
        Role employee = securityManager.createRole("Employee")
        Role manager = securityManager.createRole("Line Manager")
        Role hr = securityManager.createRole("HR Director")

        // Create 'Joe Average', an employee
        User joe = securityManager.createUser("Joe Average")
        joe.addToRole(employee)

        // Create 'Bob Bossman', a manager
        User bob = securityManager.createUser("Bob Bossman")
        bob.addToRole(employee)
        bob.addToRole(manager)
        
        // Create 'Doris Despised', the much loved Human Resources Director
        User doris = securityManager.createUser("Doris Despised")
        doris.addToRole(employee)
        doris.addToRole(hr)
            
    }



    public StepOwnershipTest() {
        super("oswf-store.cfg.xml", RDBMS_CONFIGURATION);
    }

    @Before
    public void setUp() throws Exception {
                    
        configuration = new DefaultOSWfConfiguration();
        configuration.load(getClass().getResource("/oswf-hibernate.xml"));
        configuration.getPersistenceArgs().put("sessionFactory", getSessionFactory());
        
        PeristentVarsStore delegate = new HibernatePersistentVarsStore(getSessionFactory());
        configuration.getPersistenceArgs().put("propertySetDelegate", delegate);
    }

    @After
    public void teardown() {
        closeSession();
        closeSessionFactory();
    }

    // Tests ==================================================================================
    
    @Test
    public void confirmSecurity() {

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
    public void employeesCanRequestLeave() {
        
        // Any company employee can request leave and successfully create 
        //   a process instance
        
        OSWfEngine joe, bob, doris
        long piid
        
        // Any company employee can create a "Step Ownership" process instance
        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration)
        piid = joe.initialize("Step Ownership", INITIAL_ACTION)
        assert piid
        joe.doAction(piid, REQUEST_LEAVE)
                
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration)
        piid = bob.initialize("Step Ownership", INITIAL_ACTION)
        assert piid
        bob.doAction(piid, REQUEST_LEAVE)
        
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration)
        piid = doris.initialize("Step Ownership", INITIAL_ACTION)
        assert piid
        doris.doAction(piid, REQUEST_LEAVE)
    }

    
    @Test
    public void managersCanApprove() throws Exception {
        
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
 
        assert ProcessInstanceState.COMPLETED == wf.getProcessInstanceState(piid)
        assert "approved" == wf.getPersistentVars(piid).getString("result")

    }


    @Test
    public void createWorkList() throws Exception {

        OSWfEngine joe, bob, doris, wfEngine

        WorkflowExpressionQuery query
        Expression managers, hrDirectors
                                                                                          
        def piids = []

        // Have 'Joe' create three leave requests
        joe = new DefaultOSWfEngine("Joe Average").setConfiguration(configuration);

        piids << joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);
        
        piids <<  joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);
        
        piids <<  joe.initialize("Step Ownership", INITIAL_ACTION);
        joe.doAction(piids.last(), REQUEST_LEAVE);

        // Now create workflow for 'Bob the boss' so he can approve one of
        //  the leaves for 'Joe'
        
        wfEngine = new DefaultOSWfEngine("").setConfiguration(configuration);
        
        // Get a list of process instance ids where there are steps which need
        //   the attention of any Line Manager. NB: Not necessarily 'Bob Bossman'

        managers = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "Line Manager"
        );

        query = new WorkflowExpressionQuery(managers);
        def workList = wfEngine.query(query);
        assert piids == workList

        // Bob, a Line Manager, connects to the WorkFlow and approves all requests
        bob = new DefaultOSWfEngine("Bob Bossman").setConfiguration(configuration)

        workList.each { piid ->
            bob.doAction(piid, LINE_MANAGER_APPROVES)
        }

        // No more tasks for 'Line Managers'
        query = new WorkflowExpressionQuery(managers);
        workList = wfEngine.query(query);
        assert workList == []
 
        // Do the same for 'HR Directors' -----------------------

        // Get a list of process instance ids where there are steps which need
        //   the attention of any Human Resources Manager. NB: Not necessarily 'Doris Despised'

        hrDirectors = new FieldExpression(
            Context.CURRENT_STEPS, 
            Field.OWNER, Operator.EQUALS, "HR Director"
        );

        query = new WorkflowExpressionQuery(hrDirectors);
        workList = wfEngine.query(query);
        assert piids == workList

        // Doris, a Humnan Resources Manager, connects to the WorkFlow and approves all requests
        doris = new DefaultOSWfEngine("Doris Despised").setConfiguration(configuration)

        workList.each { piid ->
            doris.doAction(piid, HUMAN_RESOURCES_APPROVES)
        }

        // No more tasks for 'Human Resources Managers'
        query = new WorkflowExpressionQuery(hrDirectors);
        workList = wfEngine.query(query);
        assert workList == []
    
    }

     

}
