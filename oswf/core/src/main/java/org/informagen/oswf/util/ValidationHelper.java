package org.informagen.oswf.util;

import org.informagen.oswf.exceptions.InvalidWorkflowDescriptorException;
import org.informagen.oswf.Validatable;

import java.util.Collection;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class ValidationHelper {

    // M E T H O D S  -------------------------------------------------------------------------

    public static void validate(Collection collection) throws InvalidWorkflowDescriptorException {

        for(Object object : collection) {
            if (object instanceof Validatable) {
                ((Validatable) object).validate();
            }
        }
    }
}
