package org.informagen.oswf.typedmap.util;

import org.informagen.oswf.typedmap.provider.ProviderFactory;
import org.informagen.oswf.typedmap.provider.ProviderInvocationException;
import org.informagen.oswf.typedmap.provider.XMLPrinterProvider;
import org.informagen.oswf.typedmap.provider.XPathProvider;

// Provider implementations
import org.informagen.oswf.typedmap.provider.xmlprinter.PrintWriterXMLPrinterProvider;
import org.informagen.oswf.typedmap.provider.xpath.JavaXPathProvider;

// W3C DOM
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Java - I/O
import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;


import java.net.URL;

// Java - Collections
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerException;


/**
 * XMLUtils is a bunch of quick access utility methods to common XML operations.
 *
 * <p>These include:</p>
 *
 * <ul>
 * <li>Parsing XML stream into org.w3c.dom.Document.
 * <li>Creating blank Documents.
 * <li>Serializing (pretty-printing) Document back to XML stream.
 * <li>Extracting nodes using X-Path expressions.
 * </ul>
 *
 * <p>This class contains static methods only and is not meant to be instantiated. It also
 * contains only basic (common) functions - for more control access appropriate API directly.</p>
 */

public class XMLUtils {
    
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final XPathProvider xPathProvider;
    private static final XMLPrinterProvider xmlPrinterProvider;

    static {
        ProviderFactory factory = ProviderFactory.getInstance();
        xPathProvider = (XPathProvider) factory.getProvider(
            "xpath.provider", JavaXPathProvider.class.getName());
        xmlPrinterProvider = (XMLPrinterProvider) factory.getProvider(
            "xmlprinter.provider", PrintWriterXMLPrinterProvider.class.getName());
    }


    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Pretty-print a Document to Writer.
     */
    public final static void print(Document document, Writer out) throws IOException {
        xmlPrinterProvider.print(document, out);
    }

    /**
     * Pretty-print a Document to OutputStream.
     */
    public final static void print(Document document, OutputStream out) throws IOException {
        print(document, new OutputStreamWriter(out));
    }

    /**
     * Pretty-print a Document to File.
     */
    public final static void print(Document document, File file) throws IOException {
        print(document, new FileWriter(file));
    }

    /**
     * Pretty-print a Document back to String of XML.
     */
    public final static String print(Document document) throws IOException {
        StringWriter result = new StringWriter();
        print(document, result);

        return result.toString();
    }

    /**
     * Parse an InputSource of XML into Document.
     */
    public final static Document parse(InputSource in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(in);
    }

    /**
     * Parse an InputStream of XML into Document.
     */
    public final static Document parse(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(in));
    }

    /**
     * Parse a Reader of XML into Document.
     */
    public final static Document parse(Reader in) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(in));
    }

    /**
     * Parse a File of XML into Document.
     */
    public final static Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(new FileInputStream(file)));
    }

    /**
     * Parse the contents of a URL's XML into Document.
     */
    public final static Document parse(URL url) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(url.toString()));
    }

    /**
     * Parse a String containing XML data into a Document.
     * Note that String contains XML itself and is not URI.
     */
    public final static Document parse(String xml) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(new StringReader(xml)));
    }

    /**
     * Return single Node from base Node using X-Path expression.
     */
    public final static Node xpath(Node base, String xpath) throws TransformerException {
        try {
            return xPathProvider.getNode(base, xpath);
        } catch (ProviderInvocationException e) {
            try {
                throw e.getCause();
            } catch (TransformerException te) {
                throw te;
            } catch (Throwable tw) {
                tw.printStackTrace();
        
                return null;
            }
        }
    }

    /**
     * Return multiple Nodes from base Node using X-Path expression.
     */
    public final static NodeList xpathList(Node base, String xpath) throws TransformerException {
        try {
            return xPathProvider.getNodes(base, xpath);
        } catch (ProviderInvocationException e) {
            try {
                throw e.getCause();
            } catch (TransformerException te) {
                throw te;
            } catch (Throwable tw) {
                tw.printStackTrace();
        
                return null;
            }
        }
    }

    /**
     * Create blank Document.
     */
    public final static Document newDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.newDocument();
    }

    /**
     * Create blank Document, and insert root element with given name.
     */
    public final static Document newDocument(String rootElementName) throws ParserConfigurationException {
        Document doc = newDocument();
        doc.appendChild(doc.createElement(rootElementName));

        return doc;
    }
    /**
     * Return the contained text within an Element. Returns null if no text found.
     */
    public final static String getElementText(Element element) {
        NodeList nl = element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            Node c = nl.item(i);

            if (c instanceof Text) {
                return ((Text) c).getData();
            }
        }

        return null;
    }

}
