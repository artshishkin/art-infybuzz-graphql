package net.shyshkin.study.graphql.jpa.mapper;

import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import org.mapstruct.Mapper;

@Mapper(uses = {AddressMapper.class, SubjectMapper.class})
public interface StudentMapper {

    Student toEntity(CreateStudentRequest request);

}
