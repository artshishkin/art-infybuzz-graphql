package net.shyshkin.study.graphql.gettingstarted.mapper;

import net.shyshkin.study.graphql.gettingstarted.entity.Subject;
import net.shyshkin.study.graphql.gettingstarted.request.CreateSubjectRequest;
import net.shyshkin.study.graphql.gettingstarted.response.SubjectResponse;
import org.mapstruct.Mapper;

@Mapper
public interface SubjectMapper {

    Subject toEntity(CreateSubjectRequest request);

    SubjectResponse toDto(Subject subject);

}
