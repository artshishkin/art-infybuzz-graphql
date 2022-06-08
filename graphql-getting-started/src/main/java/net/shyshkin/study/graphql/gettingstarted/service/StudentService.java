package net.shyshkin.study.graphql.gettingstarted.service;

import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
