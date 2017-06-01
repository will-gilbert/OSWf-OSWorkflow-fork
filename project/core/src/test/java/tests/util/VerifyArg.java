package tests.util;

import org.informagen.oswf.typedmap.TypedMap;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.exceptions.WorkflowException;

import junit.framework.TestCase;

import java.util.Map;

public class VerifyArg implements FunctionProvider {

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {
        Object expected = args.get("expected");
        Object actual = args.get("actual");
        
        TestCase.assertEquals(expected, actual);
    }
}
