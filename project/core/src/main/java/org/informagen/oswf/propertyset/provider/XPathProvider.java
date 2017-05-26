package org.informagen.oswf.propertyset.provider;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Provider used for obtaining a single Node, or multiple Nodes from a DOM
 * tree using an XPath expression.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 *
 * @see org.informagen.oswf.propertyset.util.XMLUtils
 */
public interface XPathProvider extends Provider {
    // M E T H O D S  -------------------------------------------------------------------------

    Node getNode(Node base, String xpath) throws ProviderInvocationException;

    NodeList getNodes(Node base, String xpath) throws ProviderInvocationException;
}
