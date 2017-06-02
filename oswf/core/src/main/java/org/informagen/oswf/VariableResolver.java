package org.informagen.oswf;

import org.informagen.typedmap.TypedMap;
import java.util.Map;

public interface VariableResolver {
    String translateVariables(String name, Map<String,Object> transientVars, TypedMap persistentVars);
}
