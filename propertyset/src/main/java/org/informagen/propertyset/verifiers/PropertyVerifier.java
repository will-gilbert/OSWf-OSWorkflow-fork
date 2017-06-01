package org.informagen.propertyset.verifiers;

import java.io.Serializable;


/**
 *
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @version $Revision: 144 $
 */
public interface PropertyVerifier extends Serializable {
    // M E T H O D S  -------------------------------------------------------------------------

    public void verify(Object value) throws VerifyException;
}
