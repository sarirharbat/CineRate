package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.Director;
import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/directors")
public class DirectorResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Director> getAllDirectors() {
        return new ArrayList<>(state.getAllDirectors().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getDirector(@PathParam("id") String id) {
        var directorId = ApplicationState.parseUUID(id, "director");
        var director = state.getDirector(directorId);
        if (director == null) {
            return notFound(directorId);
        }
        return Response.ok(director).build();
    }

    private Response notFound(UUID directorId) {
        var description = new ExceptionDescription(NotFoundException.class.getName(),
                "No director with id " + directorId);
        return Response.status(Response.Status.NOT_FOUND).entity(description).build();
    }
}