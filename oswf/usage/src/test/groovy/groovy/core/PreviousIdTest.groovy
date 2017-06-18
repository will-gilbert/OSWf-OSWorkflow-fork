package groovy.core;

import org.informagen.oswf.impl.DefaultOSWfEngine;

// JUnit 4.x testing
import org.junit.Test;


class PreviousIdTest {

    private static final int START_WORKFLOW =  1
    private static final int TO_SPLIT       = 10
    private static final int STEP_B_JOIN    = 20
    private static final int STEP_C_JOIN    = 30

    private static final int STEP_A          = 10
    private static final int STEP_B          = 20
    private static final int STEP_C          = 30
    private static final int STEP_D          = 40

    /**
    * A Step maintains an array list of immediately previous step record ids.
    * This array is empty just after an initial-action is executed and when
    *   the workflow has COMPLETED.
    *
    * It usually has one previous record id for the the step it came from.  It 
    *   can be greater than one after a join.
    *
    * Previous ids are not the same as history steps.
    */

    @Test
    void startTest() {

        def workflow = new DefaultOSWfEngine();
        def resource = getClass().getResource("/core/PreviousId.oswf.xml")
        def piid = workflow.initialize(resource as String, START_WORKFLOW);

        def currentSteps = workflow.getCurrentSteps(piid)

        // At the first step with no previous ids
        assert currentSteps.size() == 1
        assert currentSteps.any{it.stepId == STEP_A}
        assert currentSteps.find{it.stepId == STEP_A}.previousIds.length == 0

        workflow.doAction(piid, TO_SPLIT)

        // Here we have step both with one previous step
        assert 2 == currentSteps.size()
        assert currentSteps.any{it.stepId == STEP_B}
        assert currentSteps.find{it.stepId == STEP_B}.previousIds.length == 1

        assert currentSteps.any{it.stepId == STEP_C}
        assert currentSteps.find{it.stepId == STEP_C}.previousIds.length == 1

        workflow.doAction(piid, STEP_B_JOIN)
        assert 1 == currentSteps.size()
        assert currentSteps.any{it.stepId == STEP_C}
        assert currentSteps.find{it.stepId == STEP_C}.previousIds.length == 1

        // This step is after a join has 2 previous ids
        workflow.doAction(piid, STEP_C_JOIN)
        assert 1 == currentSteps.size()
        assert currentSteps.any{it.stepId == STEP_D}
        assert currentSteps.find{it.stepId == STEP_D}.previousIds.length == 2

    }

}



