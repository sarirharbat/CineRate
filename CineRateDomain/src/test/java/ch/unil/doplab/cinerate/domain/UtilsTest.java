package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

class UtilsTest {

    @Test
    void testHashPassword() {
        printMethodName();
        var hashed = Utils.hashPassword("secret");
        assertNotEquals("secret", hashed);
        assertTrue(Utils.checkPassword("secret", hashed));
        assertFalse(Utils.checkPassword("wrong", hashed));
    }

    @Test
    void testFormatMovieRating() {
        printMethodName();
        assertEquals("not rated yet", Utils.formatMovieRating(0));
        assertTrue(Utils.formatMovieRating(7.5).endsWith("/ 10"));
    }

    @Test
    void testBuildException() {
        printMethodName();
        var description = new ExceptionDescription("java.lang.IllegalArgumentException", "Bad value");
        var exception = Utils.buildException(description);
        assertInstanceOf(IllegalArgumentException.class, exception);
        assertEquals("Bad value", exception.getMessage());
    }
}
