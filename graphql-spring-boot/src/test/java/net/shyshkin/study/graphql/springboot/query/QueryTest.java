package net.shyshkin.study.graphql.springboot.query;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureGraphQlTester
@GraphQlTest
class QueryTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Nested
    class WithInlinedDocumentTests {

        @Test
        void firstQuery() {
            //given
            String document = "{firstQuery}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
        }

        @Test
        void secondQuery() {
            //given
            String document = "{\n" +
                    "secondQuery\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .pathDoesNotExist();
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }

        @Test
        void bothFirstAndSecondQueries() {

            //given
            String document = "{\n" +
                    "firstQuery\n" +
                    "secondQuery\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }
    }

    @Nested
    class WithExternalDocumentTests {

        @Test
        void firstQuery_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("firstQueryDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
        }

        @Test
        void secondQuery_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("secondQueryDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .pathDoesNotExist();
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }

        @Test
        void bothFirstAndSecondQueries_throughDocument() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("bothQueriesDoc")
                    .execute();

            //then
            response.path("firstQuery")
                    .entity(String.class)
                    .isEqualTo("First Query");
            response.path("secondQuery")
                    .entity(String.class)
                    .isEqualTo("Second Query");
        }
    }
}