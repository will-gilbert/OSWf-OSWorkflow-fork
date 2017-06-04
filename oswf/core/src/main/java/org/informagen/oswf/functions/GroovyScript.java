package org.informagen.oswf.functions;


import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;

import org.informagen.typedmap.TypedMap;

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
 * @author will-gilbert
 */
public class GroovyScript implements FunctionProvider {


    private static final Logger logger = LoggerFactory.getLogger(GroovyScript.class);

    // M E T H O D S  -------------------------------------------------------------------------

    public void execute(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws WorkflowException {

        String script = (String) args.get(OSWfEngine.BSH_SCRIPT);


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

            shell.evaluate(script);

        } catch (Exception shellError) {
            String message = "Evaluation error while running Groovy function script";
            logger.error(message, shellError);
            throw new WorkflowException(message, shellError);
        } 
    }
}
