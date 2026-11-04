package io.cloudiannexa.kinship;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "kinship.neo4j.bolt-port=0")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class KinshipApplicationTests {

	@TempDir
	static Path dataDirectory;

	@Autowired
	Driver driver;

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("kinship.neo4j.data-directory", () -> dataDirectory.toString());
	}

	@Test
	void contextLoads() {
		driver.verifyConnectivity();
		try (var session = driver.session()) {
			var name = session.executeWrite(transaction -> transaction.run(
					"CREATE (person:Person {name: $name}) RETURN person.name AS name",
					Map.of("name", "Kinship")).single().get("name").asString());
			assertEquals("Kinship", name);
		}
	}

}
