package com.arianit.reservation;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;

@QuarkusTest
@ConnectWireMock
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationResourceTest {

    private static Integer reservationId;
    private static WireMockServer bookServiceMock;
    private static WireMockServer costumerServiceMock;

    @BeforeAll
    static void setup() {
        bookServiceMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        costumerServiceMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        bookServiceMock.start();
        costumerServiceMock.start();

        System.setProperty("quarkus.rest-client.book-api.url", "http://localhost:" + bookServiceMock.port());
        System.setProperty("quarkus.rest-client.costumer-api.url", "http://localhost:" + costumerServiceMock.port());
    }

    @AfterAll
    static void tearDown() {
        bookServiceMock.stop();
        costumerServiceMock.stop();
    }

    @Test
    @DisplayName("Get all reservations")
    @Order(0)
    void getAllReservations() {
        given()
                .when()
                .get("api/v1/reservations")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Create new reservation")
    @Order(1)
    void createReservation() {

        costumerServiceMock.stubFor(get("/api/v1/costumers/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"John Doe\", \"email\": \" \"}")));

        bookServiceMock.stubFor(get("/api/v1/books/1/checkAvailability")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                ));

        bookServiceMock.stubFor(get("/api/v1/books/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Libri Titull\", \"author\": \"test author\", \"year\": 2011, \"stockNr\": 10, \"reservedNr\": 0}")));

        bookServiceMock.stubFor(put("/api/v1/books")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Libri Titull\", \"author\": \"test author\", \"year\": 2011, \"stockNr\": 10, \"reservedNr\": 1}")));

        reservationId = Integer.valueOf(given()
                .contentType("application/json")
                .body("{\"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(201)
                .extract().header("Location")
                .replaceAll("http://localhost:8081/api/v1/reservations/", ""));
    }

    @Test
    @DisplayName("Get reservation by ID")
    @Order(2)
    void getReservationById() {
        when()
                .get("api/v1/reservations/" + reservationId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Update reservation")
    @Order(3)
    void updateReservation() {
        costumerServiceMock.stubFor(get("/api/v1/costumers/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"John Doe\", \"email\": \" \"}")
                ));

        bookServiceMock.stubFor(get("/api/v1/books/1/checkAvailability")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                ));

        bookServiceMock.stubFor(get("/api/v1/books/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Book Title\", \"author\": \"Author Name\", \"reservedNr\": 0, \"stockNr\": 1, \"year\": 2021}")
                ));

        bookServiceMock.stubFor(put("/api/v1/books")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Book Title\", \"author\": \"Author Name\", \"reservedNr\": 1, \"stockNr\": 1, \"year\": 2021}")
                ));

        String bodyStr = String.format("{\"id\": %d, \"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}", reservationId);

        given()
                .contentType("application/json")
                .body(bodyStr)
                .when()
                .put("api/v1/reservations")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Delete reservation")
    @Order(4)
    void deleteReservation() {
        bookServiceMock.stubFor(get("/api/v1/books/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Book Title\", \"author\": \"Author Name\", \"reservedNr\": 1, \"stockNr\": 1, \"year\": 2021}")
                ));

        bookServiceMock.stubFor(put("/api/v1/books")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"title\": \"Book Title\", \"author\": \"Author Name\", \"reservedNr\": 0, \"stockNr\": 1, \"year\": 2021}")
                ));

        when()
                .delete("api/v1/reservations/" + reservationId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Create new reservation with invalid customer")
    @Order(5)
    void createReservationWithInvalidCustomer() {
        costumerServiceMock.stubFor(get("/api/v1/costumers/100")
                .willReturn(aResponse()
                        .withStatus(404)
                ));

        bookServiceMock.stubFor(get("/api/v1/books/1/checkAvailability")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                ));

        given()
                .contentType("application/json")
                .body("{\"costumerId\": 100, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create new reservation with invalid book")
    @Order(6)
    void createReservationWithInvalidBook() {
        costumerServiceMock.stubFor(get("/api/v1/costumers/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"John Doe\", \"email\": \" \"}")
                ));

        bookServiceMock.stubFor(get("/api/v1/books/100/checkAvailability")
                .willReturn(aResponse()
                        .withStatus(204)
                ));

        given()
                .contentType("application/json")
                .body("{\"costumerId\": 1, \"bookId\": 100, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Get non-existing reservation")
    @Order(7)
    void getNonExistingReservation() {
        given()
                .when()
                .get("api/v1/reservations/100")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete non-existing reservation")
    @Order(8)
    void deleteNonExistingReservation() {
        given()
                .when()
                .delete("api/v1/reservations/100")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Update non-existing reservation")
    @Order(9)
    void updateNonExistingReservation() {
        given()
                .contentType("application/json")
                .body("{\"id\": 100, \"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .put("api/v1/reservations")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create new reservation with invalid book and customer")
    @Order(10)
    void createReservationWithInvalidBookAndCustomer() {
        costumerServiceMock.stubFor(get("/api/v1/costumers/100")
                .willReturn(aResponse()
                        .withStatus(404)
                ));

        bookServiceMock.stubFor(get("/api/v1/books/100/checkAvailability")
                .willReturn(aResponse()
                        .withStatus(204)
                ));

        given()
                .contentType("application/json")
                .body("{\"costumerId\": 100, \"bookId\": 100}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(400);
    }

}