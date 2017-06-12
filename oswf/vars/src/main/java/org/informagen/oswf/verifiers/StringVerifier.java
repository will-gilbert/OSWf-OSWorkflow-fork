package org.informagen.oswf.verifiers;

import java.util.HashSet;
import java.util.Set;


/**
 * Handles verification of Strings.
 * Can be configured to only accept only strings within a given
 * length range. Omitted values are assumed to be unconstrained.
 * For example:<br><code>
 * StringVerifier sv = new StringVerifier();
 * sv.setMaxLength(50);
 * </code><br>
 * Will accept any string that is less than 50 characters in length.<p>
 * Note though that the default max length of a string is 255 chars.
 *
 */

public class StringVerifier implements ValueVerifier {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Set<String> allowableStrings = null;
    private String contains;
    private String prefix;
    private String suffix;
    private int max = 255;
    private int min = 0;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public StringVerifier() {}

    /**
     * Create a StringVerifier with the specified min and max lengths.
     * @param min The minimum allowable string length.
     * @param max The maximum allowable string length.
     */
    public StringVerifier(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public StringVerifier(String[] allowable) {
        setAllowableValues(allowable);
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public void setAllowableValues(String[] values) {
        
        allowableStrings = new HashSet<String>();

        //Store the array in a set, since all we'll be doing is lookups
        for(String value : values)
            allowableStrings.add(value);
    }

    public void setContains(String contains) {
        this.contains = contains;
    }

    public String getContains() {
        return contains;
    }

    public void setMaxLength(int max) {
        this.max = max;
    }

    public int getMaxLength() {
        return max;
    }

    public void setMinLength(int min) {
        this.min = min;
    }

    public int getMinLength() {
        return min;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void verify(Object object) throws VerifyException {
 
        String string = (String)object;

        if (string.length() < min) 
            throw new VerifyException("String " + string + " too short, min length=" + min);

        if (string.length() > max) 
            throw new VerifyException("String " + string + " too long, max length=" + max);

        if ((suffix != null) && !string.endsWith(suffix)) 
            throw new VerifyException("String " + string + " has invalid suffix (suffix must be '" + suffix + "')");

        if ((prefix != null) && !string.startsWith(prefix))
            throw new VerifyException("String " + string + " has invalid prefix (prefix must be '" + prefix + "')");

        if ((contains != null) && (string.indexOf(contains) == -1))
            throw new VerifyException("String '" + string + "' does not contain required string '" + contains + "'");

        if ((allowableStrings != null) &&!allowableStrings.contains(string))
            throw new VerifyException("String '" + string + "' not in allowed set for this property");

    }
}
