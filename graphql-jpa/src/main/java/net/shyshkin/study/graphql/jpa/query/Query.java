package net.shyshkin.study.graphql.jpa.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.filter.SubjectNameFilter;
import net.shyshkin.study.graphql.jpa.request.SampleRequest;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import net.shyshkin.study.graphql.jpa.response.SubjectResponse;
import net.shyshkin.study.graphql.jpa.service.StudentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
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

    @SchemaMapping(typeName = "StudentResponse", field = "learningSubjects")
    public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, @Argument List<SubjectNameFilter> subjectNameFilters) {
        log.debug("getLearningSubjects for {}", studentResponse);
        var learningSubjects = new ArrayList<SubjectResponse>();
        Student student = studentResponse.getStudent();

        List<String> filterNameList = Stream
                .ofNullable(subjectNameFilters)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(SubjectNameFilter::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        boolean filterIsOff = subjectNameFilters == null ||
                filterNameList.contains(SubjectNameFilter.All.name().toLowerCase());

        if (student.getLearningSubjects() != null) {
            for (Subject subject : student.getLearningSubjects()) {
                if (filterIsOff || filterNameList.contains(subject.getSubjectName().toLowerCase()))
                    learningSubjects.add(new SubjectResponse(subject));
            }
        }
        return learningSubjects;
    }

    @SchemaMapping(typeName = "StudentResponse", field = "fullName")
    public String resolveFullName(StudentResponse response) {
        if (response == null) return null;
        return response.getFirstName() + " " + response.getLastName();
    }

}
