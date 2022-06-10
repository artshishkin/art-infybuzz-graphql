package net.shyshkin.study.graphql.gettingstarted.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.mapper.StudentMapper;
import net.shyshkin.study.graphql.gettingstarted.repository.StudentRepository;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

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
