package net.shyshkin.study.graphql.clientlegacy.service;

import net.shyshkin.study.graphql.clientlegacy.response.StudentResponse;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    public StudentResponse getStudent(Long id){
        throw new RuntimeException("Not Implemented Yet");
    }

}
