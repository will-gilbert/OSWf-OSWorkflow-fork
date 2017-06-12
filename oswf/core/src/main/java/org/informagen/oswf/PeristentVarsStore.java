package org.informagen.oswf;

import org.informagen.oswf.exceptions.PersistentVarsException;

public interface PeristentVarsStore {

    /**
     *  Gets the persistent variables for a Process Instance from the PeristentVarsStore
     *
     *  @param piid the process instance id
     *  @return the property set implementation
     *  @throws PersistentVarsException a possible exception
     */
     
    PersistentVars getPersistentVars(long piid) throws PersistentVarsException;
}
