package net.shyshkin.study.graphql.jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.mapper.StudentMapper;
import net.shyshkin.study.graphql.jpa.repository.StudentRepository;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Student createStudent(CreateStudentRequest createStudentRequest) {

        Student student = studentMapper.toEntity(createStudentRequest);

        return studentRepository.save(student);
    }

}
