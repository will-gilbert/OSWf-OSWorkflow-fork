package org.informagen.propertyset.util;

import org.informagen.propertyset.PropertySet;
import org.informagen.propertyset.exceptions.PropertySetException;


/**
 * The PropertySetCloner is used to copy all the properties from one PropertySet into another.
 *
 * <h3>Example</h3>
 *
 * <blockquote><code>
 *   EJBPropertySet source = new EJBPropertySet("ejb/PropertyStore","MyEJB",7);<br>
 *   XMLPropertySet dest   = new XMLPropertySet();<br>
 *   <br>
 *   PropertySetCloner cloner = new PropertySetCloner();<br>
 *   cloner.setSource( source );<br>
 *   cloner.setDestination( dest );<br>
 *   <br>
 *   cloner.cloneProperties();<br>
 *   dest.save( new FileWriter("propertyset-MyEJB-7.xml") );<br>
 * </code></blockquote>
 *
 * <p>The above example demonstrates how a PropertySetCloner can be used to export properties
 * stores in an EJBPropertySet to an XML file.</p>
 *
 * <p>If the destination PropertySet contains any properties, they will be cleared before
 * the source properties are copied across.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 */
public class PropertySetCloner {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private PropertySet destination;
    private PropertySet source;


    /**
     * Copy the contents of one propertyset into another.
     * @param src The propertyset to copy from.
     * @param dest The propertyset to copy into.
     */

    public static void clone(PropertySet src, PropertySet dest) {
        PropertySetCloner cloner = new PropertySetCloner();
        cloner.setSource(src);
        cloner.setDestination(dest);
        cloner.cloneProperties();
    }


    // M E T H O D S  -------------------------------------------------------------------------

    public void setDestination(PropertySet destination) {
        this.destination = destination;
    }

    public PropertySet getDestination() {
        return destination;
    }

    public void setSource(PropertySet source) {
        this.source = source;
    }

    public PropertySet getSource() {
        return source;
    }

    public void cloneProperties() throws PropertySetException {
        clearDestination();

        for(String key : source.getKeys()) 
            cloneProperty(key);
    }

    /**
     * Clear all properties that already exist in destination PropertySet.
     */
    private void clearDestination() throws PropertySetException {
        
        for(String key : destination.getKeys()) 
            destination.remove(key);
    }

    /**
     * Copy individual property from source to destination.
     */
    private void cloneProperty(String key) throws PropertySetException {
        
        switch (source.getType(key)) {

            case BOOLEAN:
                destination.setBoolean(key, source.getBoolean(key));
                break;

            case INT:
                destination.setInt(key, source.getInt(key));
                break;

            case LONG:
                destination.setLong(key, source.getLong(key));
                break;

            case DOUBLE:
                destination.setDouble(key, source.getDouble(key));
                break;

            case STRING:
                destination.setString(key, source.getString(key));
                break;

            case TEXT:
                destination.setText(key, source.getText(key));
                break;

            case DATE:
                destination.setDate(key, source.getDate(key));
                break;

            case OBJECT:
                destination.setObject(key, source.getObject(key));
                break;

            case XML:
                destination.setXML(key, source.getXML(key));
                break;

            case DATA:
                destination.setData(key, source.getData(key));
                break;

            case PROPERTIES:
                destination.setProperties(key, source.getProperties(key));
                break;
        }
    }
}
