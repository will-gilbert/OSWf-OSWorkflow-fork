package org.informagen.oswf.typedmap.provider.xpath;

import org.informagen.oswf.typedmap.provider.*;

import java.io.IOException;

import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;


/**
 * XPathProvider implementation that uses the XPath capabilities of Xalan 1.x.
 *
 */
public class JavaXPathProvider implements XPathProvider {
    // M E T H O D S  -------------------------------------------------------------------------

    public Node getNode(Node base, String xpathAsString) throws ProviderInvocationException {
        
        Node node = null;

        try {

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathAsString);

            Object result = expr.evaluate(base, XPathConstants.NODE);
            node = (Node) result;

        } catch (XPathExpressionException e) {
            throw new ProviderInvocationException(e);
        }
        
        return node;
    }

    public NodeList getNodes(Node base, String xpathAsString) throws ProviderInvocationException {

        NodeList nodes = null;
        
        try {

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathAsString);

            Object result = expr.evaluate(base, XPathConstants.NODESET);
            nodes = (NodeList) result;


        } catch (XPathExpressionException e) {
            throw new ProviderInvocationException(e);
        }
        
        return nodes;
    }

    public void destroy() {
    }

    public void init() throws ProviderConfigurationException {
    }
}
