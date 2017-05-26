package org.informagen.oswf.registers;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.Register;
import org.informagen.oswf.WorkflowContext;
import org.informagen.oswf.ProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * This is a register, which helps logging using 'Simple Logging Facade for Java'.
 * It wraps a Logger instance which is linked to the "OSWorkflow/<workflow_name>/<id>" category
 *
 * Following optional arguments available:
 * <ul>
 *  <li>addInstanceId=true/false - if the instance id of the workflow should be added to the category. Defaults to false</li>
 *  <li>Category="OSWorkflow" - change the name of the log category other than "OSWorkflow"</li>
 * </ul>
 *
 *
 * If you register this class as "Logger", then you may use it from a Beanshell script like:
 * <pre>
 * logger = transientVars.get("logger");
 * logger.debug("hello logger!");
 * </pre>
 *
 * @author Zoltan Luspai
 */
public class SLF4jLogger implements Register {

    public Object registerVariable(WorkflowContext context, ProcessInstance pi, Map<String,String> args, PropertySet ps) {

        String loggerName = "oswf";

        if (args.get("loggerName") != null) 
            loggerName = args.get("loggerName");

        return LoggerFactory.getLogger(loggerName);
    }
}
