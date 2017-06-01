package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.*;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;
import org.informagen.oswf.propertyset.exceptions.IllegalPropertyException;
import org.informagen.oswf.propertyset.exceptions.DuplicatePropertyKeyException;

import java.io.Serializable;

// Java - Collections
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;


/**
 * The SerializablePropertySet is a PropertySet implementation that
 *      will only store primitive of serializable objects in 
 *      an internal Map which is stored in memory.
 * *
 * <p>This offers the most basic form of persistence. Note that
 *      <code>setObject()</code> will throw an IllegalPropertyException if
 *  the passed object does not implement Serializable.</p>
 *
 */


public class SerializablePropertySet extends MemoryPropertySet implements Serializable {
        
    // M E T H O D  ---------------------------------------------------------------------------
    
    /**
     **  Incoming objects must be 'serializable'; AutoBoxed primative are by definition
     **     'serializable'
     */

    protected synchronized void setImpl(Type type, String key, Object value) throws IllegalPropertyException, DuplicatePropertyKeyException {
        if ((value != null) && !(value instanceof Serializable)) {
            throw new IllegalPropertyException("Cannot set " + key + ". Value type " + value.getClass() + " not Serializable");
        }

        super.setImpl(type, key, value);
    }}
