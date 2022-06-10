package net.shyshkin.study.graphql.jpa.mapper;

import net.shyshkin.study.graphql.jpa.entity.Address;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    Address toEntity(CreateStudentRequest request);

}
