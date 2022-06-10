package net.shyshkin.study.graphql.jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.mapper.AddressMapper;
import net.shyshkin.study.graphql.jpa.mapper.StudentMapper;
import net.shyshkin.study.graphql.jpa.mapper.SubjectMapper;
import net.shyshkin.study.graphql.jpa.repository.StudentRepository;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final AddressMapper addressMapper;
    private final SubjectMapper subjectMapper;

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Student createStudent(CreateStudentRequest createStudentRequest) {

        Student student = studentMapper.toEntity(createStudentRequest);

        student.getAddress().setStudent(student);

        List<Subject> subjectsList = Optional.ofNullable(createStudentRequest.getSubjectsLearning())
                .stream()
                .flatMap(Collection::stream)
                .map(subjectMapper::toEntity)
                .peek(subject -> subject.setStudent(student))
                .collect(Collectors.toList());

        student.setLearningSubjects(subjectsList);

        return studentRepository.save(student);
    }

}
