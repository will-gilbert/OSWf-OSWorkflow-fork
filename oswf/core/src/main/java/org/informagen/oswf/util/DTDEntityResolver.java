package org.informagen.oswf.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;


/**
 */
public class DTDEntityResolver implements EntityResolver {

    // M E T H O D S  -------------------------------------------------------------------------

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
 
        if (systemId == null) 
            return null;

        try {
            
            String filename = new URL(systemId).getFile();

            if ((filename != null) && (filename.indexOf('/') > -1)) 
                filename = filename.substring(filename.lastIndexOf('/') + 1);

            if (systemId.endsWith(".dtd")) {
                InputStream is = getClass().getResourceAsStream("/" + filename);
                                
                if (is == null)
                    is = getClass().getResourceAsStream("/META-INF/" + filename);
                    
                if (is != null) 
                    return new InputSource(is);
            }
            
        } catch (MalformedURLException malformedURLException) {
            throw new IOException(malformedURLException);
        }
        
        return null;
    }
}
