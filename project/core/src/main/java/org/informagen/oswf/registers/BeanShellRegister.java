package org.informagen.oswf.registers;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.Register;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;

import org.informagen.oswf.OSWfEngine;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


import java.util.Map;


/**
 * A register that executes a beanshell script when invoked.
 * @author Hani
 */
public class BeanShellRegister implements Register {

    public Object registerVariable(WorkflowContext context, ProcessInstance pi, Map<String,String> args, PropertySet ps) throws WorkflowException {

        String script = (String) args.get(OSWfEngine.BSH_SCRIPT);

        Interpreter i = new Interpreter();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            if (loader != null) {
                i.setClassLoader(loader);
            }

            i.set("pi", pi);
            i.set("context", context);
            i.set("propertySet", ps);

            return i.eval(script);
        } catch (TargetError targetError) {
            if (targetError.getTarget() instanceof WorkflowException) {
                throw (WorkflowException) targetError.getTarget();
            } else {
                String message = "Could not get object registered in to variable map";
                throw new WorkflowException(message, targetError.getTarget());
            }
        } catch (EvalError e) {
            String message = "Could not get object registered in to variable map";
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) {
                i.setClassLoader(null);
            }
        }
    }
}
