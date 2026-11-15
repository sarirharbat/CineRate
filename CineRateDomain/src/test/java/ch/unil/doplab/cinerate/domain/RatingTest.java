package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

// Elias Mounerat
class RatingTest {

    @Test
    void testGetValue() {
        printMethodName();
        assertEquals(0, Rating.NO_RATING.getValue());
        assertEquals(7, Rating.SEVEN.getValue());
        assertEquals(10, Rating.TEN.getValue());
    }

    @Test
    void testFromValue() {
        printMethodName();
        assertEquals(Rating.ONE, Rating.fromValue(1));
        assertEquals(Rating.TEN, Rating.fromValue(10));
    }

    @Test
    void testFromValueFailure_OutOfRange() {
        printMethodName();
        assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(11));
        assertThrows(IllegalArgumentException.class, () -> Rating.fromValue(-1));
    }

    @Test
    void testGetLabel() {
        printMethodName();
        assertEquals("No rating", Rating.NO_RATING.getLabel());
        assertEquals("★★★☆☆☆☆☆☆☆", Rating.THREE.getLabel());
    }
}
