package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;
 
// OSWf Exceptions
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.exceptions.*;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * A Restriction Descriptor contains a single ConditionsDescriptor which in turn
 *   contains an ordered list of ConditionDescriptors.
 *
 * A Restriction Descriptor is used by an ActionDescriptor and a StepConditionDescriptor.
 */
 
public class RestrictionDescriptor extends AbstractDescriptor implements Validatable {

    protected final ConditionsDescriptor conditionsDescriptor;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    public RestrictionDescriptor(Element restrictionElement) {

        List<Element> conditionsElements = XMLHelper.getChildElements(restrictionElement, "conditions");

        // If none or more than one 'conditions' return leaving conditionsDescriptor as null;
        //   The 'validate' method will throw an exception
        
        if(conditionsElements.size() == 1) {
            Element conditionsElement = conditionsElements.get(0);
            ConditionsDescriptor conditionsDescriptor = factory.createConditionsDescriptor(conditionsElement);
            conditionsDescriptor.setParent(this);
            this.conditionsDescriptor = conditionsDescriptor;
        } else
            this.conditionsDescriptor = null;
    }

    // M¨E T H O D S  --------------------------------------------------------------------------

    public ConditionsDescriptor getConditionsDescriptor() {
        return conditionsDescriptor;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        if (conditionsDescriptor == null)
            throw new InvalidWorkflowDescriptorException("A <restrict-to> element must have one and only one <conditions> child element");
    }


    
}
