package org.informagen.oswf.typedmap.verifiers;

import java.io.Serializable;


/**
 *
 */
public interface ValueVerifier extends Serializable {
    // M E T H O D S  -------------------------------------------------------------------------

    public void verify(Object value) throws VerifyException;
}
