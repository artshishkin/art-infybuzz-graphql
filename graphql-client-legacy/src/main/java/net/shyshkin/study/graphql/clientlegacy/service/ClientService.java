package net.shyshkin.study.graphql.clientlegacy.service;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.clientlegacy.request.CreateStudentRequest;
import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final GraphQLWebClient client;

    public StudentResponse getStudent(Long id) {
        return client
                .post("get_student.graphql", Map.of("studentId", id), StudentResponse.class)
                .block();
    }

    public StudentResponse createStudent(CreateStudentRequest createStudentRequest) {
        return client
                .post("create_student.graphql", Map.of("createStReq", createStudentRequest), StudentResponse.class)
                .block();
    }

}
