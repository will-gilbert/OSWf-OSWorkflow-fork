package org.informagen.oswf.registers;

import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.Register;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.OSWfEngine;


// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Invoke a register registred in JNDI.
 * Args must contain a AbstractWorkflow.JNDI_LOCATION key.
 * @author $Author: hani $
 * @version $Revision: 1.5 $
 */
public class JNDIRegister implements Register {
    // M E T H O D S  -------------------------------------------------------------------------

    public Object registerVariable(WorkflowContext context, ProcessInstance entry, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException {
        String location = (String) args.get(OSWfEngine.JNDI_LOCATION);

        if (location == null) {
            throw new WorkflowException(OSWfEngine.JNDI_LOCATION + " argument is null");
        }

        Register r;

        try {
            try {
                r = (Register) new InitialContext().lookup(location);
            } catch (NamingException e) {
                //ok, couldn't find it, look in env
                r = (Register) new InitialContext().lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            String message = "Could not look up JNDI register at: " + location;
            throw new WorkflowException(message, e);
        }

        return r.registerVariable(context, entry, args, persistentVars);
    }
}
