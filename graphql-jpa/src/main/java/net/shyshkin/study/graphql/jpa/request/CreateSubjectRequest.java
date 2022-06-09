package net.shyshkin.study.graphql.jpa.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubjectRequest {

	private String subjectName;
	private Double marksObtained;
}
