# Démo — Personne 3 : films et recherche (≈ 1 minute)

Préparation : service déployé sur Payara, Postman ouvert sur la collection `CineRate`,
dossier `Movie`. Lancer `GET /service/reset` juste avant l'enregistrement.

| Temps | À l'écran | À dire |
|---|---|---|
| 0:00 | `GET /movies` | « Ma partie, c'est le catalogue des films et la recherche. Le service garde en mémoire un petit catalogue de démonstration avec des acteurs, des réalisateurs, des genres et des thèmes. » |
| 0:10 | `POST /movies` (Memento) → `201` | « Je crée un film temporaire, Memento. C'est le service qui génère son identifiant, et il relie automatiquement le film à son réalisateur, Christopher Nolan. » |
| 0:20 | `GET /movies/{id}` → `200` | « Avec cet identifiant, je relis la fiche du film. » |
| 0:25 | `PUT /movies/{id}` → `200` | « Je le modifie : nouveau titre, un acteur, un mot-clé en plus, plus de réalisateur. L'identifiant reste le même, et les filmographies des personnes sont mises à jour. Une donnée invalide, par exemple un genre inconnu, est refusée avec un 400 et ne change rien. » |
| 0:35 | `DELETE /movies/{id}` → `200`, puis `GET` → `404` | « Je le supprime. Ses avis et ses entrées de watchlist disparaissent avec lui, et une nouvelle lecture renvoie 404. » |
| 0:45 | `GET /movies/search?actor=leonardo dicaprio` → 2 films, puis `?keyword=DREAM` → 2 films | « Maintenant, retrouvons un film sans son titre. DiCaprio seul donne deux films, le thème "dream" seul aussi. » |
| 0:52 | `GET /movies/search?actor=leonardo dicaprio&keyword=DREAM` → Inception | « Les critères se combinent avec un ET logique, sans tenir compte des majuscules : il ne reste qu'Inception. » |
| 1:00 | — | « Le tout est couvert par des tests JUnit et par les assertions de la collection Postman. » |
