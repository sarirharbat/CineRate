package ch.unil.doplab.cinerate.domain;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

// Klinsmann Kodra
class MovieDataJsonTest {

    private static Jsonb jsonb;

    @BeforeAll
    static void createJsonb() {
        jsonb = JsonbBuilder.create();
    }

    @AfterAll
    static void closeJsonb() throws Exception {
        jsonb.close();
    }

    private static MovieData read(String json) {
        return jsonb.fromJson(json, MovieData.class);
    }

    @Test
    void testReadsWritableFieldsAndIgnoresComputedOnes() {
        printMethodName();
        var data = read("""
                {"UUID": "10000000-0000-0000-0000-000000000001", "title": "Memento", "releaseYear": 2000,
                 "duration": 113, "genres": ["Thriller"], "directorID": "30000000-0000-0000-0000-000000000001",
                 "directorName": "Someone else", "ratingAverage": 9.5, "cast": [], "keywords": ["memory"]}
                """);
        assertEquals("Memento", data.getTitle());
        assertEquals(2000, data.getReleaseYear());
        assertEquals(113, data.getDuration());
        assertEquals(List.of("Thriller"), data.getGenres());
        assertEquals("30000000-0000-0000-0000-000000000001", data.getDirectorID());
        assertEquals(List.of("memory"), data.getKeywords());
    }

    @Test
    void testMissingOrNullNumbersStayNull() {
        printMethodName();
        var data = read("{\"title\": \"Memento\", \"duration\": null}");
        assertNull(data.getReleaseYear());
        assertNull(data.getDuration());
    }

    @Test
    void testWholeNumberWrittenWithDecimalsIsAccepted() {
        printMethodName();
        assertEquals(2000, read("{\"releaseYear\": 2000.0}").getReleaseYear());
    }

    @Test
    void testDecimalYearIsRefused() {
        printMethodName();
        assertThrows(JsonbException.class, () -> read("{\"title\": \"X\", \"releaseYear\": 2000.5}"));
        assertThrows(JsonbException.class, () -> read("{\"title\": \"X\", \"releaseYear\": 2000, \"duration\": 90.5}"));
    }

    @Test
    void testTooLargeNumberIsRefused() {
        printMethodName();
        assertThrows(JsonbException.class, () -> read("{\"title\": \"X\", \"releaseYear\": 99999999999}"));
        assertThrows(JsonbException.class, () -> read("{\"title\": \"X\", \"releaseYear\": 2000, \"duration\": 1e12}"));
    }

    @Test
    void testTextYearIsRefused() {
        printMethodName();
        assertThrows(JsonbException.class, () -> read("{\"title\": \"X\", \"releaseYear\": \"twenty ten\"}"));
    }
}
