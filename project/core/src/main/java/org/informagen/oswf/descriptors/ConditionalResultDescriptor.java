package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

// OSWf exceptions
import org.informagen.oswf.exceptions.*;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;


import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
 
public class ConditionalResultDescriptor extends ResultDescriptor {

    protected final List<ConditionsDescriptor> conditions = new ArrayList<ConditionsDescriptor>();

    // C O N S T R U C T O R  -----------------------------------------------------------------

    ConditionalResultDescriptor(Element conditionalResultElement) {
        super(conditionalResultElement);

        List<Element> conditionsElement = XMLHelper.getChildElements(conditionalResultElement, "conditions");

        for (Element conditionElement : conditionsElement) {
            ConditionsDescriptor conditionDescriptor = factory.createConditionsDescriptor(conditionElement);
            conditionDescriptor.setParent(this);
            this.conditions.add(conditionDescriptor);
        }
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public List<ConditionsDescriptor> getConditions() {
        return conditions;
    }

    public String getDestination() {
        
        WorkflowDescriptor desc = null;
        String sName = "";
        AbstractDescriptor actionDesc = getParent().getParent();

        if (actionDesc != null) {
            desc = (WorkflowDescriptor) actionDesc.getParent();
        }

        if (join != 0) {
            return "join #" + join;
        } else if (split != 0) {
            return "split #" + split;
        } else {
            if (desc != null) {
                sName = desc.getStep(step).getName();
            }

            return "step #" + step + " [" + sName + "]";
        }
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        super.validate();

        if (conditions.size() == 0) {
            throw new InvalidWorkflowDescriptorException("Conditional result from " + ((ActionDescriptor) getParent()).getName() + " to " + getDestination() + " must have at least one condition");
        }

        ValidationHelper.validate(conditions);
    }

}
