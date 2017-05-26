package org.informagen.oswf;

import org.informagen.oswf.propertyset.PropertySet;
import org.informagen.oswf.propertyset.exceptions.PropertySetException;

public interface PropertySetStore {

    /**
     *  Gets the PropertySet for a Process Instance from the PropertySetStore
     *
     */
     
    PropertySet getPropertySet(long piid) throws PropertySetException;
}
