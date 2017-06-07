package tests;

import org.informagen.oswf.OSWfEngine;
import org.informagen.oswf.impl.DefaultOSWfEngine;
import org.informagen.oswf.Step;

import static java.util.Collections.EMPTY_MAP;
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

public class WF315Test {

    @Test
    public void startTest() throws Exception {

        OSWfEngine workflow = new DefaultOSWfEngine("WF315Test");

        String wfdescr = getClass().getResource("/core/WF-315.oswf.xml").toString();

        long piid = workflow.initialize(wfdescr, 0, EMPTY_MAP);

        workflow.doAction(piid, 20)
                .doAction(piid, 65)
                .doAction(piid, 75)
                .doAction(piid, 20)
                .doAction(piid, 65)
                .doAction(piid, 75)
        ;

        List currentSteps = workflow.getCurrentSteps(piid);
        long[] prevIds = ((Step)currentSteps.get(0)).getPreviousStepIds();

        assertEquals("Unexpected number of previous steps", 2, prevIds.length);

    }

}



