package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.*;


/**
 *
 */
public class ConditionDescriptor extends AbstractDescriptor implements Validatable {

    protected final Map<String,String> args = new HashMap<String,String>();

    protected final String name;
    protected final String type;
    protected final boolean negate;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    ConditionDescriptor(Element conditionElement) {
        
        this.type = conditionElement.getAttribute("type");

        try {
            setId(Integer.parseInt(conditionElement.getAttribute("id")));
        } catch (NumberFormatException e) {}

        this.negate = "true".equalsIgnoreCase(conditionElement.getAttribute("negate"));

        this.name = conditionElement.getAttribute("name");

        List<Element> argsElement = XMLHelper.getChildElements(conditionElement, "arg");

        for (Element argElement : argsElement) 
            this.args.put(argElement.getAttribute("name"), XMLHelper.getText(argElement));
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Map<String,String> getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public boolean isNegate() {
        return negate;
    }

    public String getType() {
        return type;
    }

    public void validate() throws InvalidWorkflowDescriptorException {}

}
