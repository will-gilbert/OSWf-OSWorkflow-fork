package usage;

import org.informagen.oswf.testing.OSWfHibernateTestCase;

import org.informagen.oswf.exceptions.QueryNotSupportedException;
import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.impl.DefaultOSWfEngine;

import org.informagen.oswf.OSWfConfiguration;
import org.informagen.oswf.impl.DefaultOSWfConfiguration;

import org.informagen.oswf.descriptors.WorkflowDescriptor;

import org.informagen.oswf.query.Expression;
import org.informagen.oswf.query.FieldExpression;
import org.informagen.oswf.query.NestedExpression;
import org.informagen.oswf.query.WorkflowExpressionQuery;

import org.informagen.oswf.Step;

import org.informagen.oswf.descriptors.WorkflowDescriptor;
import org.informagen.oswf.descriptors.StepDescriptor;
import org.informagen.oswf.descriptors.StepConditionDescriptor;

import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.TypedMapStore;

// OSWf delegate which installs a Custom TypedMap mapping
import org.informagen.oswf.impl.HibernateTypedMapStore;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

// Logging
import org.slf4j.Logger;
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
 *
 * @author Will Gilbert 
 */
 
 
public class StepConditionsTest extends OSWfHibernateTestCase implements Constants {

     private static final String RDBMS_CONFIGURATION = "H2.hibernate.xml";
//    private static final String RDBMS_CONFIGURATION = "MySQL.hibernate.xml";

    OSWfConfiguration configuration;

    public StepConditionsTest() {
        super("oswf-store.cfg.xml",
              "oswf-propertyset.cfg.xml",
              RDBMS_CONFIGURATION);
              
        logger = LoggerFactory.getLogger(StepConditionsTest.class.getName());

    }


    @Before
    public void setUp() throws Exception {
                    
        configuration = new DefaultOSWfConfiguration();
        configuration.load(getClass().getResource("/oswf.xml"));
        configuration.getPersistenceArgs().put("sessionFactory", getSessionFactory());
        
        TypedMapStore delegate = new HibernateTypedMapStore(getSessionFactory());
        configuration.getPersistenceArgs().put("propertySetDelegate", delegate);
    }

    @After
    public void teardown() {
        closeSession();
        closeSessionFactory();
    }

    // Tests ==================================================================================

    @Test
    public void getSecurityPermissions() throws Exception {

        OSWfEngine wf = new DefaultOSWfEngine("Joe Average");
        wf.setConfiguration(configuration);

        // Move to Step 100
        long piid = wf.initialize("StepConditions", INITIAL_ACTION);
         
        logStepConditions(wf, piid);
        
        assertNotNull(hasStepCondition(wf, piid, "Joe Average is Underway"));
        assertTrue(hasStepCondition(wf, piid, "Joe Average is Underway"));

        assertNotNull(hasStepCondition(wf, piid, "Joe Average is Finished"));
        assertFalse(hasStepCondition(wf, piid, "Joe Average is Finished"));
 
        assertNotNull(hasStepCondition(wf, piid, "AlwaysTrue"));
        assertTrue(hasStepCondition(wf, piid, "AlwaysTrue"));

        assertNotNull(hasStepCondition(wf, piid, "AlwaysFalse"));
        assertFalse(hasStepCondition(wf, piid, "AlwaysFalse"));

        assertFalse(hasStepCondition(wf, piid, "Dos not exist"));
 
    }


    /*
     *  This test shows that 'step-conditions' have no effort on
     *   resticting the workflow
     */

     @Test
     public void stepConditionsDontRestrict() throws Exception {
         
         OSWfEngine wf = new DefaultOSWfEngine("Joe Average");
         wf.setConfiguration(configuration);

         // Move to Step 100
         long piid = wf.initialize("StepConditions", INITIAL_ACTION);

         wf.doAction(piid, REQUEST_LEAVE);
         wf.doAction(piid, LINE_MANAGER_APPROVES);
         wf.doAction(piid, HUMAN_RESOURCES_APPROVES);

         // NOTIFY_EMPLOYEE is auto='true' action 
         // wf.doAction(piid, NOTIFY_EMPLOYEE);
     }

     @Test
     public void usingStepConditions() throws Exception {
         
         OSWfEngine wfEngine = new DefaultOSWfEngine("Joe Average");
         wfEngine.setConfiguration(configuration);

         // Move to Step 100
         long piid = wfEngine.initialize("StepConditions", INITIAL_ACTION);

        // External control of actions via external-properties; allow 'Joe Average'
         
         if( hasStepCondition(wfEngine, piid, "Joe Average is Underway") ) {
             wfEngine.doAction(piid, REQUEST_LEAVE);
         } else
            fail();
            
        wfEngine = new DefaultOSWfEngine("Bob Bossman");
        wfEngine.setConfiguration(configuration);

        // Move to Step 100
        piid = wfEngine.initialize("StepConditions", INITIAL_ACTION);

        // Don't allow "Bob" access to the actions of Step 100
        if( hasStepCondition(wfEngine, piid, "Joe Average is Underway") ) {
            wfEngine.doAction(piid, REQUEST_LEAVE);
            fail();
        } else
            assertTrue(true);
  
     }


}
