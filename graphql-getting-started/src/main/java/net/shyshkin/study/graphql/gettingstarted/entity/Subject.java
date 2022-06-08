package net.shyshkin.study.graphql.gettingstarted.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "subject")
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;
	
	@Column(name = "subject_name")
	private String subjectName;
	
	@Column(name = "marks_obtained")
	private Double marksObtained;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;
}
