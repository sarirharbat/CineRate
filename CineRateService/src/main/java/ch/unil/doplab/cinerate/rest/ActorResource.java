package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.Actor;
import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/actors")
public class ActorResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Actor> getAllActors() {
        return new ArrayList<>(state.getAllActors().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getActor(@PathParam("id") String id) {
        var actorId = ApplicationState.parseUUID(id, "actor");
        var actor = state.getActor(actorId);
        if (actor == null) {
            return notFound(actorId);
        }
        return Response.ok(actor).build();
    }

    private Response notFound(UUID actorId) {
        var description = new ExceptionDescription(NotFoundException.class.getName(),
                "No actor with id " + actorId);
        return Response.status(Response.Status.NOT_FOUND).entity(description).build();
    }
}