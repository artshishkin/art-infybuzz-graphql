package net.shyshkin.study.graphql.jpa.mapper;

import net.shyshkin.study.graphql.jpa.entity.Subject;
import net.shyshkin.study.graphql.jpa.request.CreateSubjectRequest;
import org.mapstruct.Mapper;

@Mapper
public interface SubjectMapper {

    Subject toEntity(CreateSubjectRequest request);

}
