package org.informagen.oswf.descriptors;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import org.informagen.oswf.util.ValidationHelper;
import org.informagen.oswf.util.XMLHelper;

import org.w3c.dom.Element;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class SplitDescriptor extends AbstractDescriptor implements Validatable {

    protected final List<ResultDescriptor> resultDescriptors = new ArrayList<ResultDescriptor>();

    // C O N S T R U C T O R  -----------------------------------------------------------------

    SplitDescriptor(Element splitElement) {
        
        try {
            setId(Integer.parseInt(splitElement.getAttribute("id")));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid split id value " + splitElement.getAttribute("id"));
        }

        // Look for all 'default-result' elements then add any deprecated 'unconditional-result' elements
        List<Element> resultElements = XMLHelper.getChildElements(splitElement, "default-result");
        resultElements.addAll(XMLHelper.getChildElements(splitElement, "unconditional-result"));


        for (Element resultElement : resultElements) {
            ResultDescriptor resultDescriptor = factory.createResultDescriptor(resultElement);
            resultDescriptor.setParent(this);
            resultDescriptors.add(resultDescriptor);
        }
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public List<ResultDescriptor> getResults() {
        return resultDescriptors;
    }

    public void validate() throws InvalidWorkflowDescriptorException {
        ValidationHelper.validate(resultDescriptors);
    }

}
