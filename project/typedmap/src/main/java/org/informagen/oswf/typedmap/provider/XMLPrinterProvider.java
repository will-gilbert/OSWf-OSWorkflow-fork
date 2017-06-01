package org.informagen.oswf.typedmap.provider;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.Writer;


/**
 * Provider for pretty printing XML DOM documents to a stream.
 *
 *
 * @see org.informagen.oswf.typedmap.util.XMLUtils
 */
public interface XMLPrinterProvider extends Provider {
    // M E T H O D S  -------------------------------------------------------------------------

    void print(Document doc, Writer out) throws IOException;
}
