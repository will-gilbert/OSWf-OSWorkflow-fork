package org.informagen.oswf;

import org.informagen.oswf.typedmap.TypedMap;
import java.util.Map;

public interface VariableResolver {
    String translateVariables(String name, Map<String,Object> transientVars, TypedMap typedMap);
}
