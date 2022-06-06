package net.shyshkin.study.graphql.gettingstarted.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Order(10)
    void getAllStudents() {

        //when
        ResponseEntity<List<StudentResponse>> responseEntity = restTemplate.exchange("/api/student/getAll", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .hasSize(4)
                .allSatisfy(studentResponse -> assertThat(studentResponse).hasNoNullFieldsOrProperties());
    }

    @Test
    @Order(20)
    void getFirstNameById() {

        //given
        long studentId = 2L;

        //when
        var responseEntity = restTemplate.getForEntity("/api/student/getFirstNameById/{id}", String.class, studentId);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotEmpty()
                .isEqualTo("Virat")
                .satisfies(firstName -> log.debug("First Name: {}", firstName));
    }

    @Test
    @Order(30)
    void getLastNameById() {

        //given
        long studentId = 3L;

        //when
        var responseEntity = restTemplate.getForEntity("/api/student/getLastNameById/{id}", String.class, studentId);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotEmpty()
                .isEqualTo("Martin")
                .satisfies(firstName -> log.debug("Last Name: {}", firstName));
    }

    @Test
    @Order(40)
    void createStudent() {

        //given
        long studentId = 3L;
        CreateStudentRequest createStudentRequest = CreateStudentRequest
                .builder()
                .firstName("Art")
                .lastName("Shyshkin")
                .city("Volodymyr")
                .street("Zymnivska")
                .email("d.art.shishkin@gmail.com")
                .subjectsLearning(List.of())
                .build();


        //when
        var responseEntity = restTemplate.postForEntity("/api/student/create", createStudentRequest, StudentResponse.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("firstName", "Art")
                .hasFieldOrPropertyWithValue("email", "d.art.shishkin@gmail.com")
                .satisfies(studentResponse -> log.debug("Response: {}", studentResponse));
    }

}