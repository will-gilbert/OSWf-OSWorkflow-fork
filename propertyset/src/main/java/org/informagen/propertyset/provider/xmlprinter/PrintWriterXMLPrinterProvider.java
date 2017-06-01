package org.informagen.propertyset.provider.xmlprinter;

import org.informagen.propertyset.provider.ProviderConfigurationException;
import org.informagen.propertyset.provider.XMLPrinterProvider;

import org.w3c.dom.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


/**
 * XMLPrinterProvider implementation with no dependencies to other printers.
 * Uses very simple recursive walk down tree writing as it goes. Lacks
 * many features (including support for namespaces), normalization,
 * and flexible formatting options, but it's fast, very small and standalone.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 */
public class PrintWriterXMLPrinterProvider implements XMLPrinterProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String INDENT = "  ";

    // M E T H O D S  -------------------------------------------------------------------------

    public void destroy() {}

    public void init() throws ProviderConfigurationException {}

    public void print(Document doc, Writer out) throws IOException {
        PrintWriter printWriter = new PrintWriter(out);
        walk(printWriter, doc, 0, false);
        printWriter.flush();
        out.flush();
    }

    /**
     * Escape text and strip line breaks.
     */
    private void escape(PrintWriter out, String str) throws IOException {
        if (str == null) {
            return;
        }

        str = str.trim();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            switch (c) {
            case '<':
                out.print("&lt;");

                break;

            case '>':
                out.print("&gt;");

                break;

            case '&':
                out.print("&amp;");

                break;

            case '"':
                out.print("&quot;");

                break;

            case '\r':
            case '\n':
                break;

            default:
                out.print(c);
            }
        }
    }

    /**
     * Add indentation.
     */
    private void indent(PrintWriter out, int level) throws IOException {
        for (int i = 0; i < level; i++) {
            out.print(INDENT);
        }
    }

    private void walk(PrintWriter out, Node node, int indent, boolean textOnly) throws IOException {
        if (node == null) {
            return;
        }

        int type = node.getNodeType();

        boolean keepFormatting = (textOnly && (type == Node.TEXT_NODE)) || (type == Node.CDATA_SECTION_NODE);

        if (!keepFormatting) {
            indent(out, indent);
        }

        switch (type) {
        // Top of document
        case Node.DOCUMENT_NODE:
            out.print("<?xml version=\"1.0\" ?>\n");
            walk(out, ((Document) node).getDocumentElement(), indent, false);

            break;

        // Element (tag)
        case Node.ELEMENT_NODE:

            // open tag
            out.print('<');
            out.print(node.getNodeName());

            // add attributes
            NamedNodeMap attrs = node.getAttributes();

            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                out.print(' ');
                out.print(attr.getNodeName());
                out.print("=\"");
                escape(out, attr.getNodeValue());
                out.print('"');
            }

            NodeList children = node.getChildNodes();

            // empty tag if no children
            if ((children == null) || (children.getLength() == 0)) {
                out.print('/');
            }

            out.print('>');

            // walk children
            if (children != null) {
                boolean nodeTextOnly = false;

                if ((children.getLength() == 1) && (children.item(0).getNodeType() == Node.TEXT_NODE)) {
                    nodeTextOnly = true;
                }

                if ((children.getLength() > 0) && !nodeTextOnly) {
                    out.print('\n');
                }

                for (int i = 0; i < children.getLength(); i++) {
                    walk(out, children.item(i), indent + 1, nodeTextOnly);
                }

                if ((children.getLength() > 0) && !nodeTextOnly) {
                    indent(out, indent);
                }
            }

            // close tag
            if (children.getLength() > 0) {
                out.print("</");
                out.print(node.getNodeName());
                out.print('>');
            }

            break;

        // text or cdata
        case Node.CDATA_SECTION_NODE:
        case Node.TEXT_NODE:
            escape(out, node.getNodeValue());

            break;

        // processing instruction
        case Node.PROCESSING_INSTRUCTION_NODE:
            out.print("<?");
            out.print(node.getNodeName());

            if ((node.getNodeValue() != null) && (node.getNodeValue().length() > 0)) {
                out.print(' ');
                out.print(node.getNodeValue());
            }

            out.print("?>");

            break;

        // entity reference ( &blah; )
        case Node.ENTITY_REFERENCE_NODE:
            out.print('&');
            out.print(node.getNodeName());
            out.print(';');

            break;
        }

        if (!keepFormatting) {
            out.print('\n');
        }
    }
}
