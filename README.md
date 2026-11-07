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
- Movies and search, reviews and watchlists in the service: to do.

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

## Requests

- `GET /users`: all the users
- `GET /users/{id}`: one user
- `POST /users`: add a user
- `PUT /users/{id}`: change a user (the ID and the role stay the same)
- `DELETE /users/{id}`: delete a user
- `GET /service/reset`: go back to the demo users

A user needs a username (not already taken), a first name, a last name, an email with an `@`
and a password of at least 6 characters. The password is hashed and is never sent back.

An error comes back with a JSON with its `type` and its `message`, and with the status 400 if the data
is not valid, 404 if the user does not exist, or 500 for the rest.

## Demo users

- `alice` / `alice123`
- `bob` / `bob12345`
- `admin` / `admin123` (admin)

## Group

Samy Hajhouj, Elias Mounerat, Mouhamed Sarira, Klinsmann Kodra
