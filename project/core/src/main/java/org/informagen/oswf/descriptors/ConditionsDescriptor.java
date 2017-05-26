package org.informagen.oswf.descriptors;

import org.informagen.oswf.Validatable;

// OSWf exceptions
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * A ConditionsDescriptor contains an ordered list of consisting of both 
 *    ConditionDescriptors and recursively nested ConditionsDescriptors.
 *
 * The ConditionsDescriptor also has a boolean operator (type) which takes on the values of
 *    AND or OR, defaulted to AND. This operator is used with the boolean results of its
 *    child elements to return a boolean evalution.
 *
 *  A ConditionsDesriptor is a child of Actions and StepConditions via their 
 *    RestrictionDescriptor and a child of ResultDescriptor and JoinDescriptor.
 */

public class ConditionsDescriptor extends AbstractDescriptor implements Validatable {

    // This ordered list may contain both 'ConditionDescriptor' and 'ConditionsDescriptor' so
    //   so we were forced to use their most commom super class in the generic.
    
    private final List<AbstractDescriptor> conditions = new ArrayList<AbstractDescriptor>();
    private final String type;

    // C O N S T R U C T O R  -----------------------------------------------------------------
     
    ConditionsDescriptor(Element conditionsElement) {
        
        // 'AND' or 'OR currently supported; AND' is the default type; 'OR' must explcitly specified

        String typeAttribute = conditionsElement.getAttribute("type");
        this.type = (typeAttribute.trim().length() > 0) ? typeAttribute : "AND";

        // We need to handle this differently becuase the children of a 'conditions' node
        //  can be either a 'condition' or another 'conditions' node
        
        NodeList children = conditionsElement.getChildNodes();
        
        for (int i=0; i< children.getLength(); i++) {
            
            Node child = children.item(i);
        
            if (child instanceof Element) {
                if ("condition".equals(child.getNodeName())) {
                    ConditionDescriptor conditionDescriptor = factory.createConditionDescriptor((Element) child);
                    conditions.add(conditionDescriptor);
                } else if ("conditions".equals(child.getNodeName())) {
                    ConditionsDescriptor conditionsDescriptor = factory.createConditionsDescriptor((Element) child);
                    conditions.add(conditionsDescriptor);
                }
            }
        }
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public List<AbstractDescriptor> getConditions() {
        return conditions;
    }

    public String getType() {
        return type;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        
        // Validate members of a Collection
        ValidationHelper.validate(conditions);

        if (conditions.size() == 0) {
            AbstractDescriptor desc = getParent();

            if ((desc != null) && (desc instanceof ConditionalResultDescriptor)) {
                throw new InvalidWorkflowDescriptorException(
                    new StringBuffer()
                        .append("Conditional result from ")
                        .append(((ActionDescriptor)desc.getParent()).getName())
                        .append(" to ")
                        .append(((ConditionalResultDescriptor) desc).getDestination())
                        .append(" must have at least one condition")
                    .toString());
            }
        }

    }

}
