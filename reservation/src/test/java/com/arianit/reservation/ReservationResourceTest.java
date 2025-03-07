package com.arianit.reservation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ReservationResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/api/v1/reservations")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}