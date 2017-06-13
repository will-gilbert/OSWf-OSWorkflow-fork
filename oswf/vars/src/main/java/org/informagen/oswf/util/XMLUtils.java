package org.informagen.oswf.util;


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

import javax.xml.xpath.*;

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
    

    // M E T H O D S  -------------------------------------------------------------------------

    /**
     * Pretty-print a Document back to String of XML.
     */
    public final static String print(Document document) throws IOException {
        StringWriter writer = new StringWriter();
        new XMLPrinterWriter().print(document, writer);
        return writer.toString();
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
     * Parse a String containing XML data into a Document.
     * Note that String contains XML itself and is not URI.
     */
    public final static Document parse(String xml) throws ParserConfigurationException, IOException, SAXException {
        return parse(new InputSource(new StringReader(xml)));
    }

    /**
     * Return single Node from base Node using X-Path expression.
     */
    public final static Node xpath(Node base, String xpathAsString)  {

        try {
            Node node = null;

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathAsString);

            Object result = expr.evaluate(base, XPathConstants.NODE);
            node = (Node) result;
            return node;

        } catch (Throwable tw) {
            tw.printStackTrace();
            return null;
        }
    }

    /**
     * Return multiple Nodes from base Node using X-Path expression.
     */
    public final static NodeList xpathList(Node base, String xpathAsString)  {
        try {
            
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathAsString);

            Object result = expr.evaluate(base, XPathConstants.NODESET);
            return (NodeList) result;
        } catch (Throwable tw) {
            tw.printStackTrace();
            return null;
        }
    }


}
