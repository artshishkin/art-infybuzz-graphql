package net.shyshkin.study.graphql.gettingstarted.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import net.shyshkin.study.graphql.gettingstarted.request.CreateSubjectRequest;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuerySpringBootTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class WithInlinedDocumentTests {

        @Test
        void firstQuery() {
            //given
            String document = "{firstQuery}";
            var req = Map.of("query", document);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data/firstQuery").asText())
                    .isEqualTo("First Query");
        }

        @Test
        void secondQuery() {
            //given
            String document = "{secondQuery}";
            var req = Map.of("query", document);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data/firstQuery").asText())
                    .isNullOrEmpty();
            assertThat(json.at("/data/secondQuery").asText())
                    .isEqualTo("Second Query");
        }

        @Test
        void bothFirstAndSecondQueries() {
            //given
            String document = "{\n" +
                    "firstQuery\n" +
                    "secondQuery\n" +
                    "}";

            var req = Map.of("query", document);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data/firstQuery").asText())
                    .isEqualTo("First Query");
            assertThat(json.at("/data/secondQuery").asText())
                    .isEqualTo("Second Query");
        }
    }

    @Nested
    class WithInputDataTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{fullName(firstName:\"Art\",lastName:\"Shyshkin\")}";
            var req = Map.of("query", fullNameQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data/fullName").asText())
                    .isEqualTo("Art Shyshkin");
        }
    }

    @Nested
    class WithInputJsonDataTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{\n" +
                    "fullNameJson(sampleRequest:{\n" +
                    "    firstName:\"Art\"\n" +
                    "    lastName:\"Shyshkin\"\n" +
                    "  })\n" +
                    "}";

            var req = Map.of("query", fullNameQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data/fullNameJson").asText())
                    .isEqualTo("Art Shyshkin");
        }
    }

    @Nested
    class SchemaValidationTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{\n" +
                    "fullNameJson(sampleRequest:{\n" +
                    "    lastName:\"Shyshkin\"\n" +
                    "  })\n" +
                    "}";

            var req = Map.of("query", fullNameQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            assertThat(json.at("/data").asText())
                    .isEqualTo("null");
            assertThat(json.at("/errors/0/message").asText())
                    .contains("firstName", "Validation error of type WrongType");
        }
    }

    @Nested
    class GetStudentByIdTests {

        @Test
        void getStudent() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
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
                                    )
                    ));
        }
    }

    @Nested
    class ResponseFlexibilityTests {

        @Test
        void getStudent() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            var data = json.at("/data");
            log.debug("{}", data);
            assertThat(data.at("/student/city").asText()).isEmpty();
            assertThat(data.at("/student/street").asText()).isEmpty();
            assertThat(data.at("/student/email").asText()).isEmpty();
            assertThat(data.at("/student/firstName").asText()).isNotEmpty();
            StudentResponse studentResponse = objectMapper.readValue(data.at("/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isNull(),
                            () -> assertThat(st.getStreet()).isNull(),
                            () -> assertThat(st.getEmail()).isNull()
                    ));
        }
    }

    @Nested
    class GetStudentFilteredTests {

        @Test
        void getStudent_subjectPresent() throws JsonProcessingException {

            //given
            String subjectName = "Java";
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects (subjectNameFilters: [" + subjectName + "]) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(1)
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", 1L)
                                            .hasFieldOrPropertyWithValue("subjectName", "Java")
                                            .hasFieldOrPropertyWithValue("marksObtained", 80.00)
                                    )
                    ));
        }

        @Test
        void getStudent_subjectAbsent() throws JsonProcessingException {

            //given
            String subjectName = "MongoDB";
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects (subjectNameFilters: [" + subjectName + "]) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects()).isEmpty()
                    ));
        }

        @Test
        void getStudent_withAllSubjects() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects  (subjectNameFilters: [All]) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
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
                                    )
                    ));
        }

        @Test
        void getStudent_withNullFilter() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects  (subjectNameFilters: null) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
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
                                    )
                    ));
        }

        @Test
        void getStudent_withMultipleFilterValues() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects  (subjectNameFilters: [Java,MySQL]) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
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
                                    )
                    ));
        }

        @Test
        void getStudent_skippingNullFilterValues() throws JsonProcessingException {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    fullName\n" +
                    "    learningSubjects  (subjectNameFilters: [Java,null,MySQL]) {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", studentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("John Smith"),
                            () -> assertThat(st.getLearningSubjects())
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
                                    )
                    ));
        }

    }

    @Nested
    class CreateStudentTests {

        @Test
        void createStudent() throws JsonProcessingException {

            //given
            String createStudentQuery = "mutation{\n" +
                    "  createStudent(createStudentRequest:{\n" +
                    "    firstName:\"Kate\"\n" +
                    "    lastName:\"Shyshkina\"\n" +
                    "    email:\"kate@gmail.com\"\n" +
                    "    street:\"Nekrasova\"\n" +
                    "    city:\"Kramatorsk\"\n" +
                    "    subjectsLearning:[\n" +
                    "      {\n" +
                    "        subjectName:\"MySQL\"\n" +
                    "        marksObtained:67.0\n" +
                    "      },\n" +
                    "      {\n" +
                    "        subjectName:\"MongoDB\"\n" +
                    "        marksObtained:85\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    learningSubjects {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "    fullName\n" +
                    "  }\n" +
                    "}";
            var req = Map.of("query", createStudentQuery);

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/createStudent").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isGreaterThanOrEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("Kate"),
                            () -> assertThat(st.getLastName()).isEqualTo("Shyshkina"),
                            () -> assertThat(st.getCity()).isEqualTo("Kramatorsk"),
                            () -> assertThat(st.getStreet()).isEqualTo("Nekrasova"),
                            () -> assertThat(st.getEmail()).isEqualTo("kate@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("Kate Shyshkina"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(2)
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                                            .hasFieldOrPropertyWithValue("marksObtained", 67.0)
                                    )
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "MongoDB")
                                            .hasFieldOrPropertyWithValue("marksObtained", 85.0)
                                    )
                    ));
        }

    }

    @Nested
    class QueryVariableTests {

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L})
        void getStudent(long studentId) throws JsonProcessingException {

            //given
            String queryWithVariable = "query student($studentId: Long){\n" +
                    "  student(id:$studentId) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    fullName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    learningSubjects {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            String variableValue = "{\"studentId\":" + studentId + "}";
            var req = Map.of(
                    "query", queryWithVariable,
                    "variables", variableValue
            );

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/student").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects", "student"),
                            () -> assertThat(st.getId()).isEqualTo(studentId),
                            () -> log.debug("{}", st)
                    ));
        }
    }

    @Nested
    class MutationWithVariableTests {

        @Test
        void createStudent_throughJson() throws JsonProcessingException {

            //given
            String variablesJson = "{\n" +
                    "  \"createStReq\": {\n" +
                    "    \"firstName\":\"Nazar\",\n" +
                    "    \"lastName\":\"Shyshkin\",\n" +
                    "    \"email\":\"nazar.shyshkin@gmail.com\",\n" +
                    "    \"street\":\"Kramatorska\",\n" +
                    "    \"city\":\"Lviv\",\n" +
                    "    \"subjectsLearning\":[\n" +
                    "      {\n" +
                    "        \"subjectName\":\"MySQL\",\n" +
                    "        \"marksObtained\":78.0\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"subjectName\":\"Java\",\n" +
                    "        \"marksObtained\":90\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

            log.debug("Variables JSON: {}", variablesJson);

            String createStudentQuery = "mutation ($createStReq:CreateStudentRequest){\n" +
                    "  createStudent(createStudentRequest: $createStReq) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    learningSubjects {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "    fullName\n" +
                    "  }\n" +
                    "}";
            var req = Map.of(
                    "query", createStudentQuery,
                    "variables", variablesJson
            );

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/createStudent").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isGreaterThanOrEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("Nazar"),
                            () -> assertThat(st.getLastName()).isEqualTo("Shyshkin"),
                            () -> assertThat(st.getCity()).isEqualTo("Lviv"),
                            () -> assertThat(st.getStreet()).isEqualTo("Kramatorska"),
                            () -> assertThat(st.getEmail()).isEqualTo("nazar.shyshkin@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("Nazar Shyshkin"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(2)
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                                            .hasFieldOrPropertyWithValue("marksObtained", 78.0)
                                    )
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "Java")
                                            .hasFieldOrPropertyWithValue("marksObtained", 90.0)
                                    )
                    ));
        }

        @Test
        void createStudent_throughObject() throws JsonProcessingException {

            //given
            var createStudentRequest = CreateStudentRequest.builder()
                    .firstName("Nazar")
                    .lastName("Shyshkin")
                    .email("nazar.shyshkin@gmail.com")
                    .city("Lviv")
                    .street("Kramatorska")
                    .subjectsLearning(List.of(
                            CreateSubjectRequest.builder()
                                    .subjectName("MySQL")
                                    .marksObtained(78.0)
                                    .build(),
                            CreateSubjectRequest.builder()
                                    .subjectName("Java")
                                    .marksObtained(90.0)
                                    .build()
                    ))
                    .build();

            var variables = Map.of(
                    "createStReq", createStudentRequest
            );

            String variablesJson = objectMapper.writeValueAsString(variables);

            log.debug("Variables JSON: {}", variablesJson);

            String createStudentQuery = "mutation ($createStReq:CreateStudentRequest){\n" +
                    "  createStudent(createStudentRequest: $createStReq) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "    email\n" +
                    "    street\n" +
                    "    city\n" +
                    "    learningSubjects {\n" +
                    "      id\n" +
                    "      subjectName\n" +
                    "      marksObtained\n" +
                    "    }\n" +
                    "    fullName\n" +
                    "  }\n" +
                    "}";
            var req = Map.of(
                    "query", createStudentQuery,
                    "variables", variablesJson
            );

            //when
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity("/student-service", req, JsonNode.class);

            //then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            var json = responseEntity.getBody();
            log.debug("{}", json);
            StudentResponse studentResponse = objectMapper.readValue(json.at("/data/createStudent").toString(), StudentResponse.class);

            assertThat(studentResponse)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isGreaterThanOrEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("Nazar"),
                            () -> assertThat(st.getLastName()).isEqualTo("Shyshkin"),
                            () -> assertThat(st.getCity()).isEqualTo("Lviv"),
                            () -> assertThat(st.getStreet()).isEqualTo("Kramatorska"),
                            () -> assertThat(st.getEmail()).isEqualTo("nazar.shyshkin@gmail.com"),
                            () -> assertThat(st.getFullName()).isEqualTo("Nazar Shyshkin"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(2)
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                                            .hasFieldOrPropertyWithValue("marksObtained", 78.0)
                                    )
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("subjectName", "Java")
                                            .hasFieldOrPropertyWithValue("marksObtained", 90.0)
                                    )
                    ));
        }

    }

}