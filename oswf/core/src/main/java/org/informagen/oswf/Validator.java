package org.informagen.oswf;

// OSWf Exceptions
import org.informagen.oswf.exceptions.InvalidInputException;
import org.informagen.oswf.exceptions.WorkflowException;


import org.informagen.typedmap.TypedMap;

import java.util.Map;


/**
 * Interface that must be implemented to define a java-based validator in your workflow definition.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 */
public interface Validator {
    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Validates the user input.
     *
     * @param transientVars Variables that will not be persisted. These include inputs
     * given in the Worflow.initialize and Worflow.doAction method calls.
     * There are a number of special variable names:
     * <ul>
     * <li><code>entry</code>: (object type: {@link org.informagen.oswf.ProcessInstance})
     *  The workflow instance
     * <li><code>context</code>:
     * (object type: {@link org.informagen.oswf.WorkflowContext}). The workflow context.
     * <li><code>actionId</code>: The Integer ID of the current action that was take (if applicable).
     * <li><code>currentSteps</code>: A Collection of the current steps in the workflow instance.
     * <li><code>store</code>: The {@link org.informagen.oswf.WorkflowStore}.
     * <li><code>descriptor</code>: The {@link org.informagen.oswf.descriptors.WorkflowDescriptor}.
     * </ul>
     * Also, any variable set as a {@link org.informagen.oswf.Register}), will also be
     * available in the transient map. These transient variables only last through
     * the method call that they were invoked in, such as Worflow.initialize
     * and Worflow.doAction.
     * @param args The properties for this function invocation. Properties are created
     * from arg nested elements within the xml, an arg element takes in a name attribute
     * which is the properties key, and the CDATA text contents of the element map to
     * the property value.
     * @param ps The persistent variables that are associated with the current
     * instance of the workflow. Any change made to the propertyset are persisted to
     * the propertyset implementation's persistent store.
     * @throws InvalidInputException if the input is deemed to be invalid
     */
    public void validate(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) throws InvalidInputException, WorkflowException;
}
