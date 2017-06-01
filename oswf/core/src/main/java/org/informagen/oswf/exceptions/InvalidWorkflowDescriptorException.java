package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowLoaderException;
import org.informagen.oswf.exceptions.WorkflowException;


/**
 * Indicates that a Workflow Descriptor was invalid.
 * Usually this indicates a semantically incorrect XML workflow definition.
 *
 * @author <a href="mailto:vorburger@users.sourceforge.net">Michael Vorburger</a>
 */
public class InvalidWorkflowDescriptorException extends WorkflowException {

    public InvalidWorkflowDescriptorException(String message) {
        super(message);
    }

    public InvalidWorkflowDescriptorException(String message, Exception cause) {
        super(message, cause);
    }
}
