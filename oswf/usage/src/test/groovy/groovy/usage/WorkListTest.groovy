package groovy.usage;


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
import org.informagen.oswf.impl.stores.MemoryWorkflowStore;

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
**      which allow a 'Manager' role member to 'claim' a request before moving into Step
**      id=250 and becoming the owner in Step id=250.
**
**  Claiming a request removes it from a pool of Manager roles pending requests.  No other
**      manager can Approve or Deny this request.
**
**  A request in Step id=250 has three actions. The familiar actions of 'Approve' or 'Deny'
**      and a new action 'Release' which a manager can perform to return the request to
**      to the manager group work list.
**
**  This example builds on 'LeaveRequestTest', 'ActionRestrictionsTest' and 'StepConditionsTest'
*       It may be helpful to reveiw these examples before deep diving into this workflow.
**
**  Uses:  src/test/resources/usage/WorkList.oswf.xml
**
*/

class WorkListTest implements usage.Constants {

    static OSWfConfiguration configuration;


    @BeforeClass
    static void defineConfiguration() throws Exception {
        configuration = new MemoryOSWfConfiguration()
            .load(WorkListTest.class.getResource('/oswf-usage.xml'));
    }

    /*
    ** Create a set of employees in the SecurityManager
    **  Create two 'managers' in order to test claiming and releasing a work item
    **
    */

    @BeforeClass
    static void createAuthorization() {

        SecurityManager securityManager = SecurityManager.getInstance();
            
        assertNotNull('Could not get SecurityManager', securityManager);

        // Create a set of users; add them to groups; In production
        //  this would be managed by an RDBMS
                   
        // Groups
        Role employees = securityManager.createRole('Employee');
        Role managers = securityManager.createRole('Line Manager');
        Role hr = securityManager.createRole('HR Director');

        // Create 'Joe', an employee
        User joe = securityManager.createUser('Joe Average');
        joe.addToRole(employees);

        // Create 'Bob', a line manager
        User bob = securityManager.createUser('Bob Bossman');
        bob.addToRole(employees);
        bob.addToRole(managers);

        // Create 'Charlie', another line manager
        User charlie = securityManager.createUser('Charlie Mann');
        charlie.addToRole(employees);
        charlie.addToRole(managers);
        
        // Create 'Doris', the much loved Human Resources Director
        User doris = securityManager.createUser('Doris Despised');
        doris.addToRole(employees);
        doris.addToRole(hr);
            
    }


    @After
    void teardown() {
        MemoryWorkflowStore.reset();
    }

    // Tests ==================================================================================
    
    @Test
    void confirmSecurity() {

        SecurityManager securityManager = SecurityManager.getInstance()

        assert securityManager.getUser('Joe Average')
        assert securityManager.getUser('Bob Bossman')
        assert securityManager.getUser('Charlie Mann')
        assert securityManager.getUser('Doris Despised')
        
        assert securityManager.getUser('Joe Average').hasRole('Employee')
        
        assert securityManager.getUser('Bob Bossman').hasRole('Employee')
        assert securityManager.getUser('Bob Bossman').hasRole('Line Manager')
        
        assert securityManager.getUser('Charlie Mann').hasRole('Employee')
        assert securityManager.getUser('Charlie Mann').hasRole('Line Manager')
        
        assert securityManager.getUser('Doris Despised').hasRole('Employee')
        assert securityManager.getUser('Doris Despised').hasRole('HR Director')
    
    }

    @Test
    void canCreateWorkflowEngine() {

        OSWfEngine joe, bob, charlie, doris
        
        joe = new DefaultOSWfEngine('Joe Average').setConfiguration(configuration)
        assert joe
        assert joe.canInitialize('Work List', INITIAL_ACTION)
        
        bob = new DefaultOSWfEngine('Bob Bossman').setConfiguration(configuration)
        assert bob
        assert bob.canInitialize('Work List', INITIAL_ACTION)
        
        charlie = new DefaultOSWfEngine('Charlie Mann').setConfiguration(configuration)
        assert charlie
        assert charlie.canInitialize('Work List', INITIAL_ACTION)
        
        doris = new DefaultOSWfEngine('Doris Despised').setConfiguration(configuration)
        assert doris
        assert doris.canInitialize('Work List', INITIAL_ACTION)
    }


    @Test
    void canCreateRequest() {

        def joe = new DefaultOSWfEngine('Joe Average').setConfiguration(configuration)
        def piid = joe.initialize('Work List', INITIAL_ACTION)
        joe.doAction(piid, REQUEST_LEAVE)

        assert 'pending' == joe.getPersistentVars(piid).getString('result')
        assert ProcessInstanceState.ACTIVE ==  joe.getProcessInstanceState(piid)
    }


    @Test
    void managerClaimWorkItem()  {

        OSWfEngine joe, bob, charlie

        // Create engines for Joe, Bob and Charlie
        joe = createOSWfEngine('Joe Average');
        bob = createOSWfEngine('Bob Bossman');
        charlie = createOSWfEngine('Charlie Mann');

        long piid = joe.initialize('Work List', INITIAL_ACTION);
        joe.doAction(piid, REQUEST_LEAVE);

        // Either Bob or Charlie can claim the work request
        assert  bob.getAvailableActions(piid).contains(CLAIM_WORK_ITEM)
        assert  charlie.getAvailableActions(piid).contains(CLAIM_WORK_ITEM)

        // Bob claims the request
        bob.doAction(piid, CLAIM_WORK_ITEM);

        // Bob can 'Approve', 'Deny' or 'Release' the request
        def actions =  bob.getAvailableActions(piid)
        assert 3 == bob.getAvailableActions(piid).size()
        assert actions.contains(LINE_MANAGER_APPROVES)
        assert actions.contains(LINE_MANAGER_DENIES)
        assert actions.contains(RELEASE_WORK_ITEM)

        // Charlie has now has no actions
        assert 0 == charlie.getAvailableActions(piid).size()

        // Bob releases the request
        bob.doAction(piid, RELEASE_WORK_ITEM);

        // Either Bob or Charlie can now claim the request as before
        assert  bob.getAvailableActions(piid).contains(CLAIM_WORK_ITEM)
        assert  charlie.getAvailableActions(piid).contains(CLAIM_WORK_ITEM)

        // Charlie claims the request this time
        charlie.doAction(piid, CLAIM_WORK_ITEM);

        // Charlie can 'Approve', 'Deny' or 'Release' the request
        actions =  charlie.getAvailableActions(piid)
        assert 3 == charlie.getAvailableActions(piid).size()
        assert actions.contains(LINE_MANAGER_APPROVES)
        assert actions.contains(LINE_MANAGER_DENIES)
        assert actions.contains(RELEASE_WORK_ITEM)

        // Bob has now has no actions
        assert 0 == bob.getAvailableActions(piid).size()

        // Charlie as a Manager approves the reqeust
        charlie.doAction(piid, LINE_MANAGER_DENIES);
        assert 0 == charlie.getAvailableActions(piid).size()

        // Process Instance is pending 'HR Director' action
        assert ProcessInstanceState.ACTIVE ==  joe.getProcessInstanceState(piid)
        assert 'pending' == joe.getPersistentVars(piid).getString('result')
    }

    @Ignore
    void claimAndReclaim() {

        def joe, bob, doris, wfEngine

        joe = createOSWfEngine('Joe Average')
        bob = createOSWfEngine('Bob Bossman')
        doris = createOSWfEngine('Doris Despised')
        wfEngine = createOSWfEngine()

        def piid = joe.initialize('Work List', INITIAL_ACTION)
        joe.doAction(piid, REQUEST_LEAVE)

        bob.doAction(piid, CLAIM_WORK_ITEM);
        bob.doAction(piid, RELEASE_WORK_ITEM);

        bob.doAction(piid, CLAIM_WORK_ITEM);
        bob.doAction(piid, RELEASE_WORK_ITEM);

        bob.doAction(piid, CLAIM_WORK_ITEM);
        bob.doAction(piid, RELEASE_WORK_ITEM);

        bob.doAction(piid, CLAIM_WORK_ITEM)
        bob.doAction(piid, LINE_MANAGER_APPROVES)

        doris.doAction(piid, HUMAN_RESOURCES_APPROVES)

        assert ProcessInstanceState.COMPLETED ==  wfEngine.getProcessInstanceState(piid)
        assert 'approved' == wfEngine.getPersistentVars(piid).getString('result')
    }




    @Ignore
    void workingWithWorkLists() {

        OSWfEngine joe, bob, charlie, doris, wfEngine
        List<Long> workList, piids =[]

        // Create engines for Joe, Bob and Charlie
        joe = createOSWfEngine('Joe Average')
        bob = createOSWfEngine('Bob Bossman')
        charlie = createOSWfEngine('Charlie Mann')
        doris = createOSWfEngine('Doris Despised')

        wfEngine = new DefaultOSWfEngine()

        // Have Joe creates 3 requests, that is, three process instances
        piids << joe.initialize('Work List', INITIAL_ACTION)
        joe.doAction(piids.last(), REQUEST_LEAVE)
        piids << joe.initialize('Work List', INITIAL_ACTION)
        joe.doAction(piids.last(), REQUEST_LEAVE)
        piids << joe.initialize('Work List', INITIAL_ACTION)
        joe.doAction(piids.last(), REQUEST_LEAVE)

        // The LINE_MANAGER_CLAIM_STEP and the HUMAN_RESOURCES_DESCISION_STEP can only be
        //   claimd by a users role not as individuals
        // Returns the 3 process instance ids
        assert piids == wfEngine.query(createWorkListQuery('Line Manager', LINE_MANAGER_CLAIM_STEP))
        assert piids == wfEngine.query(createWorkListQuery('HR Director', HUMAN_RESOURCES_DESCISION_STEP))

        // Ensure here that individual users cannot claim the steps
        assert [] == wfEngine.query(createWorkListQuery('Joe Average',   LINE_MANAGER_CLAIM_STEP))
        assert [] == wfEngine.query(createWorkListQuery('Bob Bossman',   LINE_MANAGER_CLAIM_STEP))
        assert [] == wfEngine.query(createWorkListQuery('Charlie Mann',  LINE_MANAGER_CLAIM_STEP))
        assert [] == wfEngine.query(createWorkListQuery('Doris Despised', HUMAN_RESOURCES_DESCISION_STEP))

        // Now have Bob 'claim' one of the request
        workList = wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP));
        assert piids == workList

        def bobsWork = workList[0]
        bob.doAction(bobsWork, CLAIM_WORK_ITEM);

        // The 'Line Manager's' work list is reduced the piid which Bob has picked up a work item
        // Charlie still has no work items
        // Doris still has no work items

        assert (piids - bobsWork) == wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP))
        assert [bobsWork] == wfEngine.query(createWorkListQuery("Bob Bossman",  LINE_MANAGER_DESCISION_STEP))
        assert [] == wfEngine.query(createWorkListQuery("Charlie Mann", LINE_MANAGER_DESCISION_STEP))
        assert [] == wfEngine.query(createWorkListQuery("Doris Despised", HUMAN_RESOURCES_DESCISION_STEP))

        // Now have Charlie 'claim' one of the 2 remaining requests
        workList = wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP));
        assert 2 == workList.size()
        def charliesWork = workList[0]
        charlie.doAction(charliesWork, CLAIM_WORK_ITEM);

        // The 'Line Manager's' work list is reduced, leaving one piid
        // Bob and Charlie each have a piid to make a desision for
        // Meanwhile, no actions taken by any HR Director

        def piid = piids - bobsWork - charliesWork
        assert piid == wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP))
        assert [bobsWork] == wfEngine.query(createWorkListQuery("Bob Bossman",  LINE_MANAGER_DESCISION_STEP))
        assert [charliesWork] == wfEngine.query(createWorkListQuery("Charlie Mann", LINE_MANAGER_DESCISION_STEP))
        assert piids == wfEngine.query(createWorkListQuery('HR Director', HUMAN_RESOURCES_DESCISION_STEP))
        assert [] == wfEngine.query(createWorkListQuery("Doris Despised", HUMAN_RESOURCES_DESCISION_STEP))

        // Let's have Bob decide not to work on his request; returning it to the
        //  'Line Manager' workList
        bob.doAction(bobsWork, RELEASE_WORK_ITEM);

        // Bob's work has been returned to the LINE_MANAGER_CLAIM_STEP and he has no work
        //   at the LINE_MANAGER_DESCISION_STEP
        //
        // Charlie work is pending his actions and still, no actions taken by any HR Director

        assert piids - charliesWork == wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP))
        assert [] == wfEngine.query(createWorkListQuery("Bob Bossman",  LINE_MANAGER_DESCISION_STEP))
        assert [charliesWork] == wfEngine.query(createWorkListQuery("Charlie Mann", LINE_MANAGER_DESCISION_STEP))
        assert [] == wfEngine.query(createWorkListQuery("Doris Despised", HUMAN_RESOURCES_DESCISION_STEP))

        //---------------------------------------------------------------------
        // Finish up -- Everybody approves; All process instances complete and
        //              are approved.

        charlie.doAction(charliesWork, LINE_MANAGER_APPROVES)

        wfEngine.query(createWorkListQuery("Line Manager", LINE_MANAGER_CLAIM_STEP)).each {
            bob.doAction(it, CLAIM_WORK_ITEM)
            bob.doAction(it, LINE_MANAGER_APPROVES)
        }

        wfEngine.query(createWorkListQuery('HR Director', HUMAN_RESOURCES_DESCISION_STEP)).each {
            doris.doAction(it, HUMAN_RESOURCES_APPROVES)
        }

        //*** Had a problem here, the released piid, when reclaimed did not have the
        //    correct exit status //

        piids.each {
            assert ProcessInstanceState.COMPLETED ==  wfEngine.getProcessInstanceState(it)
            assert 'approved' == wfEngine.getPersistentVars(it).getString('result')
        }

    }

    private OSWfEngine createOSWfEngine(String actor) {
        return new DefaultOSWfEngine(actor).setConfiguration(configuration);
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
                [ownerExpression, stepExpression] as Expression[],
                LogicalOperator.AND)
            );

        return query;       
    }
    

}
