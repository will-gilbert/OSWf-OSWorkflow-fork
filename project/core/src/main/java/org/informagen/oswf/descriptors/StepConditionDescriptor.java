package org.informagen.oswf.descriptors;

import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;


public class StepConditionDescriptor extends AbstractDescriptor {

    protected final RestrictionDescriptor restriction;
    protected final String name;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    StepConditionDescriptor(Element stepConditionElement) {
        

        try {
            setId(Integer.parseInt(stepConditionElement.getAttribute("id")));
        } catch (NumberFormatException e) {
            // throw new IllegalArgumentException("Invalid Step Condition id value: " + stepConditionElement.getAttribute("id"));
        }

        this.restriction = new RestrictionDescriptor(XMLHelper.getChildElement(stepConditionElement, "restrict-to"));
        this.name = stepConditionElement.getAttribute("name");
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public RestrictionDescriptor getRestriction() {
        return restriction;
    }

}
