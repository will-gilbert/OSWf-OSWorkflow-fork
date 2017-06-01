package org.informagen.oswf.functions;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

import org.informagen.oswf.typedmap.TypedMap;

import org.informagen.oswf.*;
import org.informagen.oswf.ProcessInstance;
import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *
 *
 * @author Hani
 */
public class BeanShellFunctionProvider implements FunctionProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(BeanShellFunctionProvider.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap ps) throws WorkflowException {

        String script = (String) args.get(OSWfEngine.BSH_SCRIPT);
        Interpreter i;
        ClassLoader loader;
        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");
        loader = Thread.currentThread().getContextClassLoader();

        try {
            i = new Interpreter();

            if (loader != null) {
                i.setClassLoader(loader);
            }

            i.set("pi", pi);
            i.set("context", context);
            i.set("transientVars", transientVars);
            i.set("typedMap", ps);
            i.set("args", args);
        } catch (EvalError evalError) {
            String message = "Could not set values for BSH script";
            logger.error(message, evalError);
            throw new WorkflowException(message, evalError);
        }

        try {
        	if(logger.isDebugEnabled()){
        		logger.debug("Trying to execute script:\n" + script + "\n");
        	}
            i.eval(script);
        } catch (TargetError targetError) {
            if (targetError.getTarget() instanceof WorkflowException) {
                throw (WorkflowException) targetError.getTarget();
            } else {
                String message = "Evaluation error while running BSH function script";
                throw new WorkflowException(message, targetError.getTarget());
            }
        } catch (EvalError evalError) {
            String message = "Evaluation error while running BSH function script";
            logger.error(message, evalError);
            throw new WorkflowException(message, evalError);
        } finally {
            if (loader != null) {
                i.setClassLoader(null);
            }
        }
    }
}
