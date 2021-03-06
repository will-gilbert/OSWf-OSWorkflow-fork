package org.informagen.oswf;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.PersistentVars;

import java.util.Map;


/**
 * Interface to be implemented by any class that are to be called from within a workflow as a function,
 * either as a pre-function or a post-function. The args nested elements within the function xml call
 * will be mapped to the properties parameter.
 *
 */

public interface FunctionProvider {

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Execute this function
     * @param transientVars Variables that will not be persisted. These include inputs
     * given in the  Workflow#initializeand Workflow#doAction method calls.
     * There are a number of special variable names:
     * <ul>
     * <li><code>pi</code>: (object type: {@link org.informagen.oswf.ProcessInstance})
     *  The workflow instance
     * <li><code>context</code>:
     * (object type: {@link org.informagen.oswf.WorkflowContext}). The workflow context.
     * <li><code>actionId</code>: The Integer ID of the current action that was take (if applicable).
     * <li><code>currentSteps</code>: A Collection of the current steps in the workflow instance.
     * <li><code>store</code>: The {@link org.informagen.oswf.WorkflowStore}.
     * <li><code>descriptor</code>: The {@link org.informagen.oswf.descriptors.WorkflowDescriptor}.
     * </ul>
     * Also, any variable set as a {@link org.informagen.oswf.Register}), will also be
     * available in the transient map, no matter what. These transient variables only last through
     * the method call that they were invoked in, such as Workflow#initialize
     * and  Workflow#doAction.
     * @param args The properties for this function invocation. Properties are created
     * from arg nested elements within the xml, an arg element takes in a name attribute
     * which is the properties key, and the CDATA text contents of the element map to
     * the property value.
     * @param persistentVars The persistent variables that are associated with the current
     * instance of the workflow. Any change made to the propertyset are persisted to
     * the propertyset implementation's persistent store.
     */
    public void execute(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException;
}
