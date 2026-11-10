package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Director extends Person {

    public Director() {
        super();
    }

    public Director(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Director(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Director(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
        super(uuid, firstName, lastName, birthDate, nationality);
    }

    public boolean hasDirected(UUID movieID) {
        return hasMovie(movieID);
    }
}

