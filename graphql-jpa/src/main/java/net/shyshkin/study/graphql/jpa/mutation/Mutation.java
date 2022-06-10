package net.shyshkin.study.graphql.jpa.mutation;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.mapper.StudentMapper;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import net.shyshkin.study.graphql.jpa.service.StudentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class Mutation {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @MutationMapping("createStudent")
    public StudentResponse createStudent(@Argument CreateStudentRequest createStudentRequest) {
        Student student = studentService.createStudent(createStudentRequest);
        return studentMapper.toDto(student);
    }

}
