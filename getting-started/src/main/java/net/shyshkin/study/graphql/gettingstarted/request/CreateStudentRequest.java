package net.shyshkin.study.graphql.gettingstarted.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    private String email;

    private String street;

    private String city;

    private List<CreateSubjectRequest> subjectsLearning;

}
