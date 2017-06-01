package tests.typedmap;

import org.informagen.typedmap.verifiers.StringVerifier;
import org.informagen.typedmap.verifiers.VerifyException;

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

public class StringVerifierTest {


    @Test
    public void testVerifyStringContainsInValidString() {
        StringVerifier verifier = new StringVerifier();
        verifier.setContains("thing");

        try {
            verifier.verify("tryagain");
            fail("Should not be able to verify a string contains an invalid string.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringContainsValidString() {
        StringVerifier verifier = new StringVerifier();
        verifier.setContains("thing");
        assertEquals("thing", verifier.getContains());
        verifier.verify("somethingelse");
    }

    @Test
    public void testVerifyStringInAllowableValues() {
        String[] allowed = {"value1", "value2"};
        StringVerifier verifier = new StringVerifier(allowed);
        verifier.verify("value2");
    }

    @Test
    public void testVerifyStringLengthLongerThanMax() {
        StringVerifier verifier = new StringVerifier();
        verifier.setMaxLength(10);
        assertEquals(10, verifier.getMaxLength());

        try {
            verifier.verify("123456789012");
            fail("Should not be able to verify a string longer than the max.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringLengthShorterThanMin() {
        StringVerifier verifier = new StringVerifier();
        verifier.setMinLength(10);
        assertEquals(10, verifier.getMinLength());

        try {
            verifier.verify("value1");
            fail("Should not be able to verify a string shorter than the min.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringNotInAllowableValues() {
        String[] allowed = {"value1", "value2"};
        StringVerifier verifier = new StringVerifier();
        verifier.setAllowableValues(allowed);

        try {
            verifier.verify("not there");
            fail("Should not be able to verify a String that isn't in the allowed values.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringWithInvalidPrefix() {
        StringVerifier verifier = new StringVerifier();
        verifier.setPrefix("test");

        try {
            verifier.verify("username");
            fail("Should not be able to verify a string with an invalid prefix.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringWithInvalidSuffix() {
        StringVerifier verifier = new StringVerifier();
        verifier.setSuffix("Int");

        try {
            verifier.verify("testDouble");
            fail("Should not be able to verify string with an invalid suffix.");
        } catch (VerifyException e) {
            // success
        }
    }

    @Test
    public void testVerifyStringWithLengthBetweenMinAndMax() {
        StringVerifier verifier = new StringVerifier(0, 100);
        verifier.verify("this should work!");
    }

    @Test
    public void testVerifyStringWithValidPrefix() {
        StringVerifier verifier = new StringVerifier();
        verifier.setPrefix("test");
        assertEquals("test", verifier.getPrefix());
        verifier.verify("testInt");
    }

    @Test
    public void testVerifyStringWithValidSuffix() {
        StringVerifier verifier = new StringVerifier();
        verifier.setSuffix("Int");
        assertEquals("Int", verifier.getSuffix());
        verifier.verify("testInt");
    }
}
