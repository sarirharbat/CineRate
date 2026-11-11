package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@Path("/users/{userId}/watchlist")
@Produces(MediaType.APPLICATION_JSON)
public class WatchlistResource {

    @Inject
    private ApplicationState state;

    @GET
    public List<UUID> getWatchlist(@PathParam("userId") UUID userId) {
        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        return user.getWatchlist();
    }

    @GET
    @Path("/watched")
    public List<UUID> getWatchedMovies(@PathParam("userId") UUID userId) {
        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        return user.getWatchedMovies();
    }

    @DELETE
    @Path("/{movieId}")
    public boolean removeFromWatchlist(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        user.removeFromWatchlist(movieId);
        return true;
    }

    @PUT
    @Path("/{movieId}/watched")
    public boolean markAsWatched(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        user.markAsWatched(movieId);
        return true;
    }

    @DELETE
    @Path("/{movieId}/watched")
    public boolean markAsUnwatched(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        if (!user.isInWatchlist(movieId)) {
            throw new IllegalStateException("Movie is not in the watchlist");
        }

        user.markAsUnwatched(movieId);
        return true;
    }

    @POST
    @Path("/{movieId}")
    public boolean addToWatchlist(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        if (state.getMovie(movieId) == null) {
            throw new NotFoundException("No movie with ID " + movieId);
        }

        user.addToWatchlist(movieId);
        return true;
    }

}