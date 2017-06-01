package org.informagen.oswf.typedmap.provider;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Provider used for obtaining a single Node, or multiple Nodes from a DOM
 * tree using an XPath expression.
 * *
 * @see org.informagen.oswf.typedmap.util.XMLUtils
 */
public interface XPathProvider extends Provider {
    // M E T H O D S  -------------------------------------------------------------------------

    Node getNode(Node base, String xpath) throws ProviderInvocationException;

    NodeList getNodes(Node base, String xpath) throws ProviderInvocationException;
}
