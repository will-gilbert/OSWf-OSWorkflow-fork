package org.informagen.oswf.propertyset.verifiers;


/**
 * Handles verification of numbers.
 * Can be configured to only accept specific numeric types (int, float, etc)
 * as well as a range for the specified number. All constraints are
 * optional. If not specified, then any number is accepted.
 *
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @version $Revision: 146 $
 */
public class NumberVerifier implements PropertyVerifier {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private Class type;
    private Number max;
    private Number min;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public NumberVerifier() {
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public void setMax(Number num) {
        //Should we check if(type!=null && num.getClass()==type) ? Also ensure min/max classes match?
        max = num;
    }

    public Number getMax() {
        return max;
    }

    public void setMin(Number num) {
        //Should we check if(type!=null && num.getClass()==type) ? Also ensure min/max classes match?
        min = num;
    }

    public Number getMin() {
        return min;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    public void verify(Object value) throws VerifyException {
        //Should we wrap up a ClassCastException here?
        Number num = (Number) value;

        if (num.getClass() != type) {
            throw new VerifyException("value is of type " + num.getClass() + " expected type is " + type);
        }

        //Hmm, should we convert everything to doubles (performance?) or deal with every possible
        //Number subclass that we support?
        if ((min != null) && (value != null) && (min.doubleValue() > num.doubleValue())) {
            throw new VerifyException("value " + num.doubleValue() + " < min limit " + min.doubleValue());
        }

        if ((max != null) && (value != null) && (max.doubleValue() < num.doubleValue())) {
            throw new VerifyException("value " + num.doubleValue() + " > max limit " + max.doubleValue());
        }

        //Fall through case, allow any Number object.
    }
}
