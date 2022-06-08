package net.shyshkin.study.graphql.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"drop table subject", "drop table student", "drop table address"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GraphQLJpaApplicationTest {

    @Test
    void contextLoads() {
    }
}