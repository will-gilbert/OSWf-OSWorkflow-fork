package org.informagen.oswf;

import org.informagen.oswf.Step;

import java.lang.Iterable;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 */
public class JoinSteps implements Iterable<Step> {

    // I N S T A N C E   F I E L D S  ---------------------------------------------------------

    private final Map<Integer,Step> steps = new HashMap<Integer,Step>();

    // M E T H O D S  -------------------------------------------------------------------------

    public Iterator<Step> iterator() {
        return steps.values().iterator();
    }

    public int size() {
        return steps.size();
    }

    public void add(Step step) {

        if(steps.containsKey(step.getStepId()) == false)
            steps.put(step.getStepId(), step);
    }

    /**
    * No match, request Step is either not a Step transitioning into this join
    *   or has not yet been created and is not eligible for evaluation.
    *
    * Join logic should check for 'null'
    */

    public Step get(int stepId) {
        return steps.get(stepId);         
    }

}
