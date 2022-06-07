package net.shyshkin.study.graphql.jpa.repository;

import net.shyshkin.study.graphql.jpa.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

}
