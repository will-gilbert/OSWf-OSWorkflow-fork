package org.informagen.oswf.conditions;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

import org.informagen.oswf.PersistentVars;

import org.informagen.oswf.Condition;
import org.informagen.oswf.Register;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *
 * 
 */
public class BeanShell implements Condition {

    private static final Logger logger = LoggerFactory.getLogger(BeanShell.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException {

        String script = args.get(OSWfEngine.BSH_SCRIPT);

        Interpreter interpreter = new Interpreter();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            if (loader != null) 
                interpreter.setClassLoader(loader);

            interpreter.set("pi", transientVars.get("pi"));
            interpreter.set("context", transientVars.get("context"));
            interpreter.set("transientVars", transientVars);
            interpreter.set("persistentVars", persistentVars);
            interpreter.set("jn", transientVars.get("jn"));
            interpreter.set("joinSteps", transientVars.get("joinSteps"));

            Object object = interpreter.eval(script);

            if (object == null) {
                return false;
            } else {
                return Boolean.parseBoolean(object.toString());
            }
        } catch (TargetError targetError) {
            if (targetError.getTarget() instanceof WorkflowException) {
                throw (WorkflowException) targetError.getTarget();
            } else {
                String message = "Could not execute BeanShell script";
                throw new WorkflowException(message, targetError.getTarget());
            }
        } catch (EvalError e) {
            String message = "Could not execute BeanShell script";
            logger.error(message, e);
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) {
                interpreter.setClassLoader(null);
            }
        }
    }
}
