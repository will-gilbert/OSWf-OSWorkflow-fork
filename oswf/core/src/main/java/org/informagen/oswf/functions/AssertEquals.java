package org.informagen.oswf.functions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.exceptions.WorkflowException;

import java.util.Map;


/**
 * @author will-giblert Date: Apr 6, 2012
 */
 
public class AssertEquals implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {
        String expected = args.get("expected");
        String actual = args.get("actual");
        
        if(expected.equals(actual) == false)
            throw new WorkflowException("AssertEquals: " + expected + " does not equal " + actual);
    }
}
