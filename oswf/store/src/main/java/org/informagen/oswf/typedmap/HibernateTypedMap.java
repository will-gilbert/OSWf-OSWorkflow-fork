package org.informagen.typedmap;

// This package
import org.informagen.typedmap.hibernate.HibernateTypedMapItem;
import org.informagen.typedmap.hibernate.HibernateTypedMapDAO;
import org.informagen.typedmap.hibernate.HibernateConfigurationProvider;


// OSWf - TypedMap
import org.informagen.typedmap.AbstractTypedMap;
import org.informagen.typedmap.Type;
import org.informagen.typedmap.exceptions.TypedMapException;
import org.informagen.typedmap.exceptions.IllegalValueException;

// OSWf - Utilities 
import org.informagen.oswf.util.ClassLoaderHelper;

// OSWf - TypedMap Utililites
import org.informagen.typedmap.util.ByteArray;
import org.informagen.typedmap.util.XMLUtils;
import org.informagen.typedmap.util.Base64;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java - Collections
import java.util.Collection;
import java.util.Date;
import java.util.Map;

// Java - Utilities
import java.util.TimeZone;
import java.util.Properties;

// Java - Date Formatting
import java.text.SimpleDateFormat;

// Java IO
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.IOException;

// Java Lang
import java.lang.ClassNotFoundException;


// W3C
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;



/**
 * This is the property set implementation for storing properties using Hibernate.
 * <p>
 *
 * <b>Required Args</b>
 * <ul>
 *  <li><b>piid</b> - Long that holds the ID of this Process Instance</li>
 * </ul>
 *
 * if "<b>sessionFactory</b> - hibernate sessionFactory" is not passed as an arg then init will use: <br>
 *  <b>hibernate.*</b> - configurationProperties params needed to create a hibernate sessionFactory in the propertyset config xml.
 * <br>
 * This can be any of the configs avail from hibernate.
 * <p>
 *
 * @author $Author: will gilbert $
 */


public class HibernateTypedMap extends AbstractTypedMap {

    //~ Static fields/initializers /////////////////////////////////////////////

    protected static Logger logger = LoggerFactory.getLogger(HibernateTypedMap.class);
    
    // To avoid confusion we are really saving a DOM not serialized DOM which
    //  is known generically as 'XML'
    
    //private static final int DOM = XML;

    // I N S T A N C E   F I E L D S  ---------------------------------------------------------

    protected HibernateConfigurationProvider configProvider;
    protected String entityName;
    protected Long piid;
    protected SimpleDateFormat dateFormatter;

    // C O N S T R U C T O R  -----------------------------------------------------------------

    public HibernateTypedMap(Map<String,String> configurationProperties, Map<String,Object> args) {
        init(configurationProperties, args);       
    }


    // M E T H O D S  -------------------------------------------------------------------------

    public HibernateConfigurationProvider getConfigurationProvider() {
        return configProvider;
    }

    public Collection<String> getKeys(String itemKey, Type type) throws TypedMapException {
        return configProvider.getPersistentVarsDAO().getKeys(piid, itemKey, type);
    }

    public Type getType(String itemKey) throws TypedMapException {
        
        if(exists(itemKey) == false)
            throw new TypedMapException("itemKey: " + itemKey + " does not exist.");

        // Convert database 'int' to Type enum
        return Type.getType(findByKey(itemKey).getType());
    }

    public boolean exists(String itemKey) {
    
        try {
            findByKey(itemKey);
            return (findByKey(itemKey) != null);
        } catch (TypedMapException e) {
            return false;
        }
    }

    private void init(Map<String,String> configurationProperties, Map<String,Object> args) {

        // Process 'args' -- test 'instance of'
        piid = (Long)args.get("processInstanceId");

        // Allow a short cut, piid
        if(piid == null)
            piid = (Long)args.get("piid");


        // Set the date string renderer used in the StringVal field of the
        //   database; This is a convienence for DBAs who hate Epoch time
        String timeZoneName = null;       
        String dateFormat = null;  
        
        // Were these defined in the in 'configurationProperties' map?
        if(configurationProperties != null) {
            timeZoneName = ((String)configurationProperties.get("timeZone"));
            dateFormat = ((String)configurationProperties.get("dateFormat"));
        }

        // Default Time Zone
        if(timeZoneName == null)
            timeZoneName = "GMT";       

        // Default Date Format
        if(dateFormat == null)
            dateFormat = "EEE, dd MMM yyyy HH:mm:ss Z"; // RFC 2822    

        dateFormatter = new SimpleDateFormat(dateFormat);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZoneName)); 


        // First let's see if we were given a ConfigurationProvider via 'args'
        configProvider = (HibernateConfigurationProvider)args.get("configurationProvider");

        // If we did not get given one in the args, we can check the configuration parameters
        
        if (configProvider == null)  {
        
            // Let's see if we need to use a configurationProvider from a class
            String configProviderClass = null;
            
            if(configurationProperties != null)
                configProviderClass = (String) configurationProperties.get("configuration.provider.class");

            if (configProviderClass != null) {
                
                logger.debug("Setting up property set provider of class: " + configProviderClass);

                try {
                    configProvider = (HibernateConfigurationProvider) ClassLoaderHelper.loadClass(configProviderClass, this.getClass()).newInstance();
                } catch (Exception e) {
                    logger.error("Unable to load configuration provider class: " + configProviderClass, e);
                    return;
                }
                
            } else {
                logger.debug("Setting up property set with HibernateConfigurationProvider");
                configProvider = new HibernateConfigurationProvider();
            }
        } 

        
        configProvider.setupConfiguration(configurationProperties);

    }



    public void remove(String itemKey) throws TypedMapException {
        configProvider.getPersistentVarsDAO().remove(piid, itemKey);
    }


    public void remove() throws TypedMapException {
        configProvider.getPersistentVarsDAO().remove(piid);
    }


    protected void setImpl(Type type, String itemKey, Object value) throws TypedMapException {

        HibernateTypedMapItem item = configProvider.getPersistentVarsDAO().findByKey(piid, itemKey);

        if (item == null) 
            item = configProvider.getPersistentVarsDAO().create(piid, itemKey);
        else if (Type.getType(item.getType()) != type) 
            throw new TypedMapException("Existing key '" + itemKey + "' does not have matching type of " + type);

        switch (type) {
            case BOOLEAN:
                boolean booleanVal = ((Boolean)value).booleanValue();
                item.setIntVal(booleanVal ? 1 : 0);
                item.setStringVal(booleanVal ? "true" : "false");
                break;
    
            case DOUBLE:
                item.setDoubleVal((Double)value);
                break;
    
            case STRING:
                item.setStringVal((String) value);
                break;

            case TEXT:
                item.setTextVal((String) value);
                break;
    
            case LONG:
                item.setLongVal((Long) value);
                break;
    
            case INT:
                item.setIntVal((Integer) value);
                break;
    
            case DATE:
                long millis = ((Date)value).getTime();
                item.setLongVal(Math.round(millis/1000.0));
                item.setStringVal(dateFormatter.format(new Date(millis)));
                break;
    
            case DATA:
                item.setTextVal(new String(Base64.getInstance().encode(((ByteArray)value).getBytes())));
                break;
     
            case OBJECT:
                if ((value != null) && !(value instanceof Serializable)) 
                    throw new IllegalValueException("Cannot set " + itemKey + ". Value type " + value.getClass() + " not Serializable");
                    
            case XML:
            case PROPERTIES:

                try {
                                    
                    if (value instanceof Document) {
                        // TEXT: Save as serialized XML form
                        item.setTextVal(XMLUtils.print((Document)value));
                        
                    } else if(value instanceof Properties) {
                        // TEXT: Save as name=value pairs
                        OutputStream os = new ByteArrayOutputStream();
                        ((Properties)value).store(os, null);
                        item.setTextVal(os.toString());
                        
                    } else {
                    
                        //  TEXT: Serialize then Base64 encode the object
                        item.setTextVal(encodeObject(value));
                        
                        //STRING: Save classname 
                        item.setStringVal(value.getClass().getName());
                    }
                        
                } catch (IOException ioException) {
                    throw new TypedMapException(ioException.getMessage());
                }
                break;
    
            default:
                throw new TypedMapException("type " + type + " not supported");
        }

        // Set item 'type' and save via DAO
        
        item.setType(type.getValue());
        
        configProvider.getPersistentVarsDAO().save(item);
    }

    public boolean supportsType(final Type type) {
    
        switch (type) {
            case BOOLEAN:
            case DOUBLE:
            case STRING:
            case TEXT:
            case LONG:
            case INT:
            case DATE:
            case DATA:
            case OBJECT:
            case PROPERTIES:
            case XML:
                return true;
    
            default:
                return false;
        }
    }

    protected Object get(Type type, String itemKey) throws TypedMapException {

        if(supportsType(type) == false)
            throw new TypedMapException("Type " + type(type) + " not supported");

        // Move this snippet into getImpl() -----------------
        HibernateTypedMapItem item = findByKey(itemKey);
        
        // Convert database 'int' to Type 'enum'
        Type actualType = Type.getType(item.getType());

        //---------------------------------------------------

        switch (type) {
            case BOOLEAN: //===================================================================
                switch(actualType) {
                    case BOOLEAN: return (item.getIntVal() != 0);
                    case INT:     return (item.getIntVal() != 0);
                    case LONG:    return (item.getLongVal() != 0L);
                    case DOUBLE:  return (item.getDoubleVal() != 0);
                    case STRING:
                        String string = item.getStringVal();
                        return (string != null && string.trim().length() != 0);
                    case TEXT:
                        String text = item.getTextVal();
                        return (text != null && text.trim().length() != 0);
                    case DATE:    return (item.getLongVal() > Math.round(System.currentTimeMillis()/1000.0));  // Is future date?
                }
                break;

            case INT: //=======================================================================
                switch(actualType) {
                    case BOOLEAN: return item.getIntVal();
                    case INT:     return item.getIntVal();
                    case LONG:    return item.getLongVal().intValue();
                    case DOUBLE:  return item.getDoubleVal().intValue();
                    case STRING:
                        try {
                            return Integer.parseInt(item.getStringVal());
                        } catch (NumberFormatException nfe) {
                            return 0;
                        }
                    case TEXT:
                        try {
                            return Integer.parseInt(item.getTextVal());
                        } catch (NumberFormatException nfe) {
                            return 0;
                        }
                }
                break;

            case LONG: //======================================================================
                switch(actualType) {
                    case BOOLEAN: return item.getIntVal().longValue();
                    case INT:     return item.getIntVal().longValue();
                    case LONG:    return item.getLongVal();
                    case DOUBLE:  return item.getDoubleVal().longValue();
                    case STRING:
                        try {
                            return Long.parseLong(item.getStringVal());
                        } catch (NumberFormatException nfe) {
                            return 0L;
                        }
                    case TEXT:
                        try {
                            return Long.parseLong(item.getTextVal());
                        } catch (NumberFormatException nfe) {
                            return 0L;
                        }
                    case DATE:    return item.getLongVal();
                }
                break;

    
            case DOUBLE: //====================================================================
                switch(actualType) {
                    case BOOLEAN: return (item.getIntVal() != 0) ? 1.0 : 0.0;
                    case INT:     return item.getIntVal().doubleValue();
                    case LONG:    return item.getLongVal().doubleValue();
                    case DOUBLE:  return item.getDoubleVal();
                    case STRING:
                        try {
                            return Double.parseDouble(item.getStringVal());
                        } catch (NumberFormatException nfe) {
                            return 0.0;
                        }
                    case TEXT:
                        try {
                            return Double.parseDouble(item.getTextVal());
                        } catch (NumberFormatException nfe) {
                            return 0.0;
                        }
                }
                break;
    
            case STRING: //====================================================================
                switch(actualType) {
                    case BOOLEAN: return item.getStringVal();
                    case INT:     return Integer.toString(item.getIntVal());
                    case LONG:    return Long.toString(item.getLongVal());
                    case DOUBLE:  return Double.toString(item.getDoubleVal());
                    case STRING:  return item.getStringVal();
                    case TEXT:
                        String text = item.getTextVal();
                        if (text == null)
                            return null;
                        else if (text.length() <= 255)
                            return text;
                        else
                            return text.substring(0, 255);
                    case DATE:
                            return dateFormatter.format(new Date(item.getLongVal()*1000L));
                    case OBJECT:     return item.getStringVal();
                }
                break;

            case TEXT: //======================================================================
                switch(actualType) {
                    case BOOLEAN:    return item.getStringVal();
                    case INT:        return Integer.toString(item.getIntVal());
                    case LONG:       return Long.toString(item.getLongVal());
                    case DOUBLE:     return Double.toString(item.getDoubleVal());
                    case STRING:     return item.getStringVal();
                    case DATE:
                            return dateFormatter.format(new Date(item.getLongVal()*1000L));
                    case TEXT:       return item.getTextVal();
                    case XML:        return item.getTextVal();
                    case PROPERTIES: return item.getTextVal();
                    case OBJECT:     return decodeObject(item.getTextVal()).toString();
                    
                }
                break;
                
    
            case DATE: //======================================================================
                return new Date(item.getLongVal()*1000L);
                
            case DATA: //======================================================================
                return new ByteArray(Base64.getInstance().decode(item.getTextVal()));

            case OBJECT: //====================================================================
                return decodeObject(item.getTextVal());
                
            case XML: //=======================================================================
            case PROPERTIES:
            
                try {
                    InputStream inputStream;
                    String text;
                
                    switch(type) {
                    
                        case PROPERTIES:
                            text = item.getTextVal();
                            inputStream = new ByteArrayInputStream(text.getBytes());
                            Properties properties = new Properties();
                            properties.load(inputStream);
                            return properties;

                        case XML:
                            text = item.getTextVal();
                            inputStream = new ByteArrayInputStream(text.getBytes());
                            return XMLUtils.parse(inputStream);
                    }
                    
                } catch (SAXException saxException) {
                    throw new TypedMapException(saxException.getMessage());
                } catch (ParserConfigurationException parserConfigurationException) {
                    throw new TypedMapException(parserConfigurationException.getMessage());
                } catch (IOException ioException) {
                    throw new TypedMapException(ioException.getMessage());
                }
                
        }
        
        // Couldn't do the data type conversion

        StringBuffer buffer = new StringBuffer();
        buffer.append("Key '")
              .append(itemKey)
              .append("' of type '")
              .append(type(actualType))
              .append("' does not have conversion to type '")
              .append(type.getName())
              .append("'");
                    
        throw new TypedMapException(buffer.toString()); 

    }


    private HibernateTypedMapItem findByKey(String itemKey) throws TypedMapException {
        return configProvider.getPersistentVarsDAO().findByKey(piid, itemKey);
    }


    private String encodeObject(Object object) throws TypedMapException {
    
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    
        try {
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(object);
            objectStream.close();
            byteStream.flush();
            byteStream.close();
        } catch (IOException ioException) {
            throw new TypedMapException(ioException.getMessage());
        }

        return new String(Base64.getInstance().encode(byteStream.toByteArray()));
    }

    private Object decodeObject(String base64String) throws TypedMapException {
    
        Object object = null;
        
        try {
            byte[] bytes = Base64.getInstance().decode(base64String);
            
            InputStream inputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            object = objectStream.readObject();
            objectStream.close();
            inputStream.close();
        } catch (ClassNotFoundException classNotFoundException) {
            throw new TypedMapException(classNotFoundException.getMessage());
        } catch (IOException ioException) {
            throw new TypedMapException(ioException.getMessage());
        }

        return object;
    }

}




