# Graph Report - CineRate-main  (2026-11-12)

## Corpus Check
- Corpus is ~6,838 words - fits in a single context window. You may not need a graph.

## Summary
- 389 nodes · 900 edges · 20 communities (4 shown, 16 thin omitted)
- Extraction: 70% EXTRACTED · 30% INFERRED · 0% AMBIGUOUS · INFERRED: 269 edges (avg confidence: 0.85)
- Token cost: 83,266 input · 0 output

## Community Hubs (Navigation)
- Person Model & Domain Tests
- Review & Rating Flow
- Domain Utils & Packaging
- Genre Model
- Movie Model
- User Model
- Rating Model
- User REST Resource
- Project Documentation
- Exception Handling
- Service Resources & App State
- Service Package Metadata
- Service Package Root

## God Nodes (most connected - your core abstractions)
1. `Movie` - 64 edges
2. `User` - 63 edges
3. `Review` - 43 edges
4. `Genre` - 29 edges
5. `Person` - 28 edges
6. `Rating` - 20 edges
7. `ApplicationState` - 18 edges
8. `Utils` - 16 edges
9. `ReviewTest` - 16 edges
10. `MovieTest` - 15 edges

## Surprising Connections (you probably didn't know these)
- `ApplicationState` --references--> `User`  [EXTRACTED]
  CineRateService/src/main/java/ch/unil/doplab/cinerate/domain/ApplicationState.java → CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/User.java
- `ch.unil.doplab.cinerate:CineRateService` --depends_on--> `ch.unil.doplab.cinerate:CineRateDomain`  [EXTRACTED]
  CineRateService/pom.xml → CineRateDomain/pom.xml
- `Movie` --references--> `Genre`  [EXTRACTED]
  CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/Movie.java → CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/Genre.java
- `Movie` --references--> `Review`  [EXTRACTED]
  CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/Movie.java → CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/Review.java
- `MovieTest` --references--> `Movie`  [EXTRACTED]
  CineRateDomain/src/test/java/ch/unil/doplab/cinerate/domain/MovieTest.java → CineRateDomain/src/main/java/ch/unil/doplab/cinerate/domain/Movie.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **CineRate Domain Model** — readme_user, readme_movie, readme_review, readme_person, readme_actor, readme_director, readme_genre, readme_rating, readme_utils [EXTRACTED 1.00]

## Communities (20 total, 16 thin omitted)

### Community 0 - "Person Model & Domain Tests"
Cohesion: 0.06
Nodes (11): Actor, Director, Person, ActorTest, DirectorTest, GenreTest, MovieTest, PersonTest (+3 more)

### Community 3 - "Genre Model"
Cohesion: 0.08
Nodes (20): Genre, Action, Adventure, Animation, Comedy, Crime, Documentary, Drama (+12 more)

### Community 7 - "Rating Model"
Cohesion: 0.12
Nodes (12): Rating, EIGHT, FIVE, FOUR, NINE, NO_RATING, ONE, SEVEN (+4 more)

### Community 9 - "Project Documentation"
Cohesion: 0.19
Nodes (15): Actor, ApplicationState, CineRate, CineRateDomain, CineRateService, Director, Genre, Movie (+7 more)

## Knowledge Gaps
- **34 isolated node(s):** `ch.unil.doplab.cinerate:CineRateDomain`, `Action`, `Adventure`, `Animation`, `Comedy` (+29 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 98 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie Model` to `Person Model & Domain Tests`, `Review & Rating Flow`, `Domain Utils & Packaging`, `Genre Model`, `Movie-Genre Linking`, `Movie Attributes`, `Domain Package Metadata`, `Movie Misc`?**
  _High betweenness centrality (0.286) - this node is a cross-community bridge._
- **What connects `ch.unil.doplab.cinerate:CineRateDomain`, `Action`, `Adventure` to the rest of the system?**
  _34 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Person Model & Domain Tests` be split into smaller, more focused modules?**
  _Cohesion score 0.059244126659857 - nodes in this community are weakly interconnected._
- **Why does `User` connect `User Model` to `Person Model & Domain Tests`, `Review & Rating Flow`, `Domain Utils & Packaging`, `User State & Persistence`, `User REST Resource`, `Service Resources & App State`, `Service Collections & Types`, `User Auth Helpers`, `Domain Package Metadata`?**
  _High betweenness centrality (0.274) - this node is a cross-community bridge._
- **Should `Review & Rating Flow` be split into smaller, more focused modules?**
  _Cohesion score 0.06874669487043893 - nodes in this community are weakly interconnected._
- **Why does `Review` connect `Review & Rating Flow` to `Domain Utils & Packaging`, `Movie Model`, `User Model`, `Rating Model`?**
  _High betweenness centrality (0.156) - this node is a cross-community bridge._
- **Should `Domain Utils & Packaging` be split into smaller, more focused modules?**
  _Cohesion score 0.10588235294117647 - nodes in this community are weakly interconnected._