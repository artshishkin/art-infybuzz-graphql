package net.shyshkin.study.graphql.jpa.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.jpa.entity.Address;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.repository.StudentRepository;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Student createStudent(CreateStudentRequest createStudentRequest) {

        Student student = Student.builder()
                .firstName(createStudentRequest.getFirstName())
                .lastName(createStudentRequest.getLastName())
                .email(createStudentRequest.getEmail())
                .address(Address.builder()
                        .street(createStudentRequest.getStreet())
                        .city(createStudentRequest.getCity())
                        .build())
                .build();

        List<Subject> subjectsList = Optional.ofNullable(createStudentRequest.getSubjectsLearning())
                .stream()
                .flatMap(Collection::stream)
                .map(createSubjectRequest -> Subject.builder()
                        .subjectName(createSubjectRequest.getSubjectName())
                        .marksObtained(createSubjectRequest.getMarksObtained())
                        .student(student)
                        .build())
                .collect(Collectors.toList());

        student.setLearningSubjects(subjectsList);

        return studentRepository.save(student);
    }

}
