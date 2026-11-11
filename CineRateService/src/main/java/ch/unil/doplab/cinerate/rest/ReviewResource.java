package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.Review;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ch.unil.doplab.cinerate.domain.User;

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
}
