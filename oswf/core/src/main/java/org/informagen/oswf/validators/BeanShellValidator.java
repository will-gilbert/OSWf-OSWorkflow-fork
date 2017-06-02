package org.informagen.oswf.validators;

import bsh.Interpreter;
import bsh.TargetError;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.Validator;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Beanshell inline script validator.
 * The input is determined to be invalid of the script throws a  {@link InvalidInputException}.
 */
public class BeanShellValidator implements Validator {
    
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(BeanShellValidator.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public void validate(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {
 
        Interpreter interpreter = new Interpreter();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            String contents = (String) args.get(OSWfEngine.BSH_SCRIPT);


            if (loader != null) 
                interpreter.setClassLoader(loader);

            interpreter.set("pi",  transientVars.get("pi"));
            interpreter.set("context", transientVars.get("context"));
            interpreter.set("transientVars", transientVars);
            interpreter.set("propertySet", persistentVars);

            Object object = interpreter.eval(contents);

            if (object != null) {
                throw new InvalidInputException(object);
            }
        } catch (TargetError e) {
            if (e.getTarget() instanceof WorkflowException) {
                throw (WorkflowException) e.getTarget();
            } else {
                throw new WorkflowException("Unexpected exception in beanshell validator script:" + e.getMessage(), e);
            }
        } catch (Exception e) {
            String message = "Error executing beanshell validator";
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) 
                interpreter.setClassLoader(null);
        }
    }
}
