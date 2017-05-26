package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.util.Base64;

import org.informagen.oswf.propertyset.MemoryPropertySet;

import org.informagen.oswf.propertyset.util.ByteArray;
import org.informagen.oswf.propertyset.util.XMLUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;

//import java.io.*;
// Java - I/O
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;

// Java - Collections
import java.util.Collection;
import java.util.Map;

// Java - Util
import java.util.Date;
import java.util.Properties;


/**
 **  Support the behavior of the MemoryPropertySet with two additional methods 
 **     which can save to and load from a Properties file
 ** 
 */
public class FilePropertySet extends MemoryPropertySet {

    private final File file;
    
    public FilePropertySet(String fileName) throws IOException, ClassNotFoundException {
        this(new File(fileName));
    }

    public FilePropertySet(File file) throws IOException, ClassNotFoundException {
        this.file = file;

        // If the file aleady exists, load its content into the PropertySet
        if ( file.exists() ) 
            load();
    }


    // M E T H O D S --------------------------------------------------------------------------

    public void store() throws IOException {
        
        Properties properties = new Properties();
        
        for(Map.Entry<String,TypedValue> entry : getMap().entrySet()) {
            
            String name = entry.getKey();
            TypedValue typedValue =  entry.getValue();
            String value;

            switch (typedValue.getType()) {
                case XML:
                    value = XMLUtils.print((Document) typedValue.getValue());
                    break;

                case PROPERTIES:
                case OBJECT:
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(bos);
                    os.writeObject(typedValue.getValue());

                    byte[] data = bos.toByteArray();
                    value = new String(Base64.getInstance().encode(data));
                    break;

                case DATE:
                    value = DateFormat.getDateTimeInstance().format((Date) typedValue.getValue());
                    break;

                case DATA:
                    if (typedValue.getValue() instanceof byte[]) {
                        value = new String(Base64.getInstance().encode((byte[]) typedValue.getValue()));
                    } else {
                        value = new String(Base64.getInstance().encode(((ByteArray) typedValue.getValue()).getBytes()));
                    }
                    break;

                default:
                    value = typedValue.getValue().toString();
            }

            properties.put(name + "." + typedValue.getType(), value);
        }

        properties.store(new FileOutputStream(file), null);
    }


    public void load() throws IOException, ClassNotFoundException {
        
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        for(String name : properties.stringPropertyNames()) {

            int dot = name.lastIndexOf('.');
            Type type = Type.getType(Integer.parseInt(name.substring(dot + 1)));
            String key = name.substring(0, dot);

            String data = properties.getProperty(name);
            Object value = null;

            switch(type) {
                case STRING:
                case TEXT:
                    value = data;
                    break;

                case INT:
                    value = new Integer(data);
                    break;

                case LONG:
                    value = new Long(data);
                    break;

                case DOUBLE:
                    value = new Double(data);
                    break;

                case BOOLEAN:
                    value = new Boolean(data);
                    break;

                case DATA:
                    value = Base64.getInstance().decode(data);
                    break;

                case DATE:

                    try {
                        value = DateFormat.getDateTimeInstance().parse(data);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case PROPERTIES:
                case OBJECT:
                    byte[] bytes = Base64.getInstance().decode(data);
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    value = is.readObject();
                    break;

                case XML:
                    try {
                        value = XMLUtils.parse(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    throw new IOException("Unsupported type " + type);
            }

            getMap().put(key, new TypedValue(type, value));
        }
    }
}
