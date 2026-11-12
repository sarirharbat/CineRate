# CineRate

A movie rating website. Users can rate movies from 1 to 10, write a review, keep a watchlist
and see the average rating of each movie.

This is the project of our group for the Software Architectures course (UNIL).
Phase 1 is the domain model and the web service.

## Projects

- `CineRateDomain`: the model classes (`User`, `Movie`, `Review`, `Person`, `Actor`, `Director`,
  `Genre`, `Rating`, `Utils`) and their unit tests.
- `CineRateService`: the REST web service. The data is kept in memory in `ApplicationState`.

## Who did what

- Elias Mounerat: the domain model and its tests.
- Mouhamed Sarira: the web service, the users CRUD, the README and the Postman collection.
- Klinsmann Kodra: the movies CRUD and the advanced search in the service (with a demo catalogue).
- Reviews and watchlists in the service: to do.

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
(Cmd+Shift+R).

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

Service:

- `GET /service/reset`: go back to the demo users and the demo catalogue

A user needs a username (not already taken), a first name, a last name, an email with an `@`
and a password of at least 6 characters. The password is hashed and is never sent back.

A username is the same with or without capital letters (`Alice` and `alice` are the same user).

The service answers with these status codes:

- `200`: OK
- `201`: the user or the movie was created
- `400`: the data is not valid (for example a username already taken or an email without `@`)
- `401`: wrong username or password at login
- `404`: the user or the movie does not exist
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

Samy Hajhouj, Elias Mounerat, Mouhamed Sarira, Klinsmann Kodra
