package com.github.ajharry69.demo.book;

import com.github.ajharry69.demo.TestcontainersConfiguration;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class BookControllerAPITest {

    @LocalServerPort
    int port;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.config = RestAssured.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("multipart/form-data", ContentType.MULTIPART));
    }

    @Test
    void shouldCreateBookAndReturnMaskedSensitiveFields() {
        // language=JSON
        var payload = """
                {
                  "title": "Clean Architecture",
                  "author": "Robert Martin",
                  "email": "uncle.bob@example.com",
                  "phoneNumber": "0712345678"
                }""";

        given()
                .contentType(ContentType.JSON)
                .accept("application/hal+json")
                .body(payload)
                .when()
                .post("/api/v1/books")
                .then()
                .statusCode(201)
                .body("title", equalTo("Clean Architecture"))
                .body("author", equalTo("Robert Martin"))
                .body("email", equalTo("u********@example.com"))
                .body("phoneNumber", equalTo("0*********"));
    }

    @Test
    void shouldUpdateAndDeleteBook() {
        var book = bookRepository.save(Book.builder()
                .title("T").author("A").email("a@test.com").phoneNumber("0700000000").build());
        var id = book.getId();

        // language=JSON
        var update = """
                {
                  "title": "New",
                  "author": "Auth2",
                  "email": "new@test.com",
                  "phoneNumber": "0711111111"
                }""";

        given().contentType(ContentType.JSON)
                .accept("application/hal+json")
                .body(update)
                .when().put("/api/v1/books/{id}", id)
                .then().statusCode(200)
                .body("title", equalTo("New"))
                .body("email", equalTo("n**@test.com"))
                .body("phoneNumber", equalTo("0*********"));

        given().when().delete("/api/v1/books/{id}", id).then().statusCode(204);

        given().when().get("/api/v1/books/{id}", id).then().statusCode(404);
    }
}
