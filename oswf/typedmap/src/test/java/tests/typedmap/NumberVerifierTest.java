
package tests.typedmap;

import org.informagen.oswf.verifiers.NumberVerifier;
import org.informagen.oswf.verifiers.VerifyException;

// JUnit 4.x testing
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class NumberVerifierTest {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected NumberVerifier verifier;

    @Before
    public void setUp() {
        verifier = new NumberVerifier();
        verifier.setMin(7);
        verifier.setMax(11);
        verifier.setType(Integer.class);
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    public void testVerifyNumberBetweenMaxAndMin() {
        assertEquals(7, verifier.getMin());
        assertEquals(11, verifier.getMax());
        verifier.verify(10);
    }

    @Test
    public void testVerifyNumberGreaterThanMax() {
        try {
            verifier.verify(15);
            fail("Should not be able to verify a number greater than the max.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyNumberLessThanMin() {
        try {
            verifier.verify(1);
            fail("Should not be able to verify a number less than the min.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyNumberWithDifferentTypes() {
        assertEquals(Integer.class, verifier.getType());

        try {
            verifier.verify(new Long(100000));
            fail("Should not be able to verify a type of number other than the type specified.");
        } catch (VerifyException e) {
            // success
        }
    }
}
