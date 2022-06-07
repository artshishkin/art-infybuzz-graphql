package net.shyshkin.study.graphql.jpa.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import net.shyshkin.study.graphql.jpa.entity.Subject;

@Data
@NoArgsConstructor
public class SubjectResponse {

    private Long id;

    private String subjectName;

    private Double marksObtained;

    public SubjectResponse(Subject subject) {
        this.id = subject.getId();
        this.subjectName = subject.getSubjectName();
        this.marksObtained = subject.getMarksObtained();
    }
}
