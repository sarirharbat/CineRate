package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import org.junit.jupiter.api.Test;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Personne 3 : lecture de l'identifiant de chemin. Un UUID mal forme leve une
 * IllegalArgumentException, que GlobalExceptionMapper transforme en 400.
 * Les codes HTTP complets sont verifies avec la collection Postman sur Payara.
 */
class MovieResourceTest {

    @Test
    void testParseMovieId() {
        printMethodName();
        assertEquals(ApplicationState.inceptionID, MovieResource.parseMovieId("10000000-0000-0000-0000-000000000001"));
    }

    @Test
    void testParseMovieIdFailure_Malformed() {
        printMethodName();
        var e = assertThrows(IllegalArgumentException.class, () -> MovieResource.parseMovieId("not-a-uuid"));
        assertTrue(e.getMessage().contains("Invalid movie id"));
        assertThrows(IllegalArgumentException.class, () -> MovieResource.parseMovieId(""));
    }
}
