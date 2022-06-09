package net.shyshkin.study.graphql.gettingstarted.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String street;
    private String city;
    private List<CreateSubjectRequest> subjectsLearning;

}
