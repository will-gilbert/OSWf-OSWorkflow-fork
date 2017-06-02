package org.informagen.oswf.conditions;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.Condition;

import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CanLookupJNDI implements Condition {

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {

        String location = (String) args.get(OSWfEngine.JNDI_LOCATION);
        location = location.trim();

        Condition condition = null;

        try {
            try {
                condition = (Condition) new InitialContext().lookup(location);
            } catch (NamingException e) {
                //ok, couldn't find it, look in env
                condition = (Condition) new InitialContext().lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            String message = "Could not lookup JNDI condition at: " + location;
            throw new WorkflowException(message, e);
        }

        return condition.passesCondition(transientVars, args, persistentVars);
    }
}
