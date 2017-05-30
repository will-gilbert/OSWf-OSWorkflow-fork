package org.informagen.oswf.propertyset;

import org.informagen.oswf.propertyset.PropertySet;

import org.informagen.oswf.propertyset.exceptions.PropertyImplementationException;

import org.informagen.oswf.propertyset.util.Base64;

import org.informagen.oswf.propertyset.SerializablePropertySet;

import org.informagen.oswf.propertyset.util.ByteArray;
import org.informagen.oswf.propertyset.util.TextUtils;
import org.informagen.oswf.propertyset.util.XMLUtils;

import org.w3c.dom.*;

import org.xml.sax.SAXException;

import java.io.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


/**
 * The XMLPropertySet behaves as an in-memory typed PropertySet, with the ability to
 * load and save all the properties to/from an XML document.
 *
 * <ul>
 * <li>boolean, int, long, double and String properties are saved as simple Text nodes.</li>
 * <li>text and XML properties are stored as CDATA blocks.</li>
 * <li>java.util.Date properties are stored in <code>yyyy-mm-dd HH:mm:ss</code> format.</li>
 * <li>java.util.Properties properties are stored in child elements.</li>
 * <li>java.lang.Object and byte[] data properties are encoded using base64 into text and stored as CDATA blocks.</li>
 * </ul>
 *
 * <h3>Example:</h3>
 * <blockquote><code>
 * XMLPropertySet p = new XMLPropertySet(); // create blank property-set<br>
 * p.load( new FileReader("my-properties.xml") ); // load properties from XML.<br>
 * System.out.println( p.getString("name") );<br>
 * p.setString("name","blah blah");<br>
 * p.save( new FileWriter("my-properties.xml") ); // save properties back to XML.<br>
 * </code></blockquote>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 169 $
 *
 */
public class XMLPropertySet extends SerializablePropertySet {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    // M E T H O D S  -------------------------------------------------------------------------

    /**
    * Load properties from XML input.
    */
    public void load(Reader in) throws ParserConfigurationException, IOException, SAXException {
        loadFromDocument(XMLUtils.parse(in));
    }

    /**
    * Load properties from XML input.
    */
    public void load(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        loadFromDocument(XMLUtils.parse(in));
    }

    /**
    * Load properties from XML document.
    */
    public void loadFromDocument(Document doc) throws PropertyImplementationException {
        try {
            NodeList nodeList = XMLUtils.xpathList(doc, "/property-set/property");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                String key = e.getAttribute("key");
                Type type = type(e.getAttribute("type"));
                Object value = loadValue(e, key, type);

                if (value != null) {
                    setImpl(type, key, value);
                }
            }
        } catch (TransformerException e) {
            throw new PropertyImplementationException(e);
        }
    }

    /**
    * Save properties to XML output.
    */
    public void save(Writer out) throws ParserConfigurationException, IOException {
        XMLUtils.print(saveToDocument(), out);
    }

    /**
    * Save properties to XML output.
    */
    public void save(OutputStream out) throws ParserConfigurationException, IOException {
        XMLUtils.print(saveToDocument(), out);
    }

    /**
    * Save properties to XML Document.
    */
    public Document saveToDocument() throws ParserConfigurationException {
        Document doc = XMLUtils.newDocument("property-set");

        for(String key : getKeys()) {
            Type type = getType(key);
            Object value = get(type, key);
            saveValue(doc, key, type, value);
        }

        return doc;
    }

    /**
    * Load value from <property>...</property> tag. Null returned if value cannot be determined.
    */
    private Object loadValue(Element e, String key, Type type) {
        String text = XMLUtils.getElementText(e);

        switch (type) {
        case BOOLEAN:
            return new Boolean(TextUtils.parseBoolean(text));

        case INT:
            return new Integer(TextUtils.parseInt(text));

        case LONG:
            return new Long(TextUtils.parseLong(text));

        case DOUBLE:
            return new Double(TextUtils.parseDouble(text));

        case STRING:
        case TEXT:
            return text;

        case DATE:

            try {
                return dateFormat.parse(text);
            } catch (ParseException pe) {
                return null; // if the date cannot be parsed, ignore it.
            }

        case OBJECT:

            try {
                return Base64.getInstance().decodeObject(text);
            } catch (Exception ex) {
                return null; // if Object cannot be decoded, ignore it.
            }

        case XML:

            try {
                return XMLUtils.parse(text);
            } catch (Exception ex) {
                return null; // if XML cannot be parsed, ignore it.
            }

        case DATA:
                return new ByteArray(Base64.getInstance().decode(text));

        case PROPERTIES:

            try {
                Properties props = new Properties();
                NodeList pElements = XMLUtils.xpathList(e, "properties/property");

                for (int i = 0; i < pElements.getLength(); i++) {
                    Element pElement = (Element) pElements.item(i);
                    props.put(pElement.getAttribute("key"), XMLUtils.getElementText(pElement));
                }

                return props;
            } catch (TransformerException te) {
                return null; // could not get nodes via x-path
            }

        default:
            return null;
        }
    }

    /**
    * Append <property key="..." type="...">...</property> tag to document.
    */
    private void saveValue(Document doc, String key, Type type, Object value) {
        Element element = doc.createElement("property");
        element.setAttribute("key", key);
        element.setAttribute("type", type(type));

        Node valueNode;

        switch (type) {
        case BOOLEAN:
        case INT:
        case LONG:
        case DOUBLE:
        case STRING:
            valueNode = doc.createTextNode(value.toString());

            break;

        case TEXT:
            valueNode = doc.createCDATASection(value.toString());

            break;

        case DATE:
            valueNode = doc.createTextNode(dateFormat.format((Date) value));

            break;

        case OBJECT:

            try {
                valueNode = doc.createCDATASection(Base64.getInstance().encodeObject(value));
            } catch (IOException ioe) {
                return; // cannot save Object - carry on with rest of properties.
            }

            break;

        case XML:

            try {
                valueNode = doc.createCDATASection(XMLUtils.print((Document) value));
            } catch (IOException ioe) {
                return; // cannot serialize XML - carry on with rest of properties.
            }

            break;

        case DATA:
                valueNode = doc.createCDATASection(new String(Base64.getInstance().encode(((ByteArray) value).getBytes())));

            break;

        case PROPERTIES: { // scoping block
            valueNode = doc.createElement("properties");

            // WAG - fix this to return String keys
            Properties props = (Properties) value;
            
            for (Object pKey : props.keySet()) {
                Element pElement = doc.createElement("property");
                pElement.setAttribute("key", pKey.toString());
                pElement.setAttribute("type", "string");
                pElement.appendChild(doc.createTextNode(props.getProperty(pKey.toString())));
                valueNode.appendChild(pElement);
            }
        }

        break;

        default:
            return; // if type not recognised, stop now.
        }

        element.appendChild(valueNode);

        doc.getDocumentElement().appendChild(element);
    }
}
