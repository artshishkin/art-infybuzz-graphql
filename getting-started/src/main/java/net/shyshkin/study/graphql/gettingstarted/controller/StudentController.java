package net.shyshkin.study.graphql.gettingstarted.controller;

import net.shyshkin.study.graphql.gettingstarted.entity.Student;
import net.shyshkin.study.graphql.gettingstarted.request.CreateStudentRequest;
import net.shyshkin.study.graphql.gettingstarted.response.StudentResponse;
import net.shyshkin.study.graphql.gettingstarted.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/student/")
public class StudentController {

	@Autowired
	StudentService studentService;
	
	@GetMapping("getAll")
	public List<StudentResponse> getAllStudents () {
		List<Student> studentList = studentService.getAllStudents();
		List<StudentResponse> studentResponseList = new ArrayList<StudentResponse>();
		
		studentList.stream().forEach(student -> {
			studentResponseList.add(new StudentResponse(student));
		});
		
		return studentResponseList;
	}
	
	@GetMapping("getFirstNameById/{id}")
	public String getFirstNameById (@PathVariable long id) {
		return studentService.getFirstNameById(id);
	}
	
	@GetMapping("getLastNameById/{id}")
	public String getLastNameById (@PathVariable long id) {
		return studentService.getLastNameById(id);
	}
	
	@PostMapping("create")
	public StudentResponse createStudent (@Valid @RequestBody CreateStudentRequest createStudentRequest) {
		Student student = studentService.createStudent(createStudentRequest);
		
		return new StudentResponse(student);
	}
}
