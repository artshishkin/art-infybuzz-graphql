package net.shyshkin.study.graphql.clientlegacy;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.clientlegacy.request.CreateStudentRequest;
import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "graphql.client.url=http://${SERVICES_HOST}:${SERVICES_PORT}/student-service"
})
@ContextConfiguration(initializers = GraphQLClientLegacyApplicationTests.Initializer.class)
class GraphQLClientLegacyApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    static GenericContainer<?> graphqlJpaApp = new GenericContainer<>("artarkatesoft/art-infybuzz-graphql-jpa")
            .withExposedPorts(8080)
            .withEnv("spring.profiles.active", "testcontainer")
            .waitingFor(Wait.forLogMessage(".*Started GraphQLJpaApplication.*\\n", 1));

    @Test
    void getStudentTest() {

        //given
        Long studentId = 1L;

        //when
        ResponseEntity<StudentResponse> responseEntity = restTemplate.getForEntity("/api/students/{id}", StudentResponse.class, studentId);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentResponse studentResponse = responseEntity.getBody();
        assertThat(studentResponse)
                .hasNoNullFieldsOrProperties()
                .satisfies(st -> assertAll(
                        () -> assertThat(st.getId()).isEqualTo(1L),
                        () -> log.debug("{}", st),
                        () -> assertThat(st.getFirstName()).isEqualTo("John"),
                        () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                        () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                        () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                        () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                        () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com")
                ));
        assertThat(studentResponse.getLearningSubjects())
                .hasSize(2)
                .anySatisfy(subResp -> assertThat(subResp)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("subjectName", "Java")
                        .hasFieldOrPropertyWithValue("marksObtained", 80.00)
                )
                .anySatisfy(subResp -> assertThat(subResp)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                        .hasFieldOrPropertyWithValue("marksObtained", 70.00)
                );
    }

    @Test
    void createStudentTest() throws IOException {

        //given
        String studentJson = "{\n" +
                "    \"firstName\":\"Kate\",\n" +
                "    \"lastName\":\"Shyshkina\",\n" +
                "    \"email\":\"kate@gmail.com\",\n" +
                "    \"street\":\"Nekrasova\",\n" +
                "    \"city\":\"Kramatorsk\",\n" +
                "    \"subjectsLearning\":[\n" +
                "      {\n" +
                "        \"subjectName\":\"MySQL\",\n" +
                "        \"marksObtained\":67.0\n" +
                "      },\n" +
                "      {\n" +
                "        \"subjectName\":\"MongoDB\",\n" +
                "        \"marksObtained\":85\n" +
                "      }\n" +
                "    ]\n" +
                "  }";
        CreateStudentRequest createStudentRequest = objectMapper.readValue(studentJson, CreateStudentRequest.class);

        //when
        ResponseEntity<StudentResponse> responseEntity = restTemplate.postForEntity("/api/students", createStudentRequest, StudentResponse.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        StudentResponse studentResponse = responseEntity.getBody();

        assertThat(studentResponse)
                .hasNoNullFieldsOrProperties()
                .satisfies(st -> assertAll(
                        () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                        () -> assertThat(st.getId()).isGreaterThanOrEqualTo(1L),
                        () -> log.debug("{}", st),
                        () -> assertThat(st.getFirstName()).isEqualTo("Kate"),
                        () -> assertThat(st.getLastName()).isEqualTo("Shyshkina"),
                        () -> assertThat(st.getFullName()).isEqualTo("Kate Shyshkina"),
                        () -> assertThat(st.getCity()).isEqualTo("Kramatorsk"),
                        () -> assertThat(st.getStreet()).isEqualTo("Nekrasova"),
                        () -> assertThat(st.getEmail()).isEqualTo("kate@gmail.com"),
                        () -> assertThat(st.getLearningSubjects())
                                .hasSize(2)
                                .anySatisfy(subResp -> assertThat(subResp)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                                        .hasFieldOrPropertyWithValue("marksObtained", 67.00)
                                )
                                .anySatisfy(subResp -> assertThat(subResp)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("subjectName", "MongoDB")
                                        .hasFieldOrPropertyWithValue("marksObtained", 85.00)
                                )
                ));
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