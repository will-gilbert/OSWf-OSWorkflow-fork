package org.informagen.oswf.conditions;

import org.informagen.typedmap.TypedMap;

// OSWf Security
import org.informagen.oswf.SecurityManager;
import org.informagen.oswf.security.User;
import org.informagen.oswf.security.Role;

import org.informagen.oswf.Condition;
import org.informagen.oswf.WorkflowContext;

// Java - Collections
import java.util.Map;


/*
** Condition class which can be used to determine if the actor is in
**    a security role.
**
**   'role' is a required argument passed in via the 'args' Map
*/

public class IsStringPropertyEqual implements Condition {

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) {

        String name = args.get("name");
        String value = args.get("value");

        // Test for required inputs; Don't assert or throw exceptions, return false
        if(name == null || value == null)
            return false;
            
        String currentValue = ps.getString(name);
        
        if(currentValue == null)
            return false;

        return currentValue.equals(value);
    }
}
