package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mouhamed Sarira
@Path("/users")
public class UserResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return new ArrayList<>(state.getAllUsers().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUser(@PathParam("id") UUID id) {
        var user = state.getUser(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response setUser(@PathParam("id") UUID id, User user) {
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!state.setUser(id, user)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(true).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        state.addUser(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(User credentials) {
        if (credentials == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        var user = state.login(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(user).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response removeUser(@PathParam("id") UUID id) {
        if (!state.removeUser(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(true).build();
    }
}
