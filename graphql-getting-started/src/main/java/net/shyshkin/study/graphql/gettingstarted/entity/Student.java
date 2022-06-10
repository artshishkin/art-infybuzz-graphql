package net.shyshkin.study.graphql.gettingstarted.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Subject> learningSubjects;

    public Student(Long id, String firstName, String lastName, String email, Address address, List<Subject> learningSubjects) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setAddress(address);
        setLearningSubjects(learningSubjects);
    }

    public void setAddress(Address address) {
        this.address = address;
        if (address != null && address.getStudent() != this)
            address.setStudent(this);
    }

    public void setLearningSubjects(List<Subject> learningSubjects) {
        this.learningSubjects = learningSubjects;
        if (learningSubjects != null) {
            learningSubjects.forEach(subject -> subject.setStudent(this));
        }
    }
}
