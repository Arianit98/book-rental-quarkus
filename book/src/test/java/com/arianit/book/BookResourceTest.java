package com.arianit.book;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class BookResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/api/v1/books")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}