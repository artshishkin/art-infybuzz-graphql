package net.shyshkin.study.graphql.restapijpa.repository;

import net.shyshkin.study.graphql.restapijpa.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
