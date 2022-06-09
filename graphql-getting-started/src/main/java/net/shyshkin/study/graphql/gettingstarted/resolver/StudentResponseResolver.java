package net.shyshkin.study.graphql.gettingstarted.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.entity.Subject;
import net.shyshkin.study.graphql.gettingstarted.filter.SubjectNameFilter;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import net.shyshkin.study.graphql.gettingstarted.response.SubjectResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentResponseResolver implements GraphQLResolver<StudentResponse> {

    public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse, SubjectNameFilter subjectNameFilter) {
        var learningSubjects = new ArrayList<SubjectResponse>();
        Student student = studentResponse.getStudent();
        if (student.getLearningSubjects() != null) {
            for (Subject subject : student.getLearningSubjects()) {
                if (subjectNameFilter == null ||
                        subjectNameFilter.name().equalsIgnoreCase(SubjectNameFilter.All.name()) ||
                        subjectNameFilter.name().equalsIgnoreCase(subject.getSubjectName()))
                    learningSubjects.add(new SubjectResponse(subject));
            }
        }
        return learningSubjects;
    }

    public String getFullName(StudentResponse studentResponse) {
        return studentResponse.getFirstName() + " " + studentResponse.getLastName();
    }

}
