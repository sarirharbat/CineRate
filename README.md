# CineRate

A movie rating website. Users can rate movies from 1 to 10, write a review, keep a watchlist
and see the average rating of each movie.

This is the project of our group for the Software Architectures course (UNIL).
Phase 1 is the domain model and the web service. Phase 2 is the web app.

## Projects

- `CineRateDomain`: the model classes (`User`, `Movie`, `Review`, `Person`, `Actor`, `Director`,
  `Genre`, `Rating`, `Utils`) and their unit tests.
  - `User`: an account (username, names, email, hashed password, role, reviews, watchlist)
  - `Movie`: a movie with its director, cast, genres, keywords and reviews
  - `Review`: a rating from 1 to 10 and a comment, by one user about one movie
  - `Person`, `Actor`, `Director`: the people of a movie
  - `Genre`, `Rating`: the accepted genres and ratings
  - `Utils`: the password hashing and the roles
- `CineRateService`: the REST web service. The data is kept in memory in `ApplicationState`.
- `CineRateWebApp`: the website (JSF and PrimeFaces 14). It calls the web service over HTTP
  with the class `CineRateService` and uses the model classes of `CineRateDomain`.

## Who did what

- Elias Mounerat: the domain model and its tests.
- Mouhamed Sarira: the web service, the users CRUD, the README and the Postman collection.
- Klisman Kodra: the movies CRUD and the advanced search in the service (with a demo catalogue).
- Samy Hajhouj: the reviews and the watchlists in the service.

Phase 2:

- Mouhamed Sarira: the web app project (`CineRateWebApp`), the login, register and profile pages,
  and the calls for the movies, the reviews and the watchlist in `CineRateService`.
- Elias Mounerat: the actors, directors and genres in the service, the login filter, the design
  and the home page.
- Klisman Kodra: the catalogue and movie detail pages, the search of the home page, and the
  real movies shown on the home page (`CatalogueBean`, `MovieDetailBean`, `HomeBean`,
  `MovieDisplay`).

## How to run it

1. Open `CineRateDomain` in IntelliJ. In the Maven tab, run `clean` then `install`.
   Do it again after every change in the domain.
2. Start Payara: in the `StudyBuddyDocker` folder, run `docker compose up -d`.
3. Open `CineRateService` in IntelliJ. In the Maven tab, run `clean` then `install`.
4. Add a run configuration: `Run > Edit Configurations > + > Payara Server > Remote`.
   - Server: user `admin`, password `admin`
   - Deployment: `CineRateService:war`
   - Startup/Connection: Debug, Socket, port `9009`
5. Click the green arrow to deploy.
6. In Postman, import `CineRateService/CineRate.postman_collection.json` and run the collection.

The API is here: http://localhost:8080/CineRateService-1.0-SNAPSHOT/api

The website is here: http://localhost:8080/CineRateService-1.0-SNAPSHOT/

## How to run the web app

1. Run the web service first (steps above).
2. Open `CineRateWebApp` in IntelliJ. In the Maven tab, run `clean` then `install`.
3. Add a run configuration like for the service, with Deployment `CineRateWebApp:war`.
4. Click the green arrow to deploy.

The web app is here: http://localhost:8080/CineRateWebApp-1.0-SNAPSHOT/ (it opens on `Home.xhtml`)

## Web app pages

- `Home.xhtml`: the home page, open to everyone, with links to log in and to register.
  It shows the best-rated and the most reviewed movies of the service (`HomeBean`). The search
  bar opens `Catalogue.xhtml?q=...` with the text typed.
- `Login.xhtml`: log in with a username and a password (`POST /users/login`). A wrong username
  or password shows "Invalid login".
- `Register.xhtml`: create an account (`POST /users`). If the data is not valid, the page shows
  the message of the service.
- `UserProfile.xhtml`: the page of the logged-in user.
  - Change the first name, the last name, the email and the username (`Update`, `Undo`).
  - Change the password. The current password is checked first.
  - Delete the account.
  - `Logout` in the menu.
- `Catalogue.xhtml`: all the movies (`GET /movies`), shown as posters.
  - One search field for the title, an actor, a director, a genre or a theme. The results change
    while typing. Several clues can be separated with a comma (`dicaprio, dream`).
  - Genre buttons, a director list and an actor list (`searchMovies` of `CineRateService`).
  - Sort by rating, by year or by title.
  - When no filter is used, the best-rated movie is shown at the top.
- `MovieDetail.xhtml?id=...`: one movie (`GET /movies/{id}`).
  - Poster, year, length, genres, average rating, synopsis and director.
  - Add to or remove from the watchlist (`POST` / `DELETE /users/{id}/watchlist/{movieId}`).
  - Cast and crew, themes and reviews. A click on a name, a genre or a theme searches the
    catalogue for it.
  - "More like this": movies that share a genre.

`MovieDisplay` formats what these pages show (rating, length, genres, colour of the poster when
the movie has no image). Its tests are in `MovieDisplayTest`.

Components used: `p:panel`, `p:outputLabel`, `p:inputText`, `p:password`, `p:commandButton`,
`p:message`, `p:growl`, `p:dialog`, `p:commandLink`, `p:ajax` and `p:outputPanel`.

Without login, only `Home.xhtml`, `Login.xhtml` and `Register.xhtml` can be opened. The other
pages send back to `Login.xhtml` (`AuthenticationFilter`).

The class `CineRateService` of the web app sends a `User` to the service as it is, in JSON. When the
profile is updated, the password is set to `null`, so the service keeps the current password.

## Website (first page)

A first page of the website, ahead of Phase 2: the movie catalogue (`catalogue.xhtml`), built with
JSF and PrimeFaces 14. Its controller is `web/CatalogueBean`, which calls `ApplicationState`
directly (the page runs inside the same WAR as the REST service).

- Filters: title, actor, director, theme, genre and year. The filled filters are combined
  (logical AND), with the same search as `GET /movies/search`; the year is filtered in the bean.
- Table of the results, sortable by title, year and average rating, with pagination. On a narrow
  screen each movie is shown as a block of label/value lines.
- A click on a title opens the details of the movie (synopsis, director, cast, themes, rating).

Login, reviews and the watchlist are not on the page yet. The browser keeps the CSS of the page
for 7 days: after changing `resources/css/cinerate.css`, reload the page without the cache
(Ctrl+Shift+R, or Cmd+Shift+R on a Mac).

## Requests

- `GET /users`: all the users
- `GET /users/{id}`: one user
- `POST /users`: add a user
- `PUT /users/{id}`: change a user (the ID and the role stay the same)
- `DELETE /users/{id}`: delete a user
- `POST /users/login`: log in with a username and a password

Movies (details in the [Movies](#movies) section):

- `GET /movies`: all the movies (`200`)
- `GET /movies/{id}`: one movie (`200`, `404` if it does not exist)
- `POST /movies`: add a movie (`201`, the ID is created by the service)
- `PUT /movies/{id}`: change a movie (`200`, same ID, reviews and ratings are kept)
- `DELETE /movies/{id}`: delete a movie, its reviews and its watchlist entries (`200`)
- `GET /movies/search`: find movies with `title`, `actor`, `director`, `genre` and `keyword` (`200`)

Reviews:

- `GET /users/{userId}/reviews`: all the reviews of a user
- `GET /users/{userId}/reviews/{movieId}`: the review of a user about a movie
- `POST /users/{userId}/reviews/{movieId}`: write a review (`201`)
- `PUT /users/{userId}/reviews/{movieId}`: change the rating and the comment
- `DELETE /users/{userId}/reviews/{movieId}`: delete the review

The body is `{"rating": 8, "comment": "Great movie"}`: a rating from 1 to 10 and a comment
of at most 1000 characters. A user can write only one review per movie (a second one gives `400`).
The average rating of the movie is updated after each change.

Watchlist:

- `GET /users/{userId}/watchlist`: the IDs of the movies to watch
- `POST /users/{userId}/watchlist/{movieId}`: add a movie to the watchlist
- `DELETE /users/{userId}/watchlist/{movieId}`: remove a movie from the watchlist
- `PUT /users/{userId}/watchlist/{movieId}/watched`: mark a movie as watched (it leaves the watchlist)
- `DELETE /users/{userId}/watchlist/{movieId}/watched`: mark a movie as not watched
- `GET /users/{userId}/watchlist/watched`: the IDs of the watched movies

A movie already in the watchlist, or not in it, gives `400`. An unknown user gives `404`, and so
does an unknown movie when it is added.

Actors, directors and genres:

- `GET /actors`: all the actors
- `GET /actors/{id}`: one actor (`404` if it does not exist)
- `GET /directors`: all the directors
- `GET /directors/{id}`: one director (`404` if it does not exist)
- `GET /genres`: the labels of the accepted genres

Service:

- `GET /service/reset`: go back to the demo users and the demo catalogue

A user needs a username (not already taken), a first name, a last name, an email with an `@`
and a password of at least 6 characters. The password is hashed before it is saved: the JSON of a
user contains this hash, never the password itself.

A username is the same with or without capital letters (`Alice` and `alice` are the same user).

The service answers with these status codes:

- `200`: OK
- `201`: the user, the movie or the review was created
- `400`: the data is not valid (for example a username already taken or an email without `@`)
- `401`: wrong username or password at login
- `404`: the user or the movie does not exist
- `405`: the method is not allowed on this address
- `415`: the body is not sent as JSON
- `500`: any other error

An error with a message comes back as a JSON with its `type` and its `message`.

## Demo users

- `alice` / `alice123`
- `bob` / `bob12345`
- `admin` / `admin123` (admin)

## Movies

### Body of `POST` and `PUT`

Only these fields can be written. The other fields of a movie (`UUID`, `directorName`,
`ratingAverage`, `ratingCount`, `formattedRating`, `reviews`) are computed by the service and are
ignored if they are sent.

| Field | Rule |
|---|---|
| `title` | required, not empty (spaces around it are removed) |
| `releaseYear` | required, a positive whole number |
| `duration` | optional, a positive whole number (minutes) |
| `synopsis`, `posterUrl` | optional |
| `genres` | optional list of genres (see below) |
| `directorID` | optional, the UUID of a known director; `directorName` is taken from this director |
| `cast` | optional list of UUIDs of known actors, without duplicates |
| `keywords` | optional list of themes, not empty; they are saved in lower case without spaces around them |

`PUT` replaces all these fields. An optional field that is missing or `null` becomes empty: to
remove the director, send `"directorID": null` (or leave it out); to empty the cast, the genres or
the keywords, send `[]` (or leave the list out). The stored movie is updated in place, so its
UUID, its reviews and its ratings stay the same. Everything is checked before anything changes:
an invalid `PUT` leaves the movie and its actors and director as they were.

When the director or the cast changes, the filmographies of the people are updated too.
When a movie is deleted, its reviews are deleted (the authors lose them), it leaves every
watchlist and every list of watched movies, and its actors and director lose it.

Accepted genres: the TMDB labels `Action`, `Adventure`, `Animation`, `Comedy`, `Crime`,
`Documentary`, `Drama`, `Family`, `Fantasy`, `History`, `Horror`, `Music`, `Mystery`, `Romance`,
`Science Fiction`, `TV Movie`, `Thriller`, `War`, `Western`, without capital letters mattering.
The names of the Java constants are accepted too (`ScienceFiction`, `TVMovie`). A movie sent back
by the service shows its genres with the constant names, so it can be sent again as it is.

### Search

`GET /movies/search` takes these optional query parameters:

- `title`: part of the title (`incep` finds `Inception`)
- `actor`: the full name of an actor (`leonardo dicaprio`); every actor with this name counts
- `director`: the full name of a director (`wes craven`)
- `genre`: one accepted genre (`Science Fiction`)
- `keyword`: one saved theme (`dream`)

Spaces around a value are removed and capital letters do not matter. Missing or empty parameters
are ignored and the other ones must all match (logical AND). Without any parameter, the whole
catalogue comes back. An unknown name or a combination without a movie gives `200` and `[]`;
an unknown genre gives `400`.

### Errors

- `400` with `{"type": "java.lang.IllegalArgumentException", "message": ...}`: invalid body,
  unknown genre, unknown actor or director, or a malformed UUID such as `/movies/not-a-uuid`
- `400` with `{"type": "jakarta.ws.rs.ProcessingException", "message": ...}`: a JSON body that
  cannot be read (bad syntax, a text where a number is expected, or a `releaseYear` or `duration`
  that is not a whole number, such as `2000.5` or `99999999999`). The message names the property,
  for example `Unable to deserialize property 'releaseYear' because of: Expected a whole number, got 2000.5`
- `404` with `{"type": "jakarta.ws.rs.NotFoundException", "message": ...}`: a well-formed UUID
  without movie, or an unknown route

Errors raised by JAX-RS itself (unknown route, wrong method, wrong content type) keep their own
status code; only unexpected errors give `500`.

### Demo catalogue

`GET /service/reset` puts back these movies, with fixed IDs:

| Movie | ID | Actors | Director | Genres | Keywords |
|---|---|---|---|---|---|
| Inception | `10000000-0000-0000-0000-000000000001` | Leonardo DiCaprio, Joseph Gordon-Levitt | Christopher Nolan | Science Fiction, Action | dream, heist |
| Shutter Island | `10000000-0000-0000-0000-000000000002` | Leonardo DiCaprio, Mark Ruffalo | Martin Scorsese | Thriller, Mystery | investigation, asylum |
| A Nightmare on Elm Street | `10000000-0000-0000-0000-000000000003` | Robert Englund, Heather Langenkamp | Wes Craven | Horror | dream, slasher |

| Person | ID |
|---|---|
| Leonardo DiCaprio | `20000000-0000-0000-0000-000000000001` |
| Joseph Gordon-Levitt | `20000000-0000-0000-0000-000000000002` |
| Mark Ruffalo | `20000000-0000-0000-0000-000000000003` |
| Robert Englund | `20000000-0000-0000-0000-000000000004` |
| Heather Langenkamp | `20000000-0000-0000-0000-000000000005` |
| Christopher Nolan (director) | `30000000-0000-0000-0000-000000000001` |
| Martin Scorsese (director) | `30000000-0000-0000-0000-000000000002` |
| Wes Craven (director) | `30000000-0000-0000-0000-000000000003` |

### Examples

Add a movie (the answer is `201` with the movie and its new `UUID`):

```http
POST /movies
{
    "title": "Memento",
    "releaseYear": 2000,
    "duration": 113,
    "genres": ["Thriller", "Mystery"],
    "directorID": "30000000-0000-0000-0000-000000000001",
    "cast": [],
    "keywords": ["memory"]
}
```

Change it, with the `UUID` returned by the `POST` (no director anymore, one actor, one more keyword):

```http
PUT /movies/{id}
{
    "title": "Memento (restored)",
    "releaseYear": 2000,
    "duration": 113,
    "genres": ["Thriller"],
    "directorID": null,
    "cast": ["20000000-0000-0000-0000-000000000002"],
    "keywords": ["memory", "tattoo"]
}
```

Find a movie without its title:

- `GET /movies/search?actor=leonardo%20dicaprio` gives Inception and Shutter Island
- `GET /movies/search?keyword=DREAM` gives Inception and A Nightmare on Elm Street
- `GET /movies/search?actor=leonardo%20dicaprio&keyword=DREAM` gives **Inception only**

### Tests

```bash
mvn -f CineRateDomain/pom.xml clean install
mvn -f CineRateService/pom.xml clean verify
```

The tests of the service are in `CineRateService/src/test`. `MovieDataJsonTest` reads request
bodies with Yasson, the JSON-B library of Payara 6 (test dependency only). The Postman folder
`Movie` checks the same cases over HTTP (search, CRUD with the ID returned by the `POST`, errors).
Run `GET /service/reset` before running the collection: it expects the demo catalogue.

## Group

Samy Hajhouj, Elias Mounerat, Mouhamed Sarira, Klisman Kodra
