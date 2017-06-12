package org.informagen.oswf;

import org.informagen.oswf.exceptions.PersistentVarsException;

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
 * {DuplicateValueKeyException
 * should be thrown.</p>
 *
 * <p>If a property is set of a type that is not allowed, a
 * IllegalValueException
 * should be thrown.</p>
 *
 * <p>If a property is retrieved that exists but contains a value of different
 * type, a
 * InvalidValueTypeException
 * should be thrown.</p>
 *
 * <p>If a property is retrieved that does not exist, null (or the primitive
 * equivalent) is returned.</p>
 *
 * <p>Some PropertySet implementations may not store along side the data the original
 * type it was set as. This means that it could be retrieved using a get method of
 * a different type without throwing an InvalidValueTypeException (so long as the
 * original type can be converted to the requested type.</p>
 *
 * <p><b>Typed PropertySet Example</b></p>
 *
 * <p><code>
 * propertySet.setString("something","99");<br>
 * x = propertySet.getString("something"); // throws InvalidValueTypeException
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
public interface PersistentVars {

    // S E T T E R S   A N D   G E T T E R S  -------------------------------------------------
    
    void setAsActualType(String key, Object value) throws PersistentVarsException;
    Object getAsActualType(String key) throws PersistentVarsException;

    void setBoolean(String key, boolean value) throws PersistentVarsException;
    boolean getBoolean(String key) throws PersistentVarsException;

    void setData(String key, byte[] value) throws PersistentVarsException;
    byte[] getData(String key) throws PersistentVarsException;

    void setDate(String key, Date value) throws PersistentVarsException;
    Date getDate(String key) throws PersistentVarsException;

    void setDouble(String key, double value) throws PersistentVarsException;
    double getDouble(String key) throws PersistentVarsException;

    void setInt(String key, int value) throws PersistentVarsException;
    int getInt(String key) throws PersistentVarsException;

    void setLong(String key, long value) throws PersistentVarsException;
    long getLong(String key) throws PersistentVarsException;

    void setObject(String key, Object value) throws PersistentVarsException;
    Object getObject(String key) throws PersistentVarsException;

    void setProperties(String key, Properties value) throws PersistentVarsException;
    Properties getProperties(String key) throws PersistentVarsException;

    void setString(String key, String value) throws PersistentVarsException;
    String getString(String key) throws PersistentVarsException;

    void setText(String key, String value) throws PersistentVarsException;
    String getText(String key) throws PersistentVarsException;

    void setXML(String key, Document value) throws PersistentVarsException;
    Document getXML(String key) throws PersistentVarsException;

    // R E M O V E   P R O P E R T I E S  ----------------------------------------------------- 

    /**
    * Removes property.
    */
    void remove(String key) throws PersistentVarsException;

    /**
     * Remove the propertyset and all it associated keys.
     * @throws PersistentVarsException if there is an error removing the propertyset.
     */
    void remove() throws PersistentVarsException;

    // K E Y S  -------------------------------------------------------------------------------

    /**
    * List all keys.
    *
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */

    Collection<String> getKeys() throws PersistentVarsException;

    /**
    * List all keys of certain type.
    *
    * @param type Type to list. See static class variables. If null, then
    *        all types shall be returned.
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */
    Collection<String> getKeys(Type type) throws PersistentVarsException;

    /**
    * List all keys starting with supplied prefix.
    *
    * @param prefix String that keys must start with. If null, than all
    *        keys shall be returned.
    * @return Unmodifiable {@link java.util.Collection} of {@link java.lang.String}s.
    */
    Collection<String> getKeys(String prefix) throws PersistentVarsException;

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
    Collection<String> getKeys(String prefix, Type type) throws PersistentVarsException;


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
    
    Type getType(String key) throws PersistentVarsException;

    /**
    * Determine if property exists.
    */
    boolean exists(String key) throws PersistentVarsException;

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
