
package tests.persistentvars

import org.informagen.oswf.verifiers.NumberVerifier
import org.informagen.oswf.verifiers.VerifyException

// JUnit 4.x testing
import org.junit.Before
import org.junit.After
import org.junit.Test

import static org.junit.Assert.fail

class NumberVerifierTest {
    //  I N S T A N C E   F I E L D S  --------------------------------------------------------

    protected NumberVerifier verifier

    @Before
    void setUp() {
        verifier = new NumberVerifier()
        verifier.min = 7
        verifier.max = 11
        verifier.type = Integer.class
    }

    // M E T H O D S  -------------------------------------------------------------------------

    @Test
    void verifyNumberBetweenMaxAndMin() {
        assert 7 == verifier.min
        assert 11 == verifier.max
        verifier.verify(10)
    }

    @Test
    void verifyNumberGreaterThanMax() {
        try {
            verifier.verify(15)
            fail("Should not be able to verify a number greater than the max.")
        } catch (VerifyException verifyException) {
            verifyException.message.contains('value 15.0 > max limit 11.0')
        }
    }

    @Test
    void verifyNumberLessThanMin() {
        try {
            verifier.verify(1)
            fail("Should not be able to verify a number less than the min.")
        } catch (VerifyException verifyException) {
            verifyException.message.contains('value 1.0 < min limit 7.0')
        }
    }

    @Test
    void verifyNumberWithDifferentTypes() {
        assertEquals(Integer.class, verifier.getType())

        try {
            verifier.verify(new Long(100000))
            fail("Should not be able to verify a type of number other than the type specified.")
        } catch (VerifyException verifyException) {
            verifyException.message.contains('value is of type class java.lang.Long expected type is class java.lang.Integer')
        }
    }
}
