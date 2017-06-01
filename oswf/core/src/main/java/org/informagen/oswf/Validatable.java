package org.informagen.oswf;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;


/**
 * Abstact base class for elements that can be validated.
 *
 * @author <a href="mailto:vorburger@users.sourceforge.net">Michael Vorburger</a>
 * @version $Revision: 1.2 $
 */
public interface Validatable {
    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Validate this element, and propagate validation to all contained sub-elements.
     * Should throw an InvalidWorkflowDescriptorException with details in message if the element
     * is invalid. Validity checks should be checks that cannot be encapsulated in the DTD.
     *
     * Validation has to be called explicitly on writting, a writeXML() does not validate implicitly;
     * it *IS* thus possible to write invalid descriptor files.  This could be useful for e.g.
     * a graphical workflow definition editor which would like to write incomplete definitions.
     * Validation *IS* performed on loading a workflow definition.
     *
     * @throws InvalidWorkflowDescriptorException
     */
    public void validate() throws InvalidWorkflowDescriptorException;
}
