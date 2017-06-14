package tests.persistentvars

import org.informagen.oswf.verifiers.StringVerifier
import org.informagen.oswf.verifiers.VerifyException

// JUnit 4.x testing
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

class StringVerifierTest {


    @Test
    void verifyStringContainsInValidString() {
        StringVerifier verifier = new StringVerifier()
        verifier.setContains('thing')

        try {
            verifier.verify('tryagain')
            fail('Should not be able to verify a string contains an invalid string.')
        } catch (VerifyException verifyException) {
            assert verifyException.message == 'String \'tryagain\' does not contain required string \'thing\''
        }
    }

    @Test
    void verifyStringContainsValidString() {
        StringVerifier verifier = new StringVerifier()
        verifier.setContains('thing')
        assert 'thing' == verifier.getContains()
        verifier.verify('somethingelse')
    }

    @Test
    void verifyStringInAllowableValues() {
        String[] allowed = ['value1', 'value2']
        StringVerifier verifier = new StringVerifier(allowed)
        verifier.verify('value2')
    }

    @Test
    void verifyStringLengthLongerThanMax() {
        StringVerifier verifier = new StringVerifier()
        verifier.setMaxLength(10)
        assert 10 == verifier.getMaxLength()

        try {
            verifier.verify('123456789012')
            fail('Should not be able to verify a string longer than the max.')
        } catch (VerifyException verifyException) {
            assert verifyException.message.contains('String 123456789012 too long, max length=10')
        }
    }

    @Test
    void verifyStringLengthShorterThanMin() {
        StringVerifier verifier = new StringVerifier()
        verifier.setMinLength(10)
        assert 10 == verifier.getMinLength()

        try {
            verifier.verify('value1')
            fail('Should not be able to verify a string shorter than the min.')
        } catch (VerifyException verifyException) {
            assert verifyException.message== 'String value1 too short, min length=10'
        }
    }

    @Test
    void verifyStringNotInAllowableValues() {
        String[] allowed = ['value1', 'value2']
        StringVerifier verifier = new StringVerifier()
        verifier.setAllowableValues(allowed)

        try {
            verifier.verify('not there')
            fail('Should not be able to verify a String that isn\'t in the allowed values.')
        } catch (VerifyException verifyException) {
            assert verifyException.message == 'String \'not there\' not in allowed set for this property'
        }
    }

    @Test
    void verifyStringWithInvalidPrefix() {
        StringVerifier verifier = new StringVerifier()
        verifier.setPrefix('test')

        try {
            verifier.verify('username')
            fail('Should not be able to verify a string with an invalid prefix.')
        } catch (VerifyException verifyException) {
            assert verifyException.message == 'String username has invalid prefix (prefix must be \'test\')'
        }
    }

    @Test
    void verifyStringWithInvalidSuffix() {
        StringVerifier verifier = new StringVerifier()
        verifier.setSuffix('Int')

        try {
            verifier.verify('testDouble')
            fail('Should not be able to verify string with an invalid suffix.')
        } catch (VerifyException verifyException) {
            assert verifyException.message == 'String testDouble has invalid suffix (suffix must be \'Int\')'
        }
    }

    @Test
    void verifyStringWithLengthBetweenMinAndMax() {
        StringVerifier verifier = new StringVerifier(0, 100)
        verifier.verify('this should work!')
    }

    @Test
    void verifyStringWithValidPrefix() {
        StringVerifier verifier = new StringVerifier()
        verifier.setPrefix('test')
        assert 'test' ==  verifier.getPrefix()
        verifier.verify('testInt')
    }

    @Test
    void verifyStringWithValidSuffix() {
        StringVerifier verifier = new StringVerifier()
        verifier.setSuffix('Int')
        assert 'Int' == verifier.getSuffix()
        verifier.verify('testInt')
    }
}
