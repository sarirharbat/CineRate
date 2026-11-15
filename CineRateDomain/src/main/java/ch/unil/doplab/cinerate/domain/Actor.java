package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.UUID;

// Elias Mounerat
public class Actor extends Person {

    public Actor() {
        super();
    }

    public Actor(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Actor(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Actor(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
        super(uuid, firstName, lastName, birthDate, nationality);
    }

    public boolean playsIn(UUID movieID) {
        return hasMovie(movieID);
    }
}

