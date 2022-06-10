package net.shyshkin.study.graphql.jpa.mapper;

import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {AddressMapper.class, SubjectMapper.class})
public interface StudentMapper {

    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "learningSubjects", ignore = true)
    @Mapping(target = "student", source = "student")
    StudentResponse toDto(Student student);

}
