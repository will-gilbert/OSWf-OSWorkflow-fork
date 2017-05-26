package org.informagen.oswf;

import org.informagen.oswf.Step;

import java.lang.Iterable;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 *
 */
public class JoinSteps implements Iterable<Step> {

    // I N S T A N C E   F I E L D S  ---------------------------------------------------------

    private final Collection<Step> steps = new HashSet<Step>();

    // M E T H O D S  -------------------------------------------------------------------------

    public Iterator<Step> iterator() {
        return steps.iterator();
    }

    public int size() {
        return steps.size();
    }

    public void add(Step step) {
        steps.add(step);
    }

    public Step get(int stepId) {
        
        for (Step step : steps) {
            if (step.getStepId() == stepId) 
                return step;
        }

        // No match, request Step is either not a Step transitioning into this join
        //   or has not yet been created and is not eligible for evaluation.
        // Join logic should check for 'null'
        
        return null;
    }

}
