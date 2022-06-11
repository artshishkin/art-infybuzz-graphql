package net.shyshkin.study.graphql.gettingstarted.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;

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

	//This is for internal use. DO NOT PUT IN GRAPHQL SCHEMA
	private Student student;

	private List<SubjectResponse> learningSubjects;

	private String fullName;

}
