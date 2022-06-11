package net.shyshkin.study.graphql.clientlegacy.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import net.shyshkin.study.graphql.clientlegacy.service.ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public StudentResponse getStudent(@PathVariable Long id) {
        return clientService.getStudent(id);
    }

}
