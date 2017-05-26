package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.*;

import org.informagen.oswf.propertyset.exceptions.PropertySetException;
import org.informagen.oswf.propertyset.exceptions.DuplicatePropertyKeyException;
import org.informagen.oswf.propertyset.exceptions.InvalidPropertyTypeException;

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
 * The MemoryPropertySet is a PropertySet implementation that
 *    will store any primitive or object using an internal ValueEntity
 *    class which preserve the Type of the object. 
 *
 * To use a supplied Map see MapPropertySet.
 *
 * <p>An alternative to MemoryPropertySet is SerializablePropertySet
 *     which can be Serialized to/from a stream.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 *
 * @see import org.informagen.oswf.propertyset.PropertySet
 */

public class MemoryPropertySet extends AbstractPropertySet {

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

    public void remove() throws PropertySetException {
        map.clear();
    }


    // P R O T E C T E D   M E T H O D S  ----------------------------------------------------- 

    protected synchronized void setImpl(Type type, String key, Object value) throws DuplicatePropertyKeyException {
        if (exists(key)) {
            TypedValue typedValue = getMap().get(key);

            if (typedValue.type != type) {
                throw new DuplicatePropertyKeyException();
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

    protected synchronized Object get(Type type, String key) throws InvalidPropertyTypeException {
        if (exists(key)) {
            TypedValue typedValue = getMap().get(key);

            if (typedValue.type != type) {
                throw new InvalidPropertyTypeException();
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
