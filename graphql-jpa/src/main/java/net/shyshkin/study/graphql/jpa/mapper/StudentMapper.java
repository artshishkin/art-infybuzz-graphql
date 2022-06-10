package net.shyshkin.study.graphql.jpa.mapper;

import net.shyshkin.study.graphql.jpa.entity.Student;
import net.shyshkin.study.graphql.jpa.request.CreateStudentRequest;
import net.shyshkin.study.graphql.jpa.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = SubjectMapper.class)
public interface StudentMapper {

    @Mapping(target = "address", source = "request")
    @Mapping(target = "learningSubjects", source = "subjectsLearning")
    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = ".", source = "address")  //[3.5. Mapping nested bean properties to current target](https://mapstruct.org/documentation/stable/reference/html/#mapping-nested-bean-properties-to-current-target)
    @Mapping(target = "learningSubjects", ignore = true)
    @Mapping(target = "student", source = "student")
    StudentResponse toDto(Student student);

}
