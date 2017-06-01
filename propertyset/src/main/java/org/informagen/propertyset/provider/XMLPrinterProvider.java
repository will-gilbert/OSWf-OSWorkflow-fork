package org.informagen.propertyset.provider;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.Writer;


/**
 * Provider for pretty printing XML DOM documents to a stream.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 5 $
 *
 * @see org.informagen.propertyset.util.XMLUtils
 */
public interface XMLPrinterProvider extends Provider {
    // M E T H O D S  -------------------------------------------------------------------------

    void print(Document doc, Writer out) throws IOException;
}
