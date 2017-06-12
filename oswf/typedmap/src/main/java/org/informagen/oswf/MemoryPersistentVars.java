package org.informagen.oswf;

import org.informagen.oswf.*;

import org.informagen.oswf.exceptions.PersistentVarsException;
import org.informagen.oswf.exceptions.DuplicateValueKeyException;
import org.informagen.oswf.exceptions.InvalidValueTypeException;

import java.io.Serializable;

// Java - Collections
import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 * The MemoryTypedMap is a PropertySet implementation that
 *    will store any primitive or object using an internal ValueEntity
 *    class which preserve the Type of the object. 
 *
 * To use a supplied Map see MapTypedMap.
 *
 * <p>An alternative to MemoryTypedMap is SerializableTypedMap
 *     which can be Serialized to/from a stream.</p>
 *
 *
 */

public class MemoryPersistentVars extends AbstractPersistentVars {

    private HashMap<String,TypedValue> map = new HashMap<String,TypedValue>();

    // P U B L I C   M E T H O D S  -----------------------------------------------------------

    public synchronized Collection<String> getKeys(String prefix, Type type) {

        List<String> keyList = new LinkedList<String>();

        for(String key : map.keySet()) {
            
            if ((prefix == null) || key.startsWith(prefix)) {
                
                if (type == null) {
                    keyList.add(key);
                } else {
                    
                    TypedValue typedValue = getMap().get(key);

                    if (typedValue.type == type) {
                        keyList.add(key);
                    }
                }
            }
        }

        Collections.sort(keyList);
        return keyList;
    }

    public synchronized Type getType(String key) {
        if (getMap().containsKey(key)) {
            return getMap().get(key).type;
        } else {
            return null;
        }
    }

    public synchronized boolean exists(String key) {
        return getType(key) != null;
    }

    public synchronized void remove(String key) {
        getMap().remove(key);
    }

    public void remove() throws PersistentVarsException {
        map.clear();
    }


    // P R O T E C T E D   M E T H O D S  ----------------------------------------------------- 

    protected synchronized void setImpl(Type type, String key, Object value) throws DuplicateValueKeyException {
        if (exists(key)) {
            TypedValue typedValue = getMap().get(key);

            if (typedValue.type != type) {
                throw new DuplicateValueKeyException();
            }

            typedValue.value = value;
        } else {
            getMap().put(key, new TypedValue(type, value));
        }

        return;
    }

    protected Map<String,TypedValue> getMap() {
        return map;
    }

    protected synchronized Object get(Type type, String key) throws InvalidValueTypeException {
        if (exists(key)) {
            TypedValue typedValue = getMap().get(key);

            if (typedValue.type != type) {
                throw new InvalidValueTypeException();
            }

            return typedValue.value;
        } else {
            return null;
        }
    }

    // I N N E R   C L A S S :   T y p e d V a l u e  -----------------------------------------

    public static final class TypedValue implements Serializable {
        
        Object value;
        Type type;

        private TypedValue() {}

        public TypedValue(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        // Type
        public void setType(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        // Value
        public void setValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}
