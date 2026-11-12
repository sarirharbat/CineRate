package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger log = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        int status = statusOf(exception);
        ExceptionDescription description = new ExceptionDescription(
                exception.getClass().getName(),
                messageOf(exception, status)
        );

        if (status >= 500) {
            log.severe("Exception caught: " + exception.getMessage());
        } else {
            log.warning("Request refused (" + status + "): " + exception.getMessage());
        }

        // the type is set explicitly: for an unknown route there is no @Produces to choose it,
        // and without it Jersey cannot write the description (it would answer 500)
        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(description)
                .build();
    }

    static int statusOf(Exception exception) {
        if (exception instanceof IllegalArgumentException) {
            // invalid data sent by the client
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        if (exception instanceof WebApplicationException webException) {
            // errors produced by JAX-RS keep their own status:
            // 404 unknown route, 405 wrong method, 415 wrong content type, ...
            return webException.getResponse().getStatus();
        }
        if (exception instanceof ProcessingException && isBodyReadingError(exception)) {
            // the JSON body cannot be read (bad syntax or a value of the wrong type)
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    // For a body that cannot be read, Jersey only says "Error deserializing object from entity
    // stream."; its cause (JSON-B) says which property is wrong, for example
    // "Unable to deserialize property 'releaseYear' because of: Expected a whole number, got 2000.5".
    static String messageOf(Exception exception, int status) {
        var message = exception.getMessage();
        var cause = exception.getCause();
        if (status == Response.Status.BAD_REQUEST.getStatusCode() && exception instanceof ProcessingException
                && cause != null && cause.getMessage() != null) {
            return message + " " + cause.getMessage();
        }
        return message;
    }

    // True when the exception was thrown while the body of the request was being read
    // (MessageBodyReader.readFrom). An error while writing a response stays a 500.
    private static boolean isBodyReadingError(Throwable exception) {
        for (StackTraceElement element : exception.getStackTrace()) {
            if ("readFrom".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;
    }
}
