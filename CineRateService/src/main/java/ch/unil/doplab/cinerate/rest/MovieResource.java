package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.ExceptionDescription;
import ch.unil.doplab.cinerate.domain.Movie;
import ch.unil.doplab.cinerate.domain.MovieData;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Klinsmann Kodra
@Path("/movies")
public class MovieResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> getAllMovies() {
        return new ArrayList<>(state.getAllMovies().values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public List<Movie> searchMovies(@QueryParam("title") String title,
                                    @QueryParam("actor") String actor,
                                    @QueryParam("director") String director,
                                    @QueryParam("genre") String genre,
                                    @QueryParam("keyword") String keyword) {
        return state.searchMovies(title, actor, director, genre, keyword);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getMovie(@PathParam("id") String id) {
        var movieId = parseMovieId(id);
        var movie = state.getMovie(movieId);
        if (movie == null) {
            return notFound(movieId);
        }
        return Response.ok(movie).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMovie(MovieData data) {
        var movie = state.addMovie(data);
        return Response.status(Response.Status.CREATED).entity(movie).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response setMovie(@PathParam("id") String id, MovieData data) {
        var movieId = parseMovieId(id);
        var movie = state.setMovie(movieId, data);
        if (movie == null) {
            return notFound(movieId);
        }
        return Response.ok(movie).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response removeMovie(@PathParam("id") String id) {
        var movieId = parseMovieId(id);
        if (!state.removeMovie(movieId)) {
            return notFound(movieId);
        }
        return Response.ok(true).build();
    }

    static UUID parseMovieId(String id) {
        return ApplicationState.parseUUID(id, "movie");
    }

    private Response notFound(UUID movieId) {
        var description = new ExceptionDescription(NotFoundException.class.getName(),
                "No movie with id " + movieId);
        return Response.status(Response.Status.NOT_FOUND).entity(description).build();
    }
}
