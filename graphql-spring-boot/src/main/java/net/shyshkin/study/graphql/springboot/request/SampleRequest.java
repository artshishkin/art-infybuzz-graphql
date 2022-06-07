package net.shyshkin.study.graphql.springboot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleRequest {

    private String firstName;
    private String lastName;

}
