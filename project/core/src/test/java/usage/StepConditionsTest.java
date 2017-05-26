package usage;

import org.informagen.oswf.testing.OSWfTestCase;

// OSWf - Core
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf Configuration
import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.MemoryOSWfConfiguration;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * This test case is desinged to gain some understanding of OSWorkflow's
 *   'step-conditions' usage.
 *
 *  Uses:  src/test/resources/usage/step-conditions.oswf.xml
 *
 * @author Will Gilbert (gilbert@informagen.com)
 */
 
 
public class StepConditionsTest extends OSWfTestCase implements Constants {

    static OSWfConfiguration configuration;

    public StepConditionsTest() {
        // Variable 'logger' declased in 'OSWfTestCase'
        logger = LoggerFactory.getLogger(StepConditionsTest.class.getName());
    }

    @BeforeClass
    public static void setup() throws Exception {
        configuration = new MemoryOSWfConfiguration();
        configuration.load(StepConditionsTest.class.getResource("/oswf-usage.xml"));
    }

    // Tests ==================================================================================

    @Test
    public void getStepConditions() throws Exception {

        OSWfEngine wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);

        // Instance workflow 'StepConditions', move to Step 100 via INITIAL_ACTION
        long piid = wf.initialize("StepConditions", INITIAL_ACTION);

        // Log the current stepConditions on Step 100
        logStepConditions(wf, piid);
        
        assertTrue(hasStepCondition(wf, piid, "Joe Average is Underway"));
        assertFalse(hasStepCondition(wf, piid, "Joe Average is Finished"));
        assertTrue(hasStepCondition(wf, piid, "AlwaysTrue"));
        assertFalse(hasStepCondition(wf, piid, "AlwaysFalse"));
        
        // Non-existent stepConditions return false; easier than to have to handle null
        assertFalse(hasStepCondition(wf, piid, "Does not exist"));
 
    }


    /*
     *  This test shows that 'step-conditions' have no effort on
     *   resticting the workflow or access to actions
     */

     @Test
     public void stepConditionsDoNotRestrictActions() throws Exception {
         
         OSWfEngine wf = new DefaultOSWfEngine("Joe Average");
         wf.setConfiguration(configuration);

        // Instance workflow 'StepConditions', move to Step 100 via INITIAL_ACTION
         long piid = wf.initialize("StepConditions", INITIAL_ACTION);

         // Has access to all available actions...
         assertEquals(1, wf.getAvailableActions(piid).size());

         // ... can execute them
         wf.doAction(piid, REQUEST_LEAVE);
     }

     /*
     ** 'step-conditions' allow for dynamic control of step actions outside of
     **     the Workflow Description. Can be used as a substitute for action 
     **     restrictions within the Workflow Description.
     */

     @Test
     public void usingStepConditions() throws Exception {
         
        OSWfEngine wfEngine = new DefaultOSWfEngine("Joe Average");
        wfEngine.setConfiguration(configuration);

        // Instance workflow 'StepConditions', move to Step 100 via INITIAL_ACTION
        long piid = wfEngine.initialize("StepConditions", INITIAL_ACTION);

        // External control of actions via external-properties; allow 'Joe Average' access
        //  to the REQUEST_LEAVE action if the stepCondition 'Joe Average is Underway' returns
        //  true
         
        if( hasStepCondition(wfEngine, piid, "Joe Average is Underway") ) {
             wfEngine.doAction(piid, REQUEST_LEAVE);
        } else
            fail();
            
        wfEngine = new DefaultOSWfEngine("Bob Bossman");
        wfEngine.setConfiguration(configuration);

        // Instance workflow 'StepConditions', move to Step 100 via INITIAL_ACTION
        piid = wfEngine.initialize("StepConditions", INITIAL_ACTION);

        // Don't allow "Bob Bossman" access to the REQUEST_LEAVE action of Step 100
        if( hasStepCondition(wfEngine, piid, "Joe Average is Underway") ) {
            wfEngine.doAction(piid, REQUEST_LEAVE);
            fail();
        } else
            assertTrue(true);
  
     }


}
