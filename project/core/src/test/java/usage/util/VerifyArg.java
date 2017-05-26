package usage.util;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.exceptions.WorkflowException;

import junit.framework.TestCase;

import java.util.Map;


/**
 * @author hani Date: Apr 4, 2005 Time: 8:56:36 PM
 */
public class VerifyArg implements FunctionProvider {
    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, PropertySet ps) throws WorkflowException {
        Object expected = args.get("expected");
        Object actual = args.get("actual");
        TestCase.assertEquals(expected, actual);
    }
}
