package net.shyshkin.study.graphql.jpa.mutation;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.config.ScalarConfig;
import net.shyshkin.study.graphql.jpa.entity.Address;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.mapper.StudentMapperImpl;
import net.shyshkin.study.graphql.jpa.mapper.SubjectMapperImpl;
import net.shyshkin.study.graphql.jpa.query.Query;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@GraphQlTest
@ContextConfiguration(classes = {ScalarConfig.class, Query.class, Mutation.class, StudentMapperImpl.class, SubjectMapperImpl.class})
class MutationMockTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    StudentService studentService;

    @Nested
    class CreateStudentMutationTests {

        @BeforeEach
        void setUp() {
            given(studentService.createStudent(any(CreateStudentRequest.class))).willReturn(mockStudent());
        }

        @AfterEach
        void tearDown() {
            then(studentService).should().createStudent(any(CreateStudentRequest.class));
        }

        @Test
        void createStudent() {

            //given
            String createStudentMutation = "mutation{\n" +
                    "  createStudent(createStudentRequest:{\n" +
                    "    firstName:\"FirstMock\"\n" +
                    "    lastName:\"LastMock\"\n" +
                    "    email:\"mock@gmail.com\"\n" +
                    "    street:\"Zymnivska\"\n" +
                    "    city:\"Volodymyr\"\n" +
                    "    subjectsLearning:[\n" +
                    "      {\n" +
                    "        subjectName:\"Java\"\n" +
                    "        marksObtained:90.0\n" +
                    "      },\n" +
                    "      {\n" +
                    "        subjectName:\"MySQL\"\n" +
                    "        marksObtained:75\n" +
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
                            () -> assertThat(st.getId()).isEqualTo(123L),
                            () -> log.debug("{}", st),
                            () -> assertThat(st.getFirstName()).isEqualTo("FirstMock"),
                            () -> assertThat(st.getLastName()).isEqualTo("LastMock"),
                            () -> assertThat(st.getFullName()).isEqualTo("FirstMock LastMock"),
                            () -> assertThat(st.getCity()).isEqualTo("Volodymyr"),
                            () -> assertThat(st.getStreet()).isEqualTo("Zymnivska"),
                            () -> assertThat(st.getEmail()).isEqualTo("mock@gmail.com"),
                            () -> assertThat(st.getLearningSubjects())
                                    .hasSize(2)
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", 234L)
                                            .hasFieldOrPropertyWithValue("subjectName", "Java")
                                            .hasFieldOrPropertyWithValue("marksObtained", 90.00)
                                    )
                                    .anySatisfy(subResp -> assertThat(subResp)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", 235L)
                                            .hasFieldOrPropertyWithValue("subjectName", "MySQL")
                                            .hasFieldOrPropertyWithValue("marksObtained", 75.00)
                                    )
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
                                .subjectName("Java")
                                .build(),
                        Subject.builder()
                                .id(235L)
                                .marksObtained(75.00)
                                .subjectName("MySQL")
                                .build()
                ))
                .build();
        mock.getAddress().setStudent(mock);
        mock.getLearningSubjects().get(0).setStudent(mock);
        return mock;
    }
}