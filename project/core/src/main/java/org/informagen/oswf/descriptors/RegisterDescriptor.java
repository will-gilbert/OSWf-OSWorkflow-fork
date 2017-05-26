package org.informagen.oswf.descriptors;


import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

import java.util.*;


/**
 * 
 */
public class RegisterDescriptor extends AbstractDescriptor {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected final Map<String,String> args = new HashMap<String,String>();
    protected final String type;
    protected final String variableName;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    RegisterDescriptor(Element registerElement) {
        
        this.type = registerElement.getAttribute("type");
        this.variableName = registerElement.getAttribute("variable-name");

        try {
            setId(Integer.parseInt(registerElement.getAttribute("id")));
        } catch (NumberFormatException e) {
        }

        List<Element> argElements = XMLHelper.getChildElements(registerElement, "arg");

        for (Element argElement : argElements) 
            this.args.put(argElement.getAttribute("name"), XMLHelper.getText(argElement));

    }

    // M E T H O D S    -----------------------------------------------------------------------

    public Map<String,String> getArgs() {
        return args;
    }

    public String getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }

}
