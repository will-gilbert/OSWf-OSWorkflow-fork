package org.informagen.oswf.typedmap.util;

import org.informagen.oswf.typedmap.TypedMap;
import org.informagen.oswf.typedmap.exceptions.TypedMapException;


/**
 * The TypedMapCloner is used to copy all the properties from one TypedMap into another.
 *
 * <h3>Example</h3>
 *
 * <blockquote><code>
 *   EJBTypedMap source = new EJBTypedMap("ejb/PropertyStore","MyEJB",7);<br>
 *   XMLTypedMap dest   = new XMLTypedMap();<br>
 *   <br>
 *   TypedMapCloner cloner = new TypedMapCloner();<br>
 *   cloner.setSource( source );<br>
 *   cloner.setDestination( dest );<br>
 *   <br>
 *   cloner.cloneProperties();<br>
 *   dest.save( new FileWriter("propertyset-MyEJB-7.xml") );<br>
 * </code></blockquote>
 *
 * <p>The above example demonstrates how a TypedMapCloner can be used to export properties
 * stores in an EJBTypedMap to an XML file.</p>
 *
 * <p>If the destination TypedMap contains any properties, they will be cleared before
 * the source properties are copied across.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 */
public class TypedMapCloner {

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private TypedMap destination;
    private TypedMap source;


    /**
     * Copy the contents of one propertyset into another.
     * @param src The propertyset to copy from.
     * @param dest The propertyset to copy into.
     */

    public static void clone(TypedMap src, TypedMap dest) {
        TypedMapCloner cloner = new TypedMapCloner();
        cloner.setSource(src);
        cloner.setDestination(dest);
        cloner.cloneProperties();
    }


    // M E T H O D S  -------------------------------------------------------------------------

    public void setDestination(TypedMap destination) {
        this.destination = destination;
    }

    public TypedMap getDestination() {
        return destination;
    }

    public void setSource(TypedMap source) {
        this.source = source;
    }

    public TypedMap getSource() {
        return source;
    }

    public void cloneProperties() throws TypedMapException {
        clearDestination();

        for(String key : source.getKeys()) 
            cloneProperty(key);
    }

    /**
     * Clear all properties that already exist in destination TypedMap.
     */
    private void clearDestination() throws TypedMapException {
        
        for(String key : destination.getKeys()) 
            destination.remove(key);
    }

    /**
     * Copy individual property from source to destination.
     */
    private void cloneProperty(String key) throws TypedMapException {
        
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
