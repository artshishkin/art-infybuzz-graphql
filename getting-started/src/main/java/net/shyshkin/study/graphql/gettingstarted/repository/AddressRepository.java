package net.shyshkin.study.graphql.gettingstarted.repository;

import net.shyshkin.study.graphql.gettingstarted.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
