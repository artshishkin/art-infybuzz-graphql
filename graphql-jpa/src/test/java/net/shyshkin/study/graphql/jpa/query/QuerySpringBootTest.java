package net.shyshkin.study.graphql.jpa.query;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
class QuerySpringBootTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Nested
    class WithInlinedDocumentTests {

        @Test
        void firstQuery() {
            //given
            String document = "{firstQuery}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
        }

        @Test
        void secondQuery() {
            //given
            String document = "{\n" +
                    "secondQuery\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .pathDoesNotExist();
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }

        @Test
        void bothFirstAndSecondQueries() {

            //given
            String document = "{\n" +
                    "firstQuery\n" +
                    "secondQuery\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }
    }

    @Nested
    class WithExternalDocumentTests {

        @Test
        void firstQuery_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("firstQueryDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
        }

        @Test
        void secondQuery_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("secondQueryDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .pathDoesNotExist();
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }

        @Test
        void bothFirstAndSecondQueries_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("bothQueriesDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }
    }

    @Nested
    class WithInputDataTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{fullName(firstName:\"Art\",lastName:\"Shyshkin\")}";

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDoc() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullName")
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Kate Shyshkina");
        }

        @Test
        void fullName_thoughDocWithParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameParam")
                    .variable("firstName", "Nazar")
                    .variable("lastName", "Shyshkin")
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Nazar Shyshkin");
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

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDoc() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJson")
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDocWithParam() {

            //given
            var sampleRequest = Map.of(
                    "firstName", "Arina",
                    "lastName", "Shyshkina"
            );

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJsonParam")
                    .variable("sampleRequest", sampleRequest)
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Arina Shyshkina");
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

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.errors()
                    .satisfy(errors -> assertThat(errors)
                                    .hasSize(1)
                                    .allSatisfy(error -> assertAll(
//                                    () -> assertThat(error).isInstanceOf(ValidationError.class),
                                                    () -> assertThat(error.getMessage().toLowerCase()).contains("firstname", "valid"))
                                    )
                    );
        }

        @Test
        void fullName_thoughDocWithParam() {

            //given
            var sampleRequest = Map.of(
//                    "firstName", "Arina",
                    "lastName", "Shyshkina"
            );

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJsonParam")
                    .variable("sampleRequest", sampleRequest)
                    .execute();
            //then
            response.errors()
                    .satisfy(errors -> assertThat(errors)
                                    .hasSize(1)
                                    .allSatisfy(error -> assertAll(
//                                    () -> assertThat(error).isInstanceOf(ValidationError.class),
                                                    () -> assertThat(error.getMessage().toLowerCase()).contains("firstname", "valid"))
                                    )
                    );
        }
    }

    @Nested
    class GetStudentByIdTests {

        @Test
        void getStudent() {

            //given
            String fullNameQuery = "query{\n" +
                    "  student(id:1) {\n" +
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
                    "  }\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(2)
                                    .anySatisfy(subResp->assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id",1L)
                                            .hasFieldOrPropertyWithValue("subjectName","Java")
                                            .hasFieldOrPropertyWithValue("marksObtained",80.00)
                                    )
                                    .anySatisfy(subResp->assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id",2L)
                                            .hasFieldOrPropertyWithValue("subjectName","MySQL")
                                            .hasFieldOrPropertyWithValue("marksObtained",70.00)
                                    )
                    ));
        }

        @Test
        void getStudent_thoughDocWithParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("studentById")
                    .variable("studentId", 1L)
                    .execute();
            //then
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com")
                    ));
        }
    }

    @Nested
    class ResponseFlexibilityTests {

        @Test
        void getStudent() {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:1) {\n" +
                    "    id\n" +
                    "    firstName\n" +
                    "    lastName\n" +
                    "  }\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(studentQuery)
                    .execute();
            //then
            response.path("student.city").pathDoesNotExist();
            response.path("student.street").pathDoesNotExist();
            response.path("student.email").pathDoesNotExist();
            response.path("student.firstName").hasValue();
            response.path("student")
                    .entity(StudentResponse.class)
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

        @Test
        void getStudent_thoughDocWithParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("studentById")
                    .variable("studentId", 1L)
                    .execute();
            //then
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("learningSubjects"),
                            () -> assertThat(st.getId()).isEqualTo(1L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("John"),
                            () -> assertThat(st.getLastName()).isEqualTo("Smith"),
                            () -> assertThat(st.getCity()).isEqualTo("Delhi"),
                            () -> assertThat(st.getStreet()).isEqualTo("Happy Street"),
                            () -> assertThat(st.getEmail()).isEqualTo("john@gmail.com")
                    ));
        }
    }

}