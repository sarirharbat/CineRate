package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

class PersonTest {

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person(UUID.randomUUID(), "Sofia", "Coppola", LocalDate.of(1971, 5, 14), "American");
    }

    @Test
    void testGetFullName() {
        printMethodName();
        assertEquals("Sofia Coppola", person.getFullName());
    }

    @Test
    void testReplaceWith() {
        printMethodName();
        var other = new Person("Greta", "Gerwig");
        var uuid = person.getUUID();
        person.replaceWith(other);
        assertEquals("Greta", person.getFirstName());
        assertEquals("Gerwig", person.getLastName());
        assertNull(person.getBirthDate());
        assertEquals(uuid, person.getUUID());
    }

    @Test
    void testReplaceWithFailure_Null() {
        printMethodName();
        assertThrows(IllegalArgumentException.class, () -> person.replaceWith(null));
    }

    @Test
    void testMergeWith() {
        printMethodName();
        var patch = new Person();
        patch.setNationality("Italian");
        person.mergeWith(patch);
        assertEquals("Italian", person.getNationality());
        assertEquals("Sofia", person.getFirstName());
    }

    @Test
    void testMergeWithKeepsUUID() {
        printMethodName();
        var uuid = person.getUUID();
        var patch = new Person(UUID.randomUUID(), "Greta", null, null, null);
        person.mergeWith(patch);
        assertEquals(uuid, person.getUUID());
        assertEquals("Greta", person.getFirstName());
    }

    @Test
    void testBirthDateInFutureFails() {
        printMethodName();
        var future = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new Person("Greta", "Gerwig", future, null));
        assertThrows(IllegalArgumentException.class, () -> person.setBirthDate(future));
    }

    @Test
    void testEquals() {
        printMethodName();
        var copy = new Person(person.getUUID(), "Sofia", "Coppola", null, null);
        assertEquals(person, copy);
        assertNotEquals(person, new Person(UUID.randomUUID(), "Sofia", "Coppola", null, null));
    }

    @Test
    void testToString() {
        printMethodName();
        assertTrue(person.toString().startsWith("Person{"));
        assertTrue(person.toString().contains("Coppola"));
    }
}
