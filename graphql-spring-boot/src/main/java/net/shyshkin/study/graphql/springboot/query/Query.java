package net.shyshkin.study.graphql.springboot.query;

import net.shyshkin.study.graphql.springboot.request.SampleRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Query {

    @QueryMapping
    public String firstQuery() {
        return "First Query";
    }

    @QueryMapping
    public String secondQuery() {
        return "Second Query";
    }

    @QueryMapping("fullName")
    public String fullNameQuery(@Argument String firstName, @Argument String lastName) {
        return firstName + " " + lastName;
    }

    @QueryMapping
    public String fullNameJson(@Argument SampleRequest sampleRequest) {
        return sampleRequest.getFirstName() + " " + sampleRequest.getLastName();
    }
}
