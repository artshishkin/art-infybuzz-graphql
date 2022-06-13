package net.shyshkin.study.graphql.clientreactive.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectResponse {

    private Long id;

    private String subjectName;

    private Double marksObtained;

}
