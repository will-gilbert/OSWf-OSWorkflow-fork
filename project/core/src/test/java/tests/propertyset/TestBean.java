package tests.propertyset;

import java.io.Serializable;


public class TestBean implements Serializable {


    private long longValue;
    private int intValue;
    private String stringValue;

    // S E T T E R S   /   G E T T E R S  -----------------------------------------------------

    public long getLong() { return longValue; }
    public void setLong(long longValue) { this.longValue = longValue; }

    public int getInteger() { return intValue; }
    public void setInteger(int intValue) { this.intValue = intValue; }

    public String getString() { return stringValue; }
    public void setString(String stringValue) { this.stringValue = stringValue; }


}
