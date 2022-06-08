package net.shyshkin.study.graphql.jpa.query;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.request.SampleRequest;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import net.shyshkin.study.graphql.jpa.service.StudentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class Query {

    private final StudentService studentService;

    @QueryMapping
    public String firstQuery() {
        return "First Query";
    }

    @QueryMapping
    public String secondQuery() {
        return "Second Query";
    }

    @QueryMapping("fullName")
    public String fullNameQuery(@Argument String firstName, @Argument String lastName) {
        return firstName + " " + lastName;
    }

    @QueryMapping
    public String fullNameJson(@Argument SampleRequest sampleRequest) {
        return sampleRequest.getFirstName() + " " + sampleRequest.getLastName();
    }

    @QueryMapping
    public StudentResponse student(@Argument long id) {
        Student student = studentService.getStudentById(id);
        return new StudentResponse(student);
    }
}
