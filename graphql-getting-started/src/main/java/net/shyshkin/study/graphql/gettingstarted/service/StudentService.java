package net.shyshkin.study.graphql.gettingstarted.service;

import net.shyshkin.study.graphql.gettingstarted.entity.Address;
import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.entity.Subject;
import net.shyshkin.study.graphql.gettingstarted.repository.AddressRepository;
import net.shyshkin.study.graphql.gettingstarted.repository.StudentRepository;
import net.shyshkin.study.graphql.gettingstarted.repository.SubjectRepository;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import net.shyshkin.study.graphql.gettingstarted.request.CreateSubjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    SubjectRepository subjectRepository;

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Student createStudent (CreateStudentRequest createStudentRequest) {

        Student student = new Student(createStudentRequest);

        Address address = new Address();
        address.setStreet(createStudentRequest.getStreet());
        address.setCity(createStudentRequest.getCity());

        address = addressRepository.save(address);

        student.setAddress(address);
        student = studentRepository.save(student);

        List<Subject> subjectsList = new ArrayList<Subject>();

        if(createStudentRequest.getSubjectsLearning() != null) {
            for (CreateSubjectRequest createSubjectRequest :
                    createStudentRequest.getSubjectsLearning()) {
                Subject subject = new Subject();
                subject.setSubjectName(createSubjectRequest.getSubjectName());
                subject.setMarksObtained(createSubjectRequest.getMarksObtained());
                subject.setStudent(student);

                subjectsList.add(subject);
            }

            subjectRepository.saveAll(subjectsList);

        }

        student.setLearningSubjects(subjectsList);

        return student;
    }
}
