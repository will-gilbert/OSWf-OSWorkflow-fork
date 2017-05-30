package org.informagen.oswf.propertyset.util;

import org.informagen.oswf.propertyset.util.Base64;


/**
 * Utilities for common String manipulations.
 */
public class TextUtils {

    /**
     * Convert a String to an boolean.
     * Accepts: 1/0, yes/no, true/false - case insensitive. If the value does
     * not map to "true,", <code>false</code> is returned.
     *
     * @param in String to be parsed.
     * @return boolean representation of String.
     */
    public final static boolean parseBoolean(String in) {
        in = noNull(in);

        if (in.length() == 0) {
            return false;
        }

        switch (in.charAt(0)) {
        case '1':
        case 'y':
        case 'Y':
        case 't':
        case 'T':
            return true;
        }

        return false;
    }

    public final static int parseInt(String in) {
        int i = 0;

        try {
            i = Integer.parseInt(in);
        } catch (Exception e) {
            i = (int) parseFloat(in);
        }

        ;

        return i;
    }
    /**
     * Convert a String to a long. Truncates numbers if it's a float or double string;
     * for example, 4.5 yields a value of 4.
     *
     * @param in String containing number to be parsed.
     * @return Long value of number or 0 if error.
     *
     */
    public final static long parseLong(String in) {
        long l = 0;

        try {
            l = Long.parseLong(in);
        } catch (Exception e) {
            l = (long) parseDouble(in);
        }

        ;

        return l;
    }


    /**
     * Convert a String to a double.
     *
     * @param in String containing number to be parsed.
     * @return Double value of number or 0 if error.
     *
     */
    public final static double parseDouble(String in) {
        double d = 0;

        try {
            d = Double.parseDouble(in);
        } catch (Exception e) {
        }

        ;

        return d;
    }
    public final static String noNull(String string, String defaultString) {
        return (stringSet(string)) ? string : defaultString;
    }

    public final static String noNull(String string) {
        return noNull(string, "");
    }
    
    public final static float parseFloat(String in) {
        float f = 0;

        try {
            f = Float.parseFloat(in);
        } catch (Exception e) {
        }

        ;

        return f;
    }

    public final static boolean stringSet(String string) {
        return (string != null) && !"".equals(string);
    }




    
}
