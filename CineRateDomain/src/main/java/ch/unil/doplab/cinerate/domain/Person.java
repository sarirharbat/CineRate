package ch.unil.doplab.cinerate.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Person {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String nationality;

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
        // TODO: vérifier que la date de naissance n'est pas dans le futur
        this.birthDate = birthDate;
        this.nationality = nationality;
    }

    public void replaceWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.birthDate = person.birthDate;
        this.nationality = person.nationality;
    }

    public void mergeWith(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        if (person.uuid != null) {
            this.uuid = person.uuid;
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
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String describe() {
        return "id=" + this.uuid + ", firstName='" + this.firstName + "', lastName='" + this.lastName + "'" +
                ", birthDate=" + this.birthDate + ", nationality='" + this.nationality + "'";
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.describe() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(uuid, person.uuid) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, firstName, lastName);
    }
}

