package tests.propertyset;

import java.io.Serializable;


public class TestObject implements Serializable {

    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 261939103282846342L;

    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    private long id;

    // C O N S T R U C T O R S  ---------------------------------------------------------------

    public TestObject(long id) {
        this.id = id;
    }

    // M E T H O D S  -------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        TestObject testObject = (TestObject) obj;

        return id == testObject.getId();
    }

    public int hashCode() {
        return (int) (id ^ id >>> 32);
    }
}
