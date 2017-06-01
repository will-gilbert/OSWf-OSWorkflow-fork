package org.informagen.oswf;

import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.typedmap.exceptions.TypedMapException;

public interface TypedMapStore {

    /**
     *  Gets the TypedMap for a Process Instance from the TypedMapStore
     *
     *  @param piid the process instance id
     *  @return the property set implementation
     *  @throws TypedMapException a possible exception
     */
     
    TypedMap getTypedMap(long piid) throws TypedMapException;
}
