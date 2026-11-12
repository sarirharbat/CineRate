package ch.unil.doplab.cinerate.domain;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class ApplicationState {

    // Personne 3 : identifiants fixes du catalogue de demonstration.
    public static final UUID inceptionID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    public static final UUID shutterIslandID = UUID.fromString("10000000-0000-0000-0000-000000000002");
    public static final UUID nightmareID = UUID.fromString("10000000-0000-0000-0000-000000000003");
    public static final UUID dicaprioID = UUID.fromString("20000000-0000-0000-0000-000000000001");
    public static final UUID gordonLevittID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    public static final UUID ruffaloID = UUID.fromString("20000000-0000-0000-0000-000000000003");
    public static final UUID englundID = UUID.fromString("20000000-0000-0000-0000-000000000004");
    public static final UUID langenkampID = UUID.fromString("20000000-0000-0000-0000-000000000005");
    public static final UUID nolanID = UUID.fromString("30000000-0000-0000-0000-000000000001");
    public static final UUID scorseseID = UUID.fromString("30000000-0000-0000-0000-000000000002");
    public static final UUID cravenID = UUID.fromString("30000000-0000-0000-0000-000000000003");

    private static final Pattern uuidPattern =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private Map<UUID, User> users;
    private Map<String, UUID> usernames;

    // Personne 3 : catalogue indexe par UUID, et personnes de reference pour les liens
    // et la recherche par nom. Les memes objets Movie servent au CRUD, a la recherche
    // et aux avis.
    private Map<UUID, Movie> movies;
    private Map<UUID, Actor> actors;
    private Map<UUID, Director> directors;

    @PostConstruct
    public void init() {
        users = new TreeMap<>();
        usernames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        movies = new TreeMap<>();
        actors = new TreeMap<>();
        directors = new TreeMap<>();
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

    // =========================================================================
    // Personne 3 : catalogue des films (CRUD)
    // =========================================================================

    /** Renvoie le film correspondant, ou null s'il n'existe pas. */
    public Movie getMovie(UUID movieId) {
        return movieId == null ? null : movies.get(movieId);
    }

    public Map<UUID, Movie> getAllMovies() {
        return new TreeMap<>(movies);
    }

    public Actor getActor(UUID actorId) {
        return actorId == null ? null : actors.get(actorId);
    }

    public Director getDirector(UUID directorId) {
        return directorId == null ? null : directors.get(directorId);
    }

    public Movie addMovie(MovieData data) {
        // the ID of a new movie is always created by the service
        return addMovie(UUID.randomUUID(), data);
    }

    public Movie addMovie(UUID movieId, MovieData data) {
        var input = checkMovieData(data);
        if (movies.containsKey(movieId)) {
            throw new IllegalArgumentException("A movie with id '" + movieId + "' already exists");
        }
        var movie = new Movie();
        // l'UUID est attribue avant de lier le film au realisateur et aux acteurs
        movie.setUUID(movieId);
        applyFields(movie, input);
        updateDirector(movie, input.director());
        updateCast(movie, input.cast());
        movies.put(movieId, movie);
        return movie;
    }

    /**
     * PUT : remplace tous les champs modifiables du film stocke. Un champ facultatif
     * absent (ou null) est remis a sa valeur vide : pas de synopsis, pas de realisateur,
     * casting, genres ou mots-cles vides. Envoyer [] ou omettre la liste la vide.
     * L'instance stockee est conservee : meme UUID, memes avis, memes notes.
     *
     * @return le film mis a jour, ou null si aucun film ne porte cet UUID
     */
    public Movie setMovie(UUID movieId, MovieData data) {
        var movie = getMovie(movieId);
        if (movie == null) {
            return null;
        }
        // tout est valide et resolu avant de toucher au film ou aux associations
        var input = checkMovieData(data);
        applyFields(movie, input);
        updateDirector(movie, input.director());
        updateCast(movie, input.cast());
        return movie;
    }

    public boolean removeMovie(UUID movieId) {
        var movie = getMovie(movieId);
        if (movie == null) {
            return false;
        }
        // 1. raccord Personne 4, tant que le film et les utilisateurs sont accessibles
        cleanUpMovieReferences(movie);
        // 2. liens avec les acteurs et le realisateur (les deux cotes de la relation)
        for (UUID actorID : movie.getCast()) {
            var actor = actors.get(actorID);
            if (actor != null) {
                movie.removeActor(actor);
            }
        }
        var director = getDirector(movie.getDirectorID());
        if (director != null) {
            movie.removeDirector(director);
        }
        // 3. le film quitte le catalogue
        movies.remove(movieId);
        return true;
    }

    /**
     * Raccord avec la Personne 4 : nettoyage des avis et des watchlists quand un film
     * est supprime. C'est le seul point d'entree de ce nettoyage ; il peut etre remplace
     * par l'operation de la Personne 4 lors de l'integration. Il ne sert qu'a la
     * suppression d'un film.
     */
    void cleanUpMovieReferences(Movie movie) {
        var movieId = movie.getUUID();
        for (Review review : movie.getReviews()) {
            var author = review.getUserID() == null ? null : users.get(review.getUserID());
            if (author != null && author.hasReviewed(movieId)) {
                // retire l'avis du film et de l'utilisateur, et recalcule la note du film
                review.delete(author, movie);
            } else {
                // l'auteur n'existe plus : l'avis est seulement retire du film
                movie.removeReview(review);
            }
        }
        for (User user : users.values()) {
            // markAsWatched deplace le film de watchlist vers watchedMovies : on verifie les deux
            if (user.isInWatchlist(movieId)) {
                user.removeFromWatchlist(movieId);
            }
            user.markAsUnwatched(movieId);
        }
    }

    // Donnees du corps de requete, validees et resolues.
    private record MovieInput(String title, int releaseYear, int duration, String synopsis, String posterUrl,
                              List<Genre> genres, Director director, List<Actor> cast, List<String> keywords) {
    }

    private MovieInput checkMovieData(MovieData data) {
        if (data == null) {
            throw new IllegalArgumentException("Movie data must not be empty");
        }
        var title = data.getTitle() == null ? "" : data.getTitle().trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Movie must have a title");
        }
        if (data.getReleaseYear() == null || data.getReleaseYear() <= 0) {
            throw new IllegalArgumentException("Movie must have a positive release year");
        }
        if (data.getDuration() != null && data.getDuration() <= 0) {
            throw new IllegalArgumentException("The duration of a movie must be positive");
        }
        var genres = new ArrayList<Genre>();
        for (String value : listOrEmpty(data.getGenres())) {
            var genre = parseGenre(value);
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        Director director = null;
        if (!isBlank(data.getDirectorID())) {
            var directorId = parseUUID(data.getDirectorID(), "director");
            director = directors.get(directorId);
            if (director == null) {
                throw new IllegalArgumentException("Unknown director: " + directorId);
            }
        }
        var cast = new ArrayList<Actor>();
        for (String value : listOrEmpty(data.getCast())) {
            if (isBlank(value)) {
                throw new IllegalArgumentException("The cast must not contain an empty actor id");
            }
            var actorId = parseUUID(value, "actor");
            var actor = actors.get(actorId);
            if (actor == null) {
                throw new IllegalArgumentException("Unknown actor: " + actorId);
            }
            if (cast.contains(actor)) {
                throw new IllegalArgumentException(actor.getFullName() + " appears twice in the cast");
            }
            cast.add(actor);
        }
        var keywords = new ArrayList<String>();
        for (String value : listOrEmpty(data.getKeywords())) {
            if (isBlank(value)) {
                throw new IllegalArgumentException("Keywords must not be empty");
            }
            // meme normalisation qu'a la recherche : espaces retires ici, minuscules dans Movie
            keywords.add(value.trim());
        }
        var duration = data.getDuration() == null ? 0 : data.getDuration();
        return new MovieInput(title, data.getReleaseYear(), duration, blankToNull(data.getSynopsis()),
                blankToNull(data.getPosterUrl()), genres, director, cast, keywords);
    }

    private void applyFields(Movie movie, MovieInput input) {
        movie.setTitle(input.title());
        movie.setReleaseYear(input.releaseYear());
        movie.setDuration(input.duration());
        movie.setSynopsis(input.synopsis());
        movie.setPosterUrl(input.posterUrl());
        movie.setGenres(input.genres());
        movie.setKeywords(input.keywords());
    }

    private void updateDirector(Movie movie, Director newDirector) {
        var oldDirector = getDirector(movie.getDirectorID());
        if (newDirector == null) {
            if (oldDirector != null) {
                movie.removeDirector(oldDirector);
            } else {
                movie.setDirectorID(null);
                movie.setDirectorName(null);
            }
        } else if (!movie.isDirectedBy(newDirector.getUUID())) {
            // retire aussi le film de la filmographie de l'ancien realisateur
            movie.setDirector(newDirector, oldDirector);
        } else {
            // meme realisateur : le nom affiche reste derive de la personne
            movie.setDirectorName(newDirector.getFullName());
        }
    }

    private void updateCast(Movie movie, List<Actor> newCast) {
        var newIds = new ArrayList<UUID>();
        for (Actor actor : newCast) {
            newIds.add(actor.getUUID());
        }
        // retraits
        for (UUID actorId : movie.getCast()) {
            if (!newIds.contains(actorId)) {
                var actor = actors.get(actorId);
                if (actor != null) {
                    movie.removeActor(actor);
                } else {
                    var cast = movie.getCast();
                    cast.remove(actorId);
                    movie.setCast(cast);
                }
            }
        }
        // ajouts
        for (Actor actor : newCast) {
            if (!movie.hasActor(actor.getUUID())) {
                movie.addActor(actor);
            }
        }
    }

    // =========================================================================
    // Personne 3 : recherche avancee
    // Les criteres renseignes sont combines avec un ET logique. Un critere absent
    // ou vide est ignore. Sans critere actif, tout le catalogue est renvoye.
    // =========================================================================

    public List<Movie> searchMovies(String title, String actor, String director, String genre, String keyword) {
        var titleCriterion = clean(title) == null ? null : clean(title).toLowerCase(Locale.ROOT);
        var actorCriterion = clean(actor);
        var directorCriterion = clean(director);
        var genreCriterion = clean(genre) == null ? null : parseGenre(genre);
        var keywordCriterion = clean(keyword);

        // un nom inconnu donne un ensemble vide, donc aucun resultat
        var actorIds = actorCriterion == null ? null : findPeople(actors, actorCriterion);
        var directorIds = directorCriterion == null ? null : findPeople(directors, directorCriterion);

        var result = new ArrayList<Movie>();
        for (Movie movie : movies.values()) {
            if (titleCriterion != null
                    && !movie.getTitle().toLowerCase(Locale.ROOT).contains(titleCriterion)) {
                continue;
            }
            if (actorIds != null && actorIds.stream().noneMatch(movie::hasActor)) {
                continue;
            }
            if (directorIds != null && directorIds.stream().noneMatch(movie::isDirectedBy)) {
                continue;
            }
            if (genreCriterion != null && !movie.hasGenre(genreCriterion)) {
                continue;
            }
            if (keywordCriterion != null && !movie.hasKeyword(keywordCriterion)) {
                continue;
            }
            result.add(movie);
        }
        return result;
    }

    // Tous les UUID des personnes portant ce nom complet, sans distinction de casse.
    private <P extends Person> Set<UUID> findPeople(Map<UUID, P> people, String fullName) {
        var wanted = normalizeName(fullName);
        var ids = new HashSet<UUID>();
        for (P person : people.values()) {
            if (normalizeName(person.getFullName()).equalsIgnoreCase(wanted)) {
                ids.add(person.getUUID());
            }
        }
        return ids;
    }

    private static String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Accepte le libelle d'un genre (Genre.fromLabel, par exemple "Science Fiction")
     * ou le nom de la constante (par exemple "ScienceFiction"), sans distinction de casse.
     */
    public static Genre parseGenre(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Genres must not be empty");
        }
        var text = value.trim();
        try {
            return Genre.fromLabel(text);
        } catch (IllegalArgumentException e) {
            for (Genre genre : Genre.values()) {
                if (genre.name().equalsIgnoreCase(text)) {
                    return genre;
                }
            }
            throw new IllegalArgumentException("Unknown genre: '" + text + "'. Accepted values: " + acceptedGenres());
        }
    }

    public static String acceptedGenres() {
        var labels = new ArrayList<String>();
        for (Genre genre : Genre.values()) {
            labels.add(genre.getLabel());
        }
        return String.join(", ", labels);
    }

    /** Lit un UUID au format standard 8-4-4-4-12, sinon IllegalArgumentException (400). */
    public static UUID parseUUID(String value, String what) {
        if (value == null || !uuidPattern.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Invalid " + what + " id: '" + value + "'");
        }
        return UUID.fromString(value.trim());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String clean(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static List<String> listOrEmpty(List<String> values) {
        return values == null ? List.of() : values;
    }

    // =========================================================================
    // Donnees de demonstration
    // =========================================================================

    private void populateApplicationState() {
        addUser(UUID.fromString("1ead1a6d-7d30-4e2d-96ad-37f620949a9b"), new User("Alice", "Martin", "alice@cinerate.ch", "alice", "alice123"));
        addUser(UUID.fromString("f6a10b0a-c25f-4b6e-81b4-227a28fdda4e"), new User("Bob", "Dupont", "bob@cinerate.ch", "bob", "bob12345"));
        var admin = addUser(UUID.fromString("834bd15a-f9d6-4650-a5bf-e9bc69c89ccf"), new User("Admin", "CineRate", "admin@cinerate.ch", "admin", "admin123"));
        admin.setRole(Utils.adminRole);

        populateCatalogue();
    }

    // Catalogue deterministe (Personne 3). Aucun appel TMDB pour ce lot.
    // Il permet de verifier que l'acteur et le theme sont reellement combines :
    // DiCaprio joue dans Inception et Shutter Island, le mot-cle "dream" concerne
    // Inception et A Nightmare on Elm Street ; seul Inception reunit les deux.
    private void populateCatalogue() {
        addDirector(new Director(nolanID, "Christopher", "Nolan", null, "British"));
        addDirector(new Director(scorseseID, "Martin", "Scorsese", null, "American"));
        addDirector(new Director(cravenID, "Wes", "Craven", null, "American"));

        addActor(new Actor(dicaprioID, "Leonardo", "DiCaprio", null, "American"));
        addActor(new Actor(gordonLevittID, "Joseph", "Gordon-Levitt", null, "American"));
        addActor(new Actor(ruffaloID, "Mark", "Ruffalo", null, "American"));
        addActor(new Actor(englundID, "Robert", "Englund", null, "American"));
        addActor(new Actor(langenkampID, "Heather", "Langenkamp", null, "American"));

        addMovie(inceptionID, movieData("Inception", 2010, 148,
                "A thief who steals secrets from people's dreams is asked to plant an idea instead.",
                nolanID, List.of(dicaprioID, gordonLevittID),
                List.of("Science Fiction", "Action"), List.of("dream", "heist")));
        addMovie(shutterIslandID, movieData("Shutter Island", 2010, 138,
                "A U.S. Marshal investigates the disappearance of a patient from an island asylum.",
                scorseseID, List.of(dicaprioID, ruffaloID),
                List.of("Thriller", "Mystery"), List.of("investigation", "asylum")));
        addMovie(nightmareID, movieData("A Nightmare on Elm Street", 1984, 91,
                "Teenagers are hunted in their dreams by a killer who can hurt them in real life.",
                cravenID, List.of(englundID, langenkampID),
                List.of("Horror"), List.of("dream", "slasher")));
    }

    // Personnes de reference : pas de route REST dans ce lot, utilisees par la demo et les tests.
    void addActor(Actor actor) {
        actors.put(actor.getUUID(), actor);
    }

    void addDirector(Director director) {
        directors.put(director.getUUID(), director);
    }

    private static MovieData movieData(String title, int year, int duration, String synopsis, UUID director,
                                       List<UUID> cast, List<String> genres, List<String> keywords) {
        var data = new MovieData(title, year);
        data.setDuration(duration);
        data.setSynopsis(synopsis);
        data.setDirectorID(director.toString());
        var castIds = new ArrayList<String>();
        for (UUID id : cast) {
            castIds.add(id.toString());
        }
        data.setCast(castIds);
        data.setGenres(genres);
        data.setKeywords(keywords);
        return data;
    }
}
