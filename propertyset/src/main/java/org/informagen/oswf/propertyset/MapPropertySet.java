package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.AbstractPropertySet;
import org.informagen.oswf.propertyset.exceptions.PropertySetException;

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
 * The MapPropertySet is an UNTYPED PropertySet implementation that
 *     acts as a wrapper around a standard {@link java.util.Map} .
 *
 * <p>Because a Map will only store the value but not the type, this
 *     is untyped. </p>
 *
 * <b>Optional Args</b>
 * <ul>
 *  <li><b>map</b> - the map that will back this PropertySet</li>
 * </ul>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 *
 */
public class MapPropertySet extends AbstractPropertySet {
    
    private final Map<String,Object> map;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public MapPropertySet() {
        this.map = new HashMap<String,Object>();
    }

    public MapPropertySet(Map<String,Object> map) {
        this.map = (map != null) ? map : new HashMap<String,Object>();
    }




    // M E T H O D S  -------------------------------------------------------------------------

    /**
    * The type parameter is ignored.
    */
    public synchronized Collection<String> getKeys(String prefix, Type type) {

        List<String> keyList = new LinkedList<String>();

        for(String key : map.keySet())
            if ((prefix == null) || key.startsWith(prefix))
                keyList.add(key);

        Collections.sort(keyList);

        return keyList;
    }

    /**
    * This is an untyped PropertySet implementation so this method will always
    * throw {@link java.lang.UnsupportedOperationException} .
    */

    public Type getType(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PropertySet does not support types");
    }

    public synchronized boolean exists(String key) {
        return map.containsKey(key);
    }

    public synchronized void remove(String key) {
        map.remove(key);
    }

    public void remove() throws PropertySetException {
        map.clear();
    }

    /**
    * Returns false.
    */
    
    public boolean supportsType(Type type) {
        return false;
    }

    /**
    * Returns false.
    */
    
    public boolean supportsTypes() {
        return false;
    }

    /**
    * The type parameter is ignored.
    */
    
    protected synchronized void setImpl(Type type, String key, Object value) {
        map.put(key, value);
    }

    /**
    * The type parameter is ignored.
    */
    
    protected synchronized Object get(Type type, String key) {
        return exists(key) ? map.get(key) : null;
    }
}
