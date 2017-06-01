package org.informagen.oswf.exceptions;

// OSWf Exceptions
import org.informagen.oswf.exceptions.WorkflowException;


import java.util.*;


/**
 * Exception to indicate the user input is invalid. Handles both general errors and errors specific to an input.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Patrick Lightbody</a>
 * @version $Revision: 1.2 $
 */
public class InvalidInputException extends WorkflowException {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private List<String> genericErrors = new ArrayList<String>();
    private Map errors = new HashMap();

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public InvalidInputException() {
        super();
    }

    /**
     * Creates a new exception using the supplied Object
     * as a generic base. If the object is an instance of
     * this exception, all properties are copied to this
     * exception. If the object is an instance of Map or
     * String[], an (errorName,errorMessage) mapping will
     * be attempted to be extracted. If the object is
     * something else, it's toString() method will be
     * called and added as a single generic error.
     *
     * @param o the object
     */
    public InvalidInputException(Object o) {
        if (o instanceof InvalidInputException) {
            InvalidInputException iie = (InvalidInputException) o;
            errors = iie.errors;
            genericErrors = iie.genericErrors;
        } else if (o instanceof Map) {
            errors = (Map) o;
        } else if (o instanceof String[]) {
            String[] stringMap = (String[]) o;
            int length = stringMap.length;
            String name = null;

            for (int i = 0; i < length; i++) {
                if ((i % 2) == 0) {
                    name = stringMap[i];
                } else {
                    addError(name, stringMap[i]);
                }
            }
        } else {
            addError(o.toString());
        }
    }

    /**
     * Creates a new exception with an associated generic error.
     *
     * @param error a generic error message
     */
    public InvalidInputException(String error) {
        super(error);
        addError(error);
    }

    /**
     * Creates a new exception with an error specific to an input.
     *
     * @param name the input name that contains the error
     * @param error an error about the given name
     */
    public InvalidInputException(String name, String error) {
        super();
        addError(name, error);
    }

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Returns a map (String,String) of the input-specific errors.
     *
     * @return a map (String,String) of the input-specific errors
     */
    public Map getErrors() {
        return errors;
    }

    /**
     * Returns a list (String) of generic errors.
     *
     * @return A list (String) of generic errors
     */
    public List<String> getGenericErrors() {
        return genericErrors;
    }

    /**
     * Adds a generic error.
     *
     * @param error the generic error message
     */
    public void addError(String error) {
        genericErrors.add(error);
    }

    /**
     * Adds an input-specific error.
     *
     * @param name the name of the input
     * @param error the error message
     */
    public void addError(String name, String error) {
        errors.put(name, error);
    }

    public String toString() {
        return "[InvalidInputException: [Error map: [" + errors + "]] [Error list: [" + genericErrors + "]]";
    }
}
