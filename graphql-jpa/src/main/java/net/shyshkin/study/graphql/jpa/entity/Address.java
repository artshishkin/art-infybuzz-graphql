package net.shyshkin.study.graphql.jpa.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;

	@Column(name = "street")
	private String street;

	@Column(name = "city")
	private String city;

	@ToString.Exclude
	@OneToOne(mappedBy = "address")
	private Student student;

}
