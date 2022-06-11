package net.shyshkin.study.graphql.gettingstarted.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.entity.Subject;
import net.shyshkin.study.graphql.gettingstarted.filter.SubjectNameFilter;
import net.shyshkin.study.graphql.gettingstarted.mapper.SubjectMapper;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import net.shyshkin.study.graphql.gettingstarted.response.SubjectResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StudentResponseResolver implements GraphQLResolver<StudentResponse> {

    private final SubjectMapper subjectMapper;

    public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, List<SubjectNameFilter> subjectNameFilters) {
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
                    learningSubjects.add(subjectMapper.toDto(subject));
            }
        }
        return learningSubjects;
    }

    public String getFullName(StudentResponse studentResponse) {
        return studentResponse.getFirstName() + " " + studentResponse.getLastName();
    }

}
