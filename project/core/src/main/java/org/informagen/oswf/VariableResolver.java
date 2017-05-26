package org.informagen.oswf;

import org.informagen.oswf.propertyset.PropertySet;
import java.util.Map;

public interface VariableResolver {
    String translateVariables(String name, Map<String,Object> transientVars, PropertySet ps);
}
