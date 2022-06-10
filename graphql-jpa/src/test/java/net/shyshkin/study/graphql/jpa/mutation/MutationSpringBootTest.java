package net.shyshkin.study.graphql.jpa.mutation;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
class MutationSpringBootTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Nested
    class CreateStudentMutationTests {

        @Test
        void createStudent() {

            //given
            String createStudentMutation = "mutation{\n" +
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

            //when
            GraphQlTester.Response response = graphQlTester.document(createStudentMutation)
                    .execute();

            //then
            response.path("createStudent")
                    .entity(StudentResponse.class)
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
    }
}