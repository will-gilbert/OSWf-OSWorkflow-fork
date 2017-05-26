package tests.util;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.FunctionProvider;
import org.informagen.oswf.exceptions.WorkflowException;

import java.util.Map;

public class NullFunction implements FunctionProvider {

    public void execute(Map<String,Object> transientVars, Map<String,String> args, PropertySet ps) throws WorkflowException {
    }
}