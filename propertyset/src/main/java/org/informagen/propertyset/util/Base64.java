package org.informagen.propertyset.util;

// Java - I/O
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;


public class Base64 {

    // Singleton pattern instance
    private static Base64 instance = null;

    // Mapping table from 6-bit nibbles to Base64 characters.
    private final char[] encodeMap = new char[64];
    private final byte[] decodeMap = new byte[128];

    private Base64() {
        
          int i=0;
          for (char c='A'; c<='Z'; c++) encodeMap[i++] = c;
          for (char c='a'; c<='z'; c++) encodeMap[i++] = c;
          for (char c='0'; c<='9'; c++) encodeMap[i++] = c;
          
          encodeMap[i++] = '+'; 
          encodeMap[i++] = '/'; 

          for (int j=0; j<decodeMap.length; j++) 
              decodeMap[j] = -1;
              
          for (int k=0; k<encodeMap.length; k++) 
              decodeMap[encodeMap[k]] =(byte)k; 
    }

    public static Base64 getInstance() {
        if(instance == null) 
            instance = new Base64();
        
        return instance;
    }

   // E N C O D E  ============================================================================

    /**
     * Encode an Object to String by serializing it and encoding using base64.
     *
     */
    public final String encodeObject(Object object) throws IOException {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(baos);
        stream.writeObject(object);
        stream.close();
        baos.flush();

        return encodeAsString(baos.toByteArray());
    }

    public String encodeAsString(byte[] byteArray) {
        return new String(encode(byteArray));
    }

    public char[] encode(byte[] byteArray) {

        int iLen = byteArray.length; 
        int oDataLen = (iLen*4+2)/3;       // output length without padding
        int oLen = ((iLen+2)/3)*4;         // output length including padding

        char[] out = new char[oLen];
        int ip = 0;
        int iEnd = 0 + iLen;
        int op = 0;

        while (ip < iEnd) {

            int i0 = byteArray[ip++] & 0xff;
            int i1 = ip < iEnd ? byteArray[ip++] & 0xff : 0;
            int i2 = ip < iEnd ? byteArray[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;

            out[op++] = encodeMap[o0];
            out[op++] = encodeMap[o1];
            
            out[op] = op < oDataLen ? encodeMap[o2] : '='; op++;
            out[op] = op < oDataLen ? encodeMap[o3] : '='; op++; 
        }

        return out; 
    }




   // D E C O D E  ============================================================================


    /**
     * Decode Object from a String by decoding with base64 then deserializing.
     *
     */

    public final Object decodeObject(String string) throws IOException, ClassNotFoundException {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(decode(string));
        ObjectInputStream stream = new ObjectInputStream(bais);
        Object object = stream.readObject();
        stream.close();

        return object;
    }


    public byte[] decode(String string) {

        char[] in = string.toCharArray();
        int iLen = in.length;

        if (iLen%4 != 0) 
            throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4");

        while (iLen > 0 && in[0+iLen-1] == '=') 
            iLen--;

        int oLen = (iLen*3) / 4;

        byte[] out = new byte[oLen];
        int ip = 0;
        int iEnd = 0 + iLen;
        int op = 0;

        while (ip < iEnd) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iEnd ? in[ip++] : 'A';
            int i3 = ip < iEnd ? in[ip++] : 'A';

            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data");

            int b0 = decodeMap[i0];
            int b1 = decodeMap[i1];
            int b2 = decodeMap[i2];
            int b3 = decodeMap[i3];

            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data");

            int o0 = ( b0       <<2) | (b1>>>4);
            int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            int o2 = ((b2 &   3)<<6) |  b3;
            
            out[op++] = (byte)o0;
            
            if (op<oLen) 
                out[op++] = (byte)o1;

            if (op<oLen) 
                out[op++] = (byte)o2; 
        }

        return out; 
    }


} 


