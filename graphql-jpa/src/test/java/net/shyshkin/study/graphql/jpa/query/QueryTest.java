package net.shyshkin.study.graphql.jpa.query;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Nested
    class WithInputDataTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{fullName(firstName:\"Art\",lastName:\"Shyshkin\")}";

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDoc() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullName")
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Kate Shyshkina");
        }

        @Test
        void fullName_thoughDocWithParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameParam")
                    .variable("firstName", "Nazar")
                    .variable("lastName", "Shyshkin")
                    .execute();
            //then
            response.path("fullName")
                    .entity(String.class)
                    .isEqualTo("Nazar Shyshkin");
        }
    }

    @Nested
    class WithInputJsonDataTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{\n" +
                    "fullNameJson(sampleRequest:{\n" +
                    "    firstName:\"Art\"\n" +
                    "    lastName:\"Shyshkin\"\n" +
                    "  })\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDoc() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJson")
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Art Shyshkin");
        }

        @Test
        void fullName_thoughDocWithParam() {

            //given
            var sampleRequest = Map.of(
                    "firstName", "Arina",
                    "lastName", "Shyshkina"
            );

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJsonParam")
                    .variable("sampleRequest", sampleRequest)
                    .execute();
            //then
            response.path("fullNameJson")
                    .entity(String.class)
                    .isEqualTo("Arina Shyshkina");
        }
    }

    @Nested
    class SchemaValidationTests {
        @Test
        void fullName() {

            //given
            String fullNameQuery = "{\n" +
                    "fullNameJson(sampleRequest:{\n" +
                    "    lastName:\"Shyshkin\"\n" +
                    "  })\n" +
                    "}";

            //when
            GraphQlTester.Response response = graphQlTester.document(fullNameQuery)
                    .execute();
            //then
            response.errors()
                    .satisfy(errors -> assertThat(errors)
                                    .hasSize(1)
                                    .allSatisfy(error -> assertAll(
//                                    () -> assertThat(error).isInstanceOf(ValidationError.class),
                                                    () -> assertThat(error.getMessage().toLowerCase()).contains("firstname", "valid"))
                                    )
                    );
        }

        @Test
        void fullName_thoughDocWithParam() {

            //given
            var sampleRequest = Map.of(
//                    "firstName", "Arina",
                    "lastName", "Shyshkina"
            );

            //when
            GraphQlTester.Response response = graphQlTester.documentName("fullNameJsonParam")
                    .variable("sampleRequest", sampleRequest)
                    .execute();
            //then
            response.errors()
                    .satisfy(errors -> assertThat(errors)
                                    .hasSize(1)
                                    .allSatisfy(error -> assertAll(
//                                    () -> assertThat(error).isInstanceOf(ValidationError.class),
                                                    () -> assertThat(error.getMessage().toLowerCase()).contains("firstname", "valid"))
                                    )
                    );
        }
    }

}