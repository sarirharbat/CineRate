package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

// Mouhamed Sarira, Klinsmann Kodra, Samy Hajhouj
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

        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(description)
                .build();
    }

    static int statusOf(Exception exception) {
        if (exception instanceof IllegalArgumentException ||
                exception instanceof IllegalStateException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        if (exception instanceof WebApplicationException webException) {
            return webException.getResponse().getStatus();
        }
        if (exception instanceof ProcessingException && isBodyReadingError(exception)) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    static String messageOf(Exception exception, int status) {
        var message = exception.getMessage();
        var cause = exception.getCause();
        if (status == Response.Status.BAD_REQUEST.getStatusCode() && exception instanceof ProcessingException
                && cause != null && cause.getMessage() != null) {
            return message + " " + cause.getMessage();
        }
        return message;
    }

    private static boolean isBodyReadingError(Throwable exception) {
        for (StackTraceElement element : exception.getStackTrace()) {
            if ("readFrom".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;
    }
}
