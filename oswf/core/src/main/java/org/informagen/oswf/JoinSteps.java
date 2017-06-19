package org.informagen.oswf;

import org.informagen.oswf.Step;

import java.lang.Iterable;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 */
public class JoinSteps implements Iterable<Step> {

    // I N S T A N C E   F I E L D S  ---------------------------------------------------------

    private final Set<Step> steps = new HashSet<>();

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

    /**
    * No match, request Step is either not a Step transitioning into this join
    *   or has not yet been created and is not eligible for evaluation.
    *
    * Join logic should check for 'null'
    */

    public Step get(int stepId) {

        for( Step step : steps ) {
            if(step.getStepId() == stepId)
                return step;
        }

        return null;
    }

    public Set<Step> getAll(int stepId) {

        Set<Step> theSet = new HashSet();

        for( Step step : steps ) {
            if( step.getStepId() == stepId )
                theSet.add(step);
        }

        return theSet;
    }

}
