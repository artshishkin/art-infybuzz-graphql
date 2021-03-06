package net.shyshkin.study.graphql.gettingstarted.mutation;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.mapper.StudentMapper;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import net.shyshkin.study.graphql.gettingstarted.service.StudentService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mutation implements GraphQLMutationResolver {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentResponse createStudent(CreateStudentRequest createStudentRequest) {
        Student student = studentService.createStudent(createStudentRequest);
        return studentMapper.toDto(student);
    }

}
