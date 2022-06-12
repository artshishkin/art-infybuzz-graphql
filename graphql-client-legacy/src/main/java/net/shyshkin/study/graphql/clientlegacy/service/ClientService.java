package net.shyshkin.study.graphql.clientlegacy.service;

import graphql.kickstart.spring.webclient.boot.GraphQLRequest;
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

        String request = "query student($studentId: Long){\n" +
                "  student(id: $studentId) {\n" +
                "    id\n" +
                "    firstName\n" +
                "    lastName\n" +
                "    email\n" +
                "    street\n" +
                "    city\n" +
                "    learningSubjects(subjectNameFilters: [All]) {\n" +
                "      id\n" +
                "      subjectName\n" +
                "      marksObtained\n" +
                "    }\n" +
                "    fullName\n" +
                "  }\n" +
                "}";

        var graphQLRequest = GraphQLRequest.builder()
                .query(request)
                .variables(Map.of("studentId", id))
                .build();
        return client.post(graphQLRequest)
                .map(resp -> resp.get("student", StudentResponse.class))
                .block();
    }

    public StudentResponse createStudent(CreateStudentRequest createStudentRequest) {
        String request = "mutation createStudent($createStReq: CreateStudentRequest){\n" +
                "  createStudent(createStudentRequest: $createStReq) {\n" +
                "    id\n" +
                "    firstName\n" +
                "    lastName\n" +
                "    email\n" +
                "    street\n" +
                "    city\n" +
                "    learningSubjects(subjectNameFilters: [All]) {\n" +
                "      id\n" +
                "      subjectName\n" +
                "      marksObtained\n" +
                "    }\n" +
                "    fullName\n" +
                "  }\n" +
                "}";
        var graphQLRequest = GraphQLRequest.builder()
                .query(request)
                .variables(Map.of("createStReq", createStudentRequest))
                .build();
        return client.post(graphQLRequest)
                .map(resp -> resp.get("createStudent", StudentResponse.class))
                .block();
    }

}
