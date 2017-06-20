package org.informagen.oswf.conditions;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;

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
public class GroovyScript implements Condition {

    private static final Logger logger = LoggerFactory.getLogger(BeanShell.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException {

        String script = args.get(OSWfEngine.BSH_SCRIPT);

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        ProcessInstance pi = (ProcessInstance) transientVars.get("pi");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        GroovyShell shell;

        try {
            Binding binding = new Binding();
            binding.setVariable("pi", pi);
            binding.setVariable("context", context);
            binding.setVariable("transientVars", transientVars);
            binding.setVariable("persistentVars", persistentVars);
            binding.setVariable("args", args);
            
            binding.setVariable("joinSteps", transientVars.get("joinSteps"));
            binding.setVariable("currentSteps", transientVars.get("currentSteps"));
            binding.setVariable("historySteps", transientVars.get("historySteps"));

            shell = new GroovyShell(loader, binding);

        } catch (Exception shellError) {
            String message = "Could not set values for Groovy script";
            logger.error(message, shellError);
            throw new WorkflowException(message, shellError);
        }


        try {

            if(logger.isDebugEnabled()){
                logger.debug("Trying to execute script:\n" + script + "\n");
            }

            Object object = shell.evaluate(script);

            if (object == null) {
                return false;
            } else {
                return Boolean.parseBoolean(object.toString());
            }

        } catch (Exception shellError) {
            String message = "Evaluation error while running Groovy function script";
            logger.error(message, shellError);
            throw new WorkflowException(message, shellError);
        } 
 
    }
}
