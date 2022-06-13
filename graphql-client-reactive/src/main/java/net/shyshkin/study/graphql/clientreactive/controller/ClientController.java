package net.shyshkin.study.graphql.clientreactive.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.clientreactive.request.CreateStudentRequest;
import net.shyshkin.study.graphql.clientreactive.response.StudentResponse;
import net.shyshkin.study.graphql.clientreactive.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public Mono<StudentResponse> getStudent(@PathVariable Long id) {
        return clientService.getStudent(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<StudentResponse> createStudent(@RequestBody CreateStudentRequest createStudentRequest){
        return clientService.createStudent(createStudentRequest);
    }

}
