package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

// OSWf Exceptions
import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;

// Java - W3C XML Parsing
import org.w3c.dom.Element;

import java.io.PrintWriter;

// Java - Collections
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */


public class JoinDescriptor extends AbstractDescriptor implements Validatable {

    protected final List<ConditionsDescriptor> conditions = new ArrayList();
    protected final ResultDescriptor result;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    JoinDescriptor(Element joinElement) {
        
        try {
            setId(Integer.parseInt(joinElement.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid join id value: " + joinElement.getAttribute("id"));
        }

        // Get conditions
        List<Element> conditionsElements = XMLHelper.getChildElements(joinElement, "conditions");

        for (Element conditionsElement : conditionsElements) {
            ConditionsDescriptor conditionsDescriptor = factory.createConditionsDescriptor(conditionsElement);
            conditionsDescriptor.setParent(this);
            this.conditions.add(conditionsDescriptor);
        }

        // Look for 'default-result' then check for deprecated 'unconditional-result' if not found
        Element resultElement = XMLHelper.getChildElement(joinElement, "default-result");

        if(resultElement == null)
            resultElement = XMLHelper.getChildElement(joinElement, "unconditional-result");
        
        // This allows loading a workflow with Joins without default-results; 
        //   Validate will check for this throw an exception
        
        if (resultElement != null) {
            result = factory.createResultDescriptor(resultElement);
            result.setParent(this);
        } else
            result = null;
    }

    // M E T H O D S  ------------------------------------------------------------------------

    public List<ConditionsDescriptor> getConditions() {
        return conditions;
    }

    public ResultDescriptor getResult() {
        return result;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(conditions);

        if (result == null) {
            throw new InvalidWorkflowDescriptorException("Join has no result");
        }

        result.validate();
    }

}
