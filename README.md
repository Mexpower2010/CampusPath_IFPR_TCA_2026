# CampusPath

**A desktop app that shows where professors are on campus and computes the shortest walking route to them.**

Built with Java 21, Swing, JDBC and MySQL for the Programming Language II course (4th term, 2026) at
IFPR – Cascavel Campus, Integrated Technical Course in Informatics.

> Author: Marcelo Henrique Rocha Filho  
> Instructor: Prof. Roberta Vanessa Rojo Parcianello  
> License: MIT

## What it does

Students open a campus map, see which professors are in which room (with photo, message and time),
and ask for a route from any point to a room. Professors log in to set their room and publish a
status message. Administrators manage the map data.

| Role | Login | Can |
|---|---|---|
| Student | none | View the map, see present professors, compute routes |
| Professor | login + password | Set or clear current room, publish a message, update name and photo |
| Administrator | e-mail + password | CRUD for blocks, floors, points, segments, professors and admins |

## Highlights

- **Shortest-path routing:** Dijkstra implemented by hand with `PriorityQueue` over points and
  segments stored in the database. Segments are bidirectional.
- **Route filters:** "avoid stairs" (allows elevators) and "covered only". Closed segments are never
  used. If no route exists, the app explains why and keeps the filters.
- **JDBC persistence:** one DAO per entity, all queries use `PreparedStatement`, with
  `try-with-resources` and a mapping method from `ResultSet` to model objects.
- **Integrity in the database:** primary and foreign keys, `UNIQUE`, `CHECK` constraints, and triggers
  (a professor's location must be a point of type `SALA`).
- **Layered design:** `view` → `controller` → `service`/`dao` → `model`. Only the DAO layer talks SQL.
- **Snapshot sync:** the app exports the database state to `dados/snapshot.sql`, versioned in Git, and
  applies it in a transaction on startup when it is newer than what the local database has.

## Data model

Six entities: `bloco`, `andar`, `ponto`, `trecho`, `professor`, `administrador` (plus a small
`controle_sync` helper table). A block has floors, a floor has points, and segments connect two points.
Seed data: 4 blocks, 6 floors, 64 points, 69 segments, 1 admin and 3 professors.

```
bloco 1─N andar 1─N ponto 1─N trecho (A and B ends)
                    ponto 1─N professor (optional current room)
```

## Tech stack

Java 21 · Swing · JDBC · MySQL 8 (driver `mysql-connector-java` 8.0.33) · Maven

## Project layout

```
campus_path/
├── pom.xml
├── dados/snapshot.sql            database state versioned in Git
├── fotos/                        professor profile pictures
└── src/main/
    ├── java/ifpr/edu/
    │   ├── Main.java
    │   ├── view/                 Swing window, panels, map component
    │   ├── controller/           Mapa, Professor and Admin controllers
    │   ├── service/              RotaService (Dijkstra), AutenticacaoService
    │   ├── dao/                  6 DAOs + SnapshotService
    │   ├── model/                domain classes
    │   ├── db/                   Conexao, PersistenciaException
    │   └── util/                 Senha (SHA-256)
    └── resources/
        ├── db.properties.example
        └── db/                   schema, seed data, procedures, queries, app user
```

## Running it

Requirements: JDK 21+, MySQL 8 running locally, VS Code with the Extension Pack for Java (or any
Java IDE).

1. Create the database (run from the repository root):
   ```bash
   mysql -u root -p < campus_path/src/main/resources/db/schema.sql
   mysql -u root -p < campus_path/src/main/resources/db/dados_iniciais.sql
   ```
   Optionally run `db/usuario.sql` to create a least-privilege `campuspath` user (replace the
   placeholder password first).
2. Copy `campus_path/src/main/resources/db.properties.example` to
   `campus_path/src/main/resources/db.properties` and fill in the user and password. You can also use
   the `CAMPUSPATH_DB_URL`, `CAMPUSPATH_DB_USER` and `CAMPUSPATH_DB_PASSWORD` environment variables,
   which take precedence. `db.properties` is git-ignored.
3. Open the **`campus_path`** folder (not the repository root) in the IDE and run
   `src/main/java/ifpr/edu/Main.java`. The app reads `dados/` and `fotos/` relative to the working
   directory, so it must run from `campus_path`.

Demo accounts (seed data, for local testing only):

| Role | Access | Password |
|---|---|---|
| Administrator | `admin@campuspath.ifpr.edu.br` | `admin123` |
| Professor | `roberta.parcianello`, `fabio.moreira`, `ana.souza` | `123456` |

## Requirements status

The specification defines 28 functional requirements (RF01–RF28). Two are only partly met:

- **RF02:** switching floors works (click a stair or elevator point), but there is no block selector;
  the ground floor shows all four blocks together.
- **RF10:** the total distance is shown, but the step-by-step list of points is not displayed.

## Known limitations

- Passwords are hashed with unsalted SHA-256, which is enough for coursework but not for production;
  bcrypt, scrypt or PBKDF2 with a per-user salt would be the right choice.
- Snapshot sync happens per commit, not in real time; the latest commit wins.
- Profile pictures are not stored in the snapshot, only their paths.
- `SnapshotService` builds its `insert` lines as strings; it is a backup tool, while all regular
  application writes go through `PreparedStatement`.
- The compiler target is Java 21; some IDEs may warn if they are configured with a different JDK.

## Documentation

The full specification in Portuguese (requirements, ER diagram, attribute tables, SQL, routing
algorithm, troubleshooting) is in [`docs/ESPECIFICACAO.pt-BR.md`](docs/ESPECIFICACAO.pt-BR.md).

## License

MIT, see [`LICENSE`](LICENSE).
