package org.informagen.oswf.descriptors;

import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.*;


/**
 * Desrives a function that can be applied to a workflow step.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class FunctionDescriptor extends AbstractDescriptor {

    protected final Map<String,String> args = new HashMap<String,String>();

    protected final String name;
    protected final String type;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    FunctionDescriptor(Element functionElement) {
        
        type = functionElement.getAttribute("type");

        try {
            setId(Integer.parseInt(functionElement.getAttribute("id")));
        } catch (NumberFormatException e) { }

        name = functionElement.getAttribute("name");

        List<Element> argElements = XMLHelper.getChildElements(functionElement, "arg");

        for (Element argElement : argElements) 
            args.put(argElement.getAttribute("name"), XMLHelper.getText(argElement));
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
