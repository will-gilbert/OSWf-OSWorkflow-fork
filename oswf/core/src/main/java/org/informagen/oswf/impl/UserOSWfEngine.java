package org.informagen.oswf.impl;

// OSWf - Core
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.Step;

// OSWf - Descriptors
import org.informagen.oswf.descriptors.RegisterDescriptor;

// OSWf - Core Implementations
import org.informagen.oswf.impl.DefaultOSWfEngine;

// OSWf - Security
import org.informagen.oswf.security.User;

// OSWf - Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

// OSWF - Persistent Variables
import org.informagen.oswf.PersistentVars;

// Java - Collections
import java.util.Collection;
import java.util.Map;
import java.util.List;


/**
 * The 'actor' is defined as the user by string
 */


public class UserOSWfEngine extends DefaultOSWfEngine {
    
    final User user;
    
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public UserOSWfEngine(User user) {
        super(user.getName());
        this.user = user;
    }

    protected void populateTransientMap(ProcessInstance processInstance, Map<String,Object> transientVars, List<RegisterDescriptor> registers, Integer actionId, Collection<Step> currentSteps, PersistentVars persistentVars) throws WorkflowException {
        super.populateTransientMap(processInstance, transientVars, registers, actionId, currentSteps, persistentVars);
        transientVars.put("actor", user.getName());
        transientVars.put("user", user);
    }

}
