package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.*;

// Elias Mounerat
public class Person {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String nationality;
    private Set<UUID> movies;

    public Person() {
        this(null, null, null, null);
    }

    public Person(String firstName, String lastName) {
        this(null, firstName, lastName, null, null);
    }

    public Person(String firstName, String lastName, LocalDate birthDate, String nationality) {
        this(null, firstName, lastName, birthDate, nationality);
    }

    public Person(UUID uuid, String firstName, String lastName, LocalDate birthDate, String nationality) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        checkBirthDate(birthDate);
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.movies = new HashSet<>();
    }

    private void checkBirthDate(LocalDate birthDate) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date must not be in the future");
        }
    }

    public void replaceWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.birthDate = person.birthDate;
        this.nationality = person.nationality;
        this.movies = new HashSet<>(person.movies);
    }

    public void mergeWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        if (person.firstName != null) {
            this.firstName = person.firstName;
        }
        if (person.lastName != null) {
            this.lastName = person.lastName;
        }
        if (person.birthDate != null) {
            this.birthDate = person.birthDate;
        }
        if (person.nationality != null) {
            this.nationality = person.nationality;
        }
        if (!person.movies.isEmpty()) {
            this.movies.addAll(person.movies);
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        checkBirthDate(birthDate);
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void addMovie(UUID movieID) {
        movies.add(movieID);
    }

    public void removeMovie(UUID movieID) {
        movies.remove(movieID);
    }

    public boolean hasMovie(UUID movieID) {
        return movies.contains(movieID);
    }

    public List<UUID> getMovies() {
        return new ArrayList<>(movies);
    }

    public void setMovies(List<UUID> movies) {
        this.movies = new HashSet<>(movies);
    }

    public String describe() {
        return "id=" + this.uuid + ", firstName='" + this.firstName + "', lastName='" + this.lastName + "'" +
                ", birthDate=" + this.birthDate + ", nationality='" + this.nationality + "'" +
                ", movies=" + this.movies;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(uuid, person.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

