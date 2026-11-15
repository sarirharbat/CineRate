package ch.unil.doplab.cinerate.rest;

import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.Test;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Status chosen by the mapper. The WebApplicationException cases (404 unknown route, ...)
 * need the JAX-RS implementation and are checked over HTTP with the Postman collection.
 */
class GlobalExceptionMapperTest {

    // Same method name as MessageBodyReader.readFrom, where Jersey fails on a bad JSON body.
    private static ProcessingException readFrom() {
        return new ProcessingException("Error deserializing object from entity stream.");
    }

    @Test
    void testInvalidDataIsBadRequest() {
        printMethodName();
        assertEquals(400, GlobalExceptionMapper.statusOf(new IllegalArgumentException("Movie must have a title")));
    }

    @Test
    void testUnreadableBodyIsBadRequest() {
        printMethodName();
        assertEquals(400, GlobalExceptionMapper.statusOf(readFrom()));
    }

    @Test
    void testUnreadableBodyMessageNamesTheProperty() {
        printMethodName();
        var exception = new ProcessingException("Error deserializing object from entity stream.",
                new IllegalStateException("Unable to deserialize property 'releaseYear' because of: "
                        + "Expected a whole number, got 2000.5"));
        assertEquals("Error deserializing object from entity stream. Unable to deserialize property "
                        + "'releaseYear' because of: Expected a whole number, got 2000.5",
                GlobalExceptionMapper.messageOf(exception, 400));
        // the other messages are kept as they are
        assertEquals("Movie must have a title",
                GlobalExceptionMapper.messageOf(new IllegalArgumentException("Movie must have a title"), 400));
        assertEquals("Error serializing object to output stream.", GlobalExceptionMapper.messageOf(
                new ProcessingException("Error serializing object to output stream.", new RuntimeException("x")), 500));
    }

    @Test
    void testOtherErrorsAreServerErrors() {
        printMethodName();
        assertEquals(500, GlobalExceptionMapper.statusOf(new ProcessingException("Error serializing object to output stream.")));
        assertEquals(400, GlobalExceptionMapper.statusOf(new IllegalStateException("Unexpected")));
    }
}
