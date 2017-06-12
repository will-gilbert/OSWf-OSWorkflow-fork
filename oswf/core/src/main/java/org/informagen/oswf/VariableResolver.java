package org.informagen.oswf;

import org.informagen.oswf.PersistentVars;
import java.util.Map;

public interface VariableResolver {
    String translateVariables(String name, Map<String,Object> transientVars, PersistentVars persistentVars);
}
