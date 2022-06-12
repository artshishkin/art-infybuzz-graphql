package net.shyshkin.study.graphql.clientlegacy.service;

import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.clientlegacy.request.CreateStudentRequest;
import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final GraphQLWebClient client;

    @Value("${app.resource.query.get-student:get_student.graphql}")
    private String getStudentResource;

    @Value("${app.resource.mutation.create-student:create_student.graphql}")
    private String createStudentResource;

    public StudentResponse getStudent(Long id) {
        return client
                .post(getStudentResource, Map.of("studentId", id), StudentResponse.class)
                .block();
    }

    public StudentResponse createStudent(CreateStudentRequest createStudentRequest) {
        return client
                .post(createStudentResource, Map.of("createStReq", createStudentRequest), StudentResponse.class)
                .block();
    }

}
