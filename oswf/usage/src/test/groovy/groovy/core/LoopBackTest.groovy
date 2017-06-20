package groovy.core

import org.informagen.oswf.OSWfEngine
import org.informagen.oswf.OSWfLogging
import org.informagen.oswf.impl.DefaultOSWfEngine
import org.informagen.oswf.ProcessInstanceState

import org.informagen.oswf.testing.OSWfAssertions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// JUnit 4.x testing
import org.junit.Before
import org.junit.Ignore
import org.junit.Test



/**
 * @author will gilbert
 *
 * This test shows the behavior of a process instance which can branch back
 *   to an earlier step in the workflow.  The interesting twist is that it
 *   has looped back after a split and re-enters that split. This has the effect
 *   of creating additional current steps for the other step pending in the
 *   the split.
 *
 * The workflow behaves as it should, neither 'A' or 'B' can be pending, also
 *   step 'Start' cannot be current as this could create more 'A' and 'B' steps.
 *   Only when all 'A'  and 'B' steps have entered the join and no futher work
 *   is coming along the workflow can the join proceed.
 *
 * There will only be one 'End' step create after all A and B work is finished.
 * 
 * What is needed is a 'real world' use case which can be used to better illustrate 
 *   this rarely used behavior.
 */


class LoopBackTest {

    private static final Logger logger = LoggerFactory.getLogger(LoopBackTest.class)

    // ACTIONS
    private static final INITIAL_ACTION = 1
    private static final ENTER_SPLIT    = 2
    private static final A_REPEATS      = 3
    private static final A_FINISHES     = 4
    private static final B_REPEATS      = 5
    private static final B_FINISHES     = 6
    private static final END_ACTION     = 7

    // STEPS
    private static final STEP_ENTER     = 1
    private static final STEP_A         = 2
    private static final STEP_B         = 3
    private static final STEP_END       = 4


    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private def wfEngine
    private def piid

    @Before
    void setup() {
        wfEngine = new DefaultOSWfEngine();
        def resource = getClass().getResource("/core/LoopBack.oswf.xml")
        piid = wfEngine.initialize(resource.toString(), INITIAL_ACTION);
   }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    void noLoopback_Finish_AB() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 4 + 1
        assert done()
    }
    
    @Test
    void noLoopback_Finish_BA() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 4 + 1
        assert done()
    }

    
    @Test
    void loopback_Via_A_then_B_Finishes_before_the_split() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [1,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1
        assert done()
    }

    @Test
    void loopback_Via_A_Finish_ABB() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,2,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,2,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1
        assert done()
    }
    
    @Test
    void loopback_Via_B_Finish_BAA() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [1,1,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,2,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1
        assert done()
    }
 
    @Test
    void Loopback_Via_A_Finish_BAB() {
 
        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,2,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,1,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1 
        assert done()
    }
 
    @Test
    void Loopback_Via_B_Finish_ABA() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [1,1,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,1,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,1,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1 
        assert done()
    }
 
    @Test
    void loopback_Both_Finish_ABAB() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,2,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,1,2,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,1,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 10 + 1
        assert done()
    }
   
    @Test
    void finish_B_loopback_A_Finish_BA() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_REPEATS   ; assert currentSteps() == [1,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,0,1]
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 7 + 1
        assert done()
    }

    @Test
    void threeLoopBacks_BA() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        
        // First Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,2,0]

        // Second Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,1,2,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,3,3,0]
        
        // Third Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,2,3,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,3,3,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,4,4,0]

        // Finish all B        
        doAction B_FINISHES  ; assert currentSteps() == [0,4,3,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,4,2,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,4,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,4,0,0]

        // Finish all A, each creates an END step        
        doAction A_FINISHES  ; assert currentSteps() == [0,3,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,2,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,1,0,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,0,1]

        // Complete the END step
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 22 + 1
        assert done()
    }

    @Test
    void threeLoopBacks_AB() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        
        // First Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,2,0]

        // Second Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,1,2,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,3,3,0]
        
        // Third Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,2,3,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,3,3,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,4,4,0]

        // Finish all A        
        doAction A_FINISHES  ; assert currentSteps() == [0,3,4,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,2,4,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,1,4,0]
        doAction A_FINISHES  ; assert currentSteps() == [0,0,4,0]

        // Finish all B, each creates an END step        
        doAction B_FINISHES  ; assert currentSteps() == [0,0,3,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,2,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,1,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1]

        // Complete the END step
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 22 + 1
        assert done()
    }


    @Test
    void threeLoopBacks_Alternate() {

        doAction ENTER_SPLIT ; assert currentSteps() == [0,1,1,0]
        
        // First Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,0,1,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,0,0,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,2,2,0]

        // Second Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,1,2,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,1,1,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,3,3,0]
        
        // Third Loopback
        doAction A_REPEATS   ; assert currentSteps() == [1,2,3,0]
        doAction B_REPEATS   ; assert currentSteps() == [2,2,2,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [1,3,3,0]
        doAction ENTER_SPLIT ; assert currentSteps() == [0,4,4,0]

        // Finish all A steps  
        doAction A_FINISHES  ; assert currentSteps() == [0,3,4,0]
        doAction B_FINISHES  ; assert currentSteps() == [0,3,3,0] 
        doAction A_FINISHES  ; assert currentSteps() == [0,2,3,0] 
        doAction B_FINISHES  ; assert currentSteps() == [0,2,2,0] 

        // Finish all B steps        
        doAction A_FINISHES  ; assert currentSteps() == [0,1,2,0] 
        doAction B_FINISHES  ; assert currentSteps() == [0,1,1,0] 
        doAction A_FINISHES  ; assert currentSteps() == [0,0,1,0] 
        doAction B_FINISHES  ; assert currentSteps() == [0,0,0,1] 

        // Complete the END step
        doAction END_ACTION  ; assert currentSteps() == [0,0,0,0]

        assert historySteps() == 22 + 1
        assert done()
    }

    //==============================================================


    private def doAction(action) {
         wfEngine.doAction(piid, action)
    }

    /**
    **  Get the size of current steps in the steps 'Start', 'A', 'B', 'End'
    **  
    ** returns a list of count of current steps for each step as an array
    */

    private def currentSteps() {

        def currentSteps = wfEngine.getCurrentSteps(piid)

        [
            currentSteps.findAll{it.stepId == STEP_ENTER}.size(), 
            currentSteps.findAll{it.stepId == STEP_A}.size(), 
            currentSteps.findAll{it.stepId == STEP_B}.size(),
            currentSteps.findAll{it.stepId == STEP_END}.size()
        ]
    }

    /**
    **  The number of history steps is the number of invocations of 'doAction'
    **    plus the number of time the END_ACTION was invoke because the final
    **    step has no action and finished automatically.
    */

    private def historySteps() {
        wfEngine.getHistorySteps(piid).size()    
    }

    private def done() {
         ProcessInstanceState.COMPLETED == wfEngine.getProcessInstanceState(piid)
    }

}
