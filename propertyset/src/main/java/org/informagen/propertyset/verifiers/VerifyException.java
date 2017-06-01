package org.informagen.propertyset.verifiers;


/**
 *
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @version $Revision: 146 $
 */
public class VerifyException extends RuntimeException {
    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public VerifyException() {
        super();
    }

    public VerifyException(String msg) {
        super(msg);
    }
}
