# Kinship

Run the application with `./mvnw spring-boot:run`. No external database or Docker
is required.

Neo4j runs in the application process and persists its graph in `./data/neo4j`.
Configure `kinship.neo4j.data-directory` and `kinship.neo4j.bolt-port` in
`src/main/resources/application.yaml` to change the storage location or Bolt port
(default: 7687). The unauthenticated Bolt endpoint binds only to `127.0.0.1`;
it is intended for local development on a trusted machine, not production.
Spring's Neo4j driver connects to this embedded instance automatically. The
database shuts down with the application.

The existing JPA and Modulith components use an in-memory H2 database, whose data
is lost on shutdown. Liquibase is disabled because no changelog exists yet.
Docker Compose integration is disabled so the bundled Compose file does not
start a second Neo4j instance.

Run `./mvnw test` to load the full application context and verify a graph write
through the Neo4j driver. Tests use a temporary graph directory and a random
loopback Bolt port, leaving application data untouched.
