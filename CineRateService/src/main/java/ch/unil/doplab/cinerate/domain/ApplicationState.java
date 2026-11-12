package ch.unil.doplab.cinerate.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class ApplicationState {

    private Map<UUID, User> users;
    private Map<String, UUID> usernames;

    @PostConstruct
    public void init() {
        users = new TreeMap<>();
        usernames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        populateApplicationState();
    }

    public User addUser(User user) {
        // the ID of a new user is always created by the service
        return addUser(UUID.randomUUID(), user);
    }

    public User addUser(UUID uuid, User user) {
        var username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("User must have a username");
        }
        if (usernames.containsKey(username)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        checkNames(user);
        checkEmail(user.getEmail());
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("User must have a password of at least 6 characters");
        }
        checkPassword(user.getPassword());
        user.setPassword(Utils.hashPassword(user.getPassword()));
        user.setUUID(uuid);
        // a new user is always a member, never an administrator
        user.setRole(Utils.memberRole);
        // the registration date is always set by the service
        user.setRegistrationDate(LocalDate.now());
        users.put(uuid, user);
        usernames.put(username, uuid);
        return user;
    }

    public boolean setUser(UUID uuid, User user) {
        var theUser = users.get(uuid);
        if (theUser == null) {
            return false;
        }
        var username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("User must have a username");
        }
        if (!theUser.getUsername().equalsIgnoreCase(username) &&
                usernames.get(username) != null &&
                !usernames.get(username).equals(uuid)) {
            throw new IllegalArgumentException("A user named '" + username + "' already exists");
        }
        checkNames(user);
        checkEmail(user.getEmail());
        if (user.getPassword() != null) {
            checkPassword(user.getPassword());
            user.setPassword(Utils.hashPassword(user.getPassword()));
        }
        // the watchlist is changed with its own requests (person 4): it is kept
        user.setWatchlist(theUser.getWatchlist());
        user.setWatchedMovies(theUser.getWatchedMovies());
        usernames.remove(theUser.getUsername());
        usernames.put(username, uuid);
        theUser.replaceWith(user);
        return true;
    }

    public boolean removeUser(UUID uuid) {
        var user = users.get(uuid);
        if (user == null) {
            return false;
        }
        for (Review review : user.getReviews()) {
            Movie movie = getMovie(review.getMovieID());

            if (movie != null) {
                review.delete(user, movie);
            }
        }
        usernames.remove(user.getUsername());
        users.remove(uuid);
        return true;
        usernames.remove(user.getUsername());
        users.remove(uuid);
        return true;
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public Map<UUID, User> getAllUsers() {
        return new TreeMap<>(users);
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        var uuid = usernames.get(username);
        if (uuid == null) {
            return null;
        }
        var user = users.get(uuid);
        if (!user.checkPassword(password)) {
            return null;
        }
        return user;
    }

    private void checkNames(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("User must have a first name");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("User must have a last name");
        }
    }

    private void checkEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("User must have an email with an @");
        }
    }

    private void checkPassword(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("User must have a password of at least 6 characters");
        }
    }

    private void populateApplicationState() {
        addUser(UUID.fromString("1ead1a6d-7d30-4e2d-96ad-37f620949a9b"), new User("Alice", "Martin", "alice@cinerate.ch", "alice", "alice123"));
        addUser(UUID.fromString("f6a10b0a-c25f-4b6e-81b4-227a28fdda4e"), new User("Bob", "Dupont", "bob@cinerate.ch", "bob", "bob12345"));
        var admin = addUser(UUID.fromString("834bd15a-f9d6-4650-a5bf-e9bc69c89ccf"), new User("Admin", "CineRate", "admin@cinerate.ch", "admin", "admin123"));
        admin.setRole(Utils.adminRole);
    }
}
