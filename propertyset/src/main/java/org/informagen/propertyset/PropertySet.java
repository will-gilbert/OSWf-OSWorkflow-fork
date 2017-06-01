package org.informagen.propertyset;

import org.informagen.propertyset.Type;
import org.informagen.propertyset.exceptions.PropertySetException;

import org.w3c.dom.Document;

// Java - Collections
import java.util.Collection;
import java.util.Map;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 * A <code>PropertySet</code> is designed to be associated with other entities
 * in the system for storing key/value property pairs.
 *
 * <p>A key can only contain one value and a key is unique across all types. If
 * a property is set using the same key and an already existing property of the
 * SAME type, the new value will overwrite the old. However, if a property of
 * DIFFERENT type attempts to overwrite the existing value, a
 * {DuplicatePropertyKeyException
 * should be thrown.</p>
 *
 * <p>If a property is set of a type that is not allowed, a
 * IllegalPropertyException
 * should be thrown.</p>
 *
 * <p>If a property is retrieved that exists but contains a value of different
 * type, a
 * InvalidPropertyTypeException
 * should be thrown.</p>
 *
 * <p>If a property is retrieved that does not exist, null (or the primitive
 * equivalent) is returned.</p>
 *
 * <p>If an Exception is encountered in the actual implementation of the
 * PropertySet that needs to be rethrown, it should be wrapped in a
 * PropertyImplementationException
 * .</p>
 *
 * <p>Some PropertySet implementations may not store along side the data the original
 * type it was set as. This means that it could be retrieved using a get method of
 * a different type without throwing an InvalidPropertyTypeException (so long as the
 * original type can be converted to the requested type.</p>
 *
 * <p><b>Typed PropertySet Example</b></p>
 *
 * <p><code>
 * propertySet.setString("something","99");<br>
 * x = propertySet.getString("something"); // throws InvalidPropertyTypeException
 * </code></p>
 *
 * <p><b>Untyped PropertySet Example</b></p>
 *
 * <p><code>
 * propertySet.setString("something","99");<br>
 * x = propertySet.getString("something"); // returns 99.
 * </code></p>
 *
 * <p>Typically (unless otherwise stated), an implementation is typed. This can be
 * checked by calling the {@link #supportsTypes()} method of the implementation.</p>
 *
 * <p>Not all PropertySet implementations need to support setter methods (i.e.
 * they are read only) and not all have to support storage/retrieval of specific
 * types. The capabilities of the specific implementation can be determined by
 * callingsupportsType(int) and isSettable(String).</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 */
public interface PropertySet {

    // S E T T E R S   A N D   G E T T E R S  -------------------------------------------------
    
    void setAsActualType(String key, Object value) throws PropertySetException;
    Object getAsActualType(String key) throws PropertySetException;

    void setBoolean(String key, boolean value) throws PropertySetException;
    boolean getBoolean(String key) throws PropertySetException;

    void setData(String key, byte[] value) throws PropertySetException;
    byte[] getData(String key) throws PropertySetException;

    void setDate(String key, Date value) throws PropertySetException;
    Date getDate(String key) throws PropertySetException;

    void setDouble(String key, double value) throws PropertySetException;
    double getDouble(String key) throws PropertySetException;

    void setInt(String key, int value) throws PropertySetException;
    int getInt(String key) throws PropertySetException;

    void setLong(String key, long value) throws PropertySetException;
    long getLong(String key) throws PropertySetException;

    void setObject(String key, Object value) throws PropertySetException;
    Object getObject(String key) throws PropertySetException;

    void setProperties(String key, Properties value) throws PropertySetException;
    Properties getProperties(String key) throws PropertySetException;

    void setString(String key, String value) throws PropertySetException;
    String getString(String key) throws PropertySetException;

    void setText(String key, String value) throws PropertySetException;
    String getText(String key) throws PropertySetException;

    void setXML(String key, Document value) throws PropertySetException;
    Document getXML(String key) throws PropertySetException;

    // R E M O V E   P R O P E R T I E S  ----------------------------------------------------- 

    /**
    * Removes property.
    */
    void remove(String key) throws PropertySetException;

    /**
     * Remove the propertyset and all it associated keys.
     * @throws PropertySetException if there is an error removing the propertyset.
     */
    void remove() throws PropertySetException;

    // K E Y S  -------------------------------------------------------------------------------

    /**
    * List all keys.
    *
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */

    Collection<String> getKeys() throws PropertySetException;

    /**
    * List all keys of certain type.
    *
    * @param type Type to list. See static class variables. If null, then
    *        all types shall be returned.
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */
    Collection<String> getKeys(Type type) throws PropertySetException;

    /**
    * List all keys starting with supplied prefix.
    *
    * @param prefix String that keys must start with. If null, than all
    *        keys shall be returned.
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */
    Collection<String> getKeys(String prefix) throws PropertySetException;

    /**
    * List all keys starting with supplied prefix of certain type. See
    * statics.
    *
    * @param prefix String that keys must start with. If null, than all
    *        keys shall be returned.
    * @param type Type to list. See static class variables. If null, then
    *        all types shall be returned.
    * @return Unmodifiable {@link java.util.Collection} of
    *         {@link java.lang.String}s.
    */
    Collection<String> getKeys(String prefix, Type type) throws PropertySetException;


    // P R O P E R T Y S E T   P R O P E R T I E S  -------------------------------------------

    /**
    * Whether this PropertySet implementation allows values to be set
    * (as opposed to read-only).
    */
    boolean isSettable(String property);

    /**
    * Returns type of value.
    *
    * @return Type of value. See static class variables.
    */
    
    Type getType(String key) throws PropertySetException;

    /**
    * Determine if property exists.
    */
    boolean exists(String key) throws PropertySetException;

    /**
    * Whether this PropertySet implementation allows the type specified
    * to be stored or retrieved.
    */
    boolean supportsType(Type type);

    /**
    * Whether this PropertySet implementation supports types when storing values.
     * (i.e. the type of data is stored as well as the actual value).
    */
    boolean supportsTypes();
}
