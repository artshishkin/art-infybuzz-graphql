package net.shyshkin.study.graphql.jpa.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.shyshkin.study.graphql.jpa.entity.Student;

import java.util.List;

@Data
@NoArgsConstructor
public class StudentResponse {

	private long id;

	@JsonProperty("firstName")
	private String firstName;

	private String lastName;

	private String email;

	private String street;

	private String city;

	private List<SubjectResponse> learningSubjects;

	@ToString.Exclude
	private Student student;

	private String fullName;

}
