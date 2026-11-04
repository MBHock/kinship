package io.cloudiannexa.kinship;

import java.net.URI;
import java.nio.file.Path;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.connectors.ConnectorPortRegister;
import org.neo4j.configuration.connectors.ConnectorType;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.neo4j.autoconfigure.Neo4jConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class EmbeddedNeo4jConfiguration {

	@Bean(destroyMethod = "shutdown")
	DatabaseManagementService embeddedNeo4j(
			@Value("${kinship.neo4j.data-directory}") Path dataDirectory,
			@Value("${kinship.neo4j.bolt-port}") int boltPort) {
		return new DatabaseManagementServiceBuilder(dataDirectory)
				.setConfig(GraphDatabaseSettings.auth_enabled, false)
				.setConfig(BoltConnector.enabled, true)
				.setConfig(BoltConnector.listen_address, new SocketAddress("127.0.0.1", boltPort))
				.build();
	}

	@Bean
	Neo4jConnectionDetails neo4jConnectionDetails(DatabaseManagementService embeddedNeo4j) {
		var database = (GraphDatabaseAPI) embeddedNeo4j.database("neo4j");
		var address = database.getDependencyResolver()
				.resolveDependency(ConnectorPortRegister.class)
				.getLocalAddress(ConnectorType.BOLT);
		var uri = URI.create("bolt://127.0.0.1:" + address.getPort());
		return new Neo4jConnectionDetails() {
			@Override
			public URI getUri() {
				return uri;
			}

			@Override
			public AuthToken getAuthToken() {
				return AuthTokens.none();
			}
		};
	}

}
