package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

class ReviewTest {

    private User alice;
    private User bob;
    private Movie inception;
    private Review review;

    @BeforeEach
    void setUp() {
        alice = new User(UUID.randomUUID(), "Alice", "Martin", "alice@unil.ch", "alice", "pw");
        bob = new User(UUID.randomUUID(), "Bob", "Durand", "bob@unil.ch", "bob", "pw");
        inception = new Movie(UUID.randomUUID(), "Inception", 2010);
        review = new Review(UUID.randomUUID(), Rating.EIGHT, "Great movie");
    }

    @Test
    void testPostSuccess() {
        printMethodName();
        review.post(alice, inception);
        assertTrue(review.isPosted());
        assertTrue(alice.hasReviewed(inception.getUUID()));
        assertTrue(inception.hasReviewFrom(alice.getUUID()));
        assertEquals("Inception", review.getMovieTitle());
        assertEquals(8.0, inception.getRatingAverage());
    }

    @Test
    void testPostFailure_AlreadyReviewed() {
        printMethodName();
        review.post(alice, inception);
        var second = new Review(Rating.TWO, "Changed my mind");
        Exception e = assertThrows(IllegalStateException.class, () -> second.post(alice, inception));
        assertTrue(e.getMessage().contains("already reviewed"));
        assertEquals(1, inception.getRatingCount());
    }

    @Test
    void testPostFailure_NoRating() {
        printMethodName();
        var empty = new Review(Rating.NO_RATING, "No stars");
        Exception e = assertThrows(IllegalStateException.class, () -> empty.post(alice, inception));
        assertTrue(e.getMessage().contains("between 1 and 10"));
    }

    @Test
    void testPostFailure_CommentTooLong() {
        printMethodName();
        var longReview = new Review(Rating.FIVE, "a".repeat(Review.maxCommentLength + 1));
        Exception e = assertThrows(IllegalStateException.class, () -> longReview.post(alice, inception));
        assertTrue(e.getMessage().contains("longer than"));
    }

    @Test
    void testAverageWithTwoUsers() {
        printMethodName();
        review.post(alice, inception);
        new Review(Rating.TEN, null).post(bob, inception);
        assertEquals(9.0, inception.getRatingAverage());
        assertEquals(2, inception.getReviews().size());
    }

    @Test
    void testEditUpdatesAverage() {
        printMethodName();
        review.post(alice, inception);
        review.edit(alice, inception, Rating.FOUR, "Not so good");
        assertEquals(4.0, inception.getRatingAverage());
        assertEquals("Not so good", inception.getReview(alice.getUUID()).getComment());
    }

    @Test
    void testEditFailure_WrongUser() {
        printMethodName();
        review.post(alice, inception);
        Exception e = assertThrows(IllegalStateException.class, () -> review.edit(bob, inception, Rating.ONE, null));
        assertTrue(e.getMessage().contains("not written by"));
    }

    @Test
    void testDeleteSuccess() {
        printMethodName();
        review.post(alice, inception);
        review.delete(alice, inception);
        assertFalse(alice.hasReviewed(inception.getUUID()));
        assertFalse(inception.hasReviewFrom(alice.getUUID()));
        assertEquals(0, inception.getRatingCount());
    }

    @Test
    void testDeleteFailure_NotPosted() {
        printMethodName();
        assertThrows(IllegalStateException.class, () -> review.delete(alice, inception));
    }

    @Test
    void testRatingFromValue() {
        printMethodName();
        assertEquals(Rating.SEVEN, Rating.fromValue(7));
        assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(11));
    }
}

