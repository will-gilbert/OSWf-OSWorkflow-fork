package org.informagen.oswf.validators;

import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.Validator;
import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.4 $
 */
public class JNDIValidator implements Validator {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(JNDIValidator.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public void validate(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws InvalidInputException, WorkflowException {
        String location = (String) args.get(OSWfEngine.JNDI_LOCATION);

        if (location == null) {
            throw new WorkflowException(OSWfEngine.JNDI_LOCATION + " argument is null");
        }

        Validator validator;

        try {
            try {
                validator = (Validator) new InitialContext().lookup(location);
            } catch (NamingException e) {
                validator = (Validator) new InitialContext().lookup("java:comp/env/" + location);
            }
        } catch (NamingException e) {
            String message = "Could not look up JNDI Validator at: " + location;
            throw new WorkflowException(message, e);
        }

        validator.validate(transientVars, args, persistentVars);
    }
}
