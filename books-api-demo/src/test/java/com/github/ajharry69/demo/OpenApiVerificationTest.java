package com.github.ajharry69.demo;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class OpenApiVerificationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.config = RestAssured.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs("multipart/form-data", ContentType.MULTIPART));
    }

    @Test
    void shouldReturnOpenApiDocumentation() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .body(containsString("/api/v1/books"));
    }
}
