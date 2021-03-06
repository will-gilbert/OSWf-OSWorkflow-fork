package org.informagen.oswf;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;

import org.informagen.oswf.PersistentVars;

import java.util.Map;


/**
 * Interface that must be implemented to define a java-based condition in your workflow definition.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 */
public interface Condition {
    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Determines if a condition should signal pass or fail.
     *
     * @param transientVars Variables that will not be persisted. These include inputs
     * given in the Workflow#initialize and Workflow#doAction method calls.
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
     * <p>
     * Also, any variable set as a {@link org.informagen.oswf.Register}), will also be
     * available in the transient map, no matter what. These transient variables only last through
     * the method call that they were invoked in, such as Worflow.initialize
     * and Worflow.doAction.
     * @param args The properties for this function invocation. Properties are created
     * from arg nested elements within the xml, an arg element takes in a name attribute
     * which is the properties key, and the CDATA text contents of the element map to
     * the property value. There is a magic property of '<code>stepId</code>';
     * if specified with a value of -1, then the value is replaced with the
     * current step's ID before the condition is called.
     * @param persistentVars The persistent variables that are associated with the current
     * instance of the workflow. Any change made to this will be seen on the next
     * function call in the workflow lifetime.
     */
    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, PersistentVars persistentVars) throws WorkflowException;
}
