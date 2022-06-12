package net.shyshkin.study.graphql.clientlegacy.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.clientlegacy.request.CreateStudentRequest;
import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import net.shyshkin.study.graphql.clientlegacy.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public StudentResponse getStudent(@PathVariable Long id) {
        return clientService.getStudent(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public StudentResponse createStudent(@RequestBody CreateStudentRequest createStudentRequest){
        return clientService.createStudent(createStudentRequest);
    }

}
