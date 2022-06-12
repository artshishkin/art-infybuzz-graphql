package net.shyshkin.study.graphql.clientlegacy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "graphql.client.url=http://${SERVICES_HOST}:${SERVICES_PORT}/student-service"
})
@ContextConfiguration(initializers = GraphQLClientLegacyApplicationTests.Initializer.class)
class GraphQLClientLegacyApplicationTests {

    @Container
    static GenericContainer<?> graphqlJpaApp = new GenericContainer<>("artarkatesoft/art-infybuzz-graphql-jpa")
            .withExposedPorts(8080)
            .withEnv("spring.profiles.active","testcontainer")
            .waitingFor(Wait.forLogMessage(".*Started GraphQLJpaApplication.*\\n", 1));

    @Test
    void contextLoads() {
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String host = graphqlJpaApp.getHost();
            Integer port = graphqlJpaApp.getMappedPort(8080);

            System.setProperty("SERVICES_HOST", host);
            System.setProperty("SERVICES_PORT", String.valueOf(port));
        }
    }

}