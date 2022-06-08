package net.shyshkin.study.graphql.gettingstarted.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import net.shyshkin.study.graphql.gettingstarted.response.SubjectResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentResponseResolver implements GraphQLResolver<StudentResponse> {

    public List<SubjectResponse> getLearningSubjects(StudentResponse studentResponse) {
        long studentId = studentResponse.getId();
        throw new RuntimeException("NOt implemented yet");
    }

}
