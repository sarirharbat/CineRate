package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.Review;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ch.unil.doplab.cinerate.domain.User;
import ch.unil.doplab.cinerate.domain.Movie;
import ch.unil.doplab.cinerate.domain.Rating;

import java.util.List;
import java.util.UUID;

@Path("/users/{userId}/reviews")
@Produces(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @Inject
    private ApplicationState state;

    @GET
    public List<Review> getReviews(@PathParam("userId") UUID userId) {
        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        return user.getReviews();
    }

    @GET
    @Path("/{movieId}")
    public Review getReview(@PathParam("userId") UUID userId,
                            @PathParam("movieId") UUID movieId) {
        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        Review review = user.getReview(movieId);

        if (review == null) {
            throw new NotFoundException("This user has no review for this movie");
        }

        return review;
    }

    @POST
    @Path("/{movieId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Review addReview(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId,
            Review review) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        Movie movie = state.getMovie(movieId);

        if (movie == null) {
            throw new NotFoundException("No movie with ID " + movieId);
        }

        if (review == null) {
            throw new BadRequestException("Review must not be empty");
        }

        Review newReview = new Review(
                UUID.randomUUID(),
                Rating.fromValue(review.getRating()),
                review.getComment());

        newReview.post(user, movie);
        return newReview;
    }

    @PUT
    @Path("/{movieId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Review editReview(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId,
            Review changes) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        Movie movie = state.getMovie(movieId);

        if (movie == null) {
            throw new NotFoundException("No movie with ID " + movieId);
        }

        Review review = user.getReview(movieId);

        if (review == null) {
            throw new NotFoundException("This user has no review for this movie");
        }

        if (changes == null) {
            throw new BadRequestException("Review must not be empty");
        }

        review.edit(user, movie,
                Rating.fromValue(changes.getRating()),
                changes.getComment());

        return review;
    }

    @DELETE
    @Path("/{movieId}")
    public boolean deleteReview(
            @PathParam("userId") UUID userId,
            @PathParam("movieId") UUID movieId) {

        User user = state.getUser(userId);

        if (user == null) {
            throw new NotFoundException("No user with ID " + userId);
        }

        Movie movie = state.getMovie(movieId);

        if (movie == null) {
            throw new NotFoundException("No movie with ID " + movieId);
        }

        Review review = user.getReview(movieId);

        if (review == null) {
            throw new NotFoundException("This user has no review for this movie");
        }

        review.delete(user, movie);
        return true;
    }
}
