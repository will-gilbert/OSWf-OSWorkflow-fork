package org.informagen.oswf.descriptors;


import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

// Java - Collections
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 * A validator is a helper used to verify values in the input map that is
 * provided to every action call.
 *
 */
public class ValidatorDescriptor extends AbstractDescriptor {

    protected final Map<String,String> args = new HashMap<String,String>();
    protected final String name;
    protected final String type;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    ValidatorDescriptor(Element validatorElement) {
        
        this.type = validatorElement.getAttribute("type");
        this.name = validatorElement.getAttribute("name");

        try {
            setId(Integer.parseInt(validatorElement.getAttribute("id")));
        } catch (NumberFormatException e) {}

        List<Element> argsElements = XMLHelper.getChildElements(validatorElement, "arg");

        for (Element argElement : argsElements) 
            this.args.put(argElement.getAttribute("name"), XMLHelper.getText(argElement));
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public Map<String,String> getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
