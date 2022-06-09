package net.shyshkin.study.graphql.jpa.query;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.config.ScalarConfig;
import net.shyshkin.study.graphql.jpa.entity.Address;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import net.shyshkin.study.graphql.jpa.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureGraphQlTester
@GraphQlTest
@ContextConfiguration(classes = {ScalarConfig.class, Query.class})
class QueryTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    StudentService studentService;

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
    class GetStudentByIdMockTests {

        @BeforeEach
        void setUp() {
            given(studentService.getStudentById(anyLong())).willReturn(mockStudent());
        }

        @AfterEach
        void tearDown() {
            then(studentService).should().getStudentById(eq(123L));
        }

        @Test
        void getStudent() {

            //given
            String fullNameQuery = "query{\n" +
                    "  student(id:123) {\n" +
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

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isEqualTo(123L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("FirstMock"),
                            () -> assertThat(st.getLastName()).isEqualTo("LastMock"),
                            () -> assertThat(st.getFullName()).isEqualTo("FirstMock LastMock"),
                            () -> assertThat(st.getCity()).isEqualTo("Volodymyr"),
                            () -> assertThat(st.getStreet()).isEqualTo("Zymnivska"),
                            () -> assertThat(st.getEmail()).isEqualTo("mock@gmail.com"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(1)
                                    .allSatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", 234L)
                                            .hasFieldOrPropertyWithValue("subjectName", "GraphQL")
                                            .hasFieldOrPropertyWithValue("marksObtained", 90.00)
                                    )
                    ));
        }

        @Test
        void getStudent_thoughDocWithParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("studentById")
                    .variable("studentId", 123L)
                    .execute();
            //then
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st).hasNoNullFieldsOrPropertiesExcept("student"),
                            () -> assertThat(st.getId()).isEqualTo(123L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("FirstMock"),
                            () -> assertThat(st.getLastName()).isEqualTo("LastMock"),
                            () -> assertThat(st.getFullName()).isEqualTo("FirstMock LastMock"),
                            () -> assertThat(st.getCity()).isEqualTo("Volodymyr"),
                            () -> assertThat(st.getStreet()).isEqualTo("Zymnivska"),
                            () -> assertThat(st.getEmail()).isEqualTo("mock@gmail.com")
                    ));
        }

    }

    @Nested
    class ResponseFlexibilityTests {

        @BeforeEach
        void setUp() {
            given(studentService.getStudentById(anyLong())).willReturn(mockStudent());
        }

        @AfterEach
        void tearDown() {
            then(studentService).should().getStudentById(eq(123L));
        }

        @Test
        void getStudent() {

            //given
            String studentQuery = "query{\n" +
                    "  student(id:123) {\n" +
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
            response.path("student")
                    .entity(StudentResponse.class)
                    .satisfies(st -> assertAll(
                            () -> assertThat(st.getId()).isEqualTo(123L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("FirstMock"),
                            () -> assertThat(st.getLastName()).isEqualTo("LastMock"),
                            () -> assertThat(st.getFullName()).isNull(),
                            () -> assertThat(st.getCity()).isNull(),
                            () -> assertThat(st.getStreet()).isNull(),
                            () -> assertThat(st.getEmail()).isNull()
                    ));
        }
    }

    private Student mockStudent() {
        Student mock = Student.builder()
                .id(123L)
                .firstName("FirstMock")
                .lastName("LastMock")
                .address(Address.builder()
                        .id(321L)
                        .city("Volodymyr")
                        .street("Zymnivska")
                        .build())
                .email("mock@gmail.com")
                .learningSubjects(List.of(
                        Subject.builder()
                                .id(234L)
                                .marksObtained(90.00)
                                .subjectName("GraphQL")
                                .build()
                ))
                .build();
        mock.getAddress().setStudent(mock);
        mock.getLearningSubjects().get(0).setStudent(mock);
        return mock;
    }

}