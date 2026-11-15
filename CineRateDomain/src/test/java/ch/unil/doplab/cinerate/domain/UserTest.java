package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

// Elias Mounerat
class UserTest {

    private User alice;
    private UUID inception;

    @BeforeEach
    void setUp() {
        alice = new User(UUID.randomUUID(), "Alice", "Martin", "alice@unil.ch", "alice", Utils.hashPassword("secret"));
        inception = UUID.randomUUID();
    }

    @Test
    void testGetFullName() {
        printMethodName();
        assertEquals("Alice Martin", alice.getFullName());
        assertEquals(Utils.memberRole, alice.getRole());
        assertFalse(alice.isAdmin());
    }

    @Test
    void testPasswordIsHashed() {
        printMethodName();
        assertNotEquals("secret", alice.getPassword());
        assertTrue(alice.checkPassword("secret"));
        assertFalse(alice.checkPassword("wrong"));
    }

    @Test
    void testAddToWatchlistSuccess() {
        printMethodName();
        alice.addToWatchlist(inception);
        assertTrue(alice.isInWatchlist(inception));
        assertEquals(1, alice.getWatchlist().size());
    }

    @Test
    void testAddToWatchlistFailure_AlreadyThere() {
        printMethodName();
        alice.addToWatchlist(inception);
        Exception e = assertThrows(IllegalStateException.class, () -> alice.addToWatchlist(inception));
        assertTrue(e.getMessage().contains("already in the watchlist"));
    }

    @Test
    void testRemoveFromWatchlistFailure_NotThere() {
        printMethodName();
        Exception e = assertThrows(IllegalStateException.class, () -> alice.removeFromWatchlist(inception));
        assertTrue(e.getMessage().contains("not in the watchlist"));
    }

    @Test
    void testMarkAsWatchedLeavesWatchlist() {
        printMethodName();
        alice.addToWatchlist(inception);
        alice.markAsWatched(inception);
        assertTrue(alice.hasWatched(inception));
        assertFalse(alice.isInWatchlist(inception));
    }

    @Test
    void testMarkAsWatchedFailure_NotInWatchlist() {
        printMethodName();
        assertThrows(IllegalStateException.class, () -> alice.markAsWatched(inception));
    }

    @Test
    void testReplaceWith() {
        printMethodName();
        var other = new User("Bob", "Durand", "bob@unil.ch", "bob", null);
        alice.replaceWith(other);
        assertEquals("Bob", alice.getFirstName());
        assertEquals("bob", alice.getUsername());
        assertNotNull(alice.getUUID());
        assertTrue(alice.checkPassword("secret"));
    }

    @Test
    void testMergeWith() {
        printMethodName();
        var patch = new User();
        patch.setEmail("new@unil.ch");
        alice.mergeWith(patch);
        assertEquals("new@unil.ch", alice.getEmail());
        assertEquals("Alice", alice.getFirstName());
    }

    @Test
    void testEquals() {
        printMethodName();
        var copy = new User(alice.getUUID(), "A", "M", "alice@unil.ch", "alice", null);
        assertEquals(alice, copy);
        assertNotEquals(alice, new User(UUID.randomUUID(), "Alice", "Martin", "alice@unil.ch", "alice", null));
    }
}

