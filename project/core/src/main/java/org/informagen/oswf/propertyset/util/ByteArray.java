package org.informagen.oswf.propertyset.util;

import java.io.Serializable;

/**
 * Wrapper class for a byte array primative
 *
 */
 
public final class ByteArray implements Serializable {

    private byte[] bytes;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public ByteArray() {}

    public ByteArray(byte[] bytes) {
        this.bytes = bytes;
    }

    //  M E T H O D S  ------------------------------------------------------------------------

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
