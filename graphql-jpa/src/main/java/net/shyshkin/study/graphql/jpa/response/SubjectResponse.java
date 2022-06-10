package net.shyshkin.study.graphql.jpa.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectResponse {

    private Long id;

    private String subjectName;

    private Double marksObtained;

}
