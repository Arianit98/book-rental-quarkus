package com.arianit.reservation;

import com.arianit.reservation.client.Book;
import com.arianit.reservation.client.BookClient;
import com.arianit.reservation.client.CostumerClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationResourceTest {

    private static Integer reservationId;

    @InjectMock
    @RestClient
    @MockitoConfig(convertScopes = true)
    BookClient bookClient;

    @InjectMock
    @RestClient
    @MockitoConfig(convertScopes = true)
    CostumerClient costumerClient;

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
        Book book = new Book();
        book.setId(1L);
        book.setReservedNr(0);
        Mockito.when(bookClient.getBookById(1L)).thenReturn(RestResponse.ok(book));
        Mockito.when(costumerClient.getCostumer(1L)).thenReturn(RestResponse.ok());
        Mockito.when(bookClient.checkAvailability(1L)).thenReturn(RestResponse.ok(true));

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
        Book book = new Book();
        book.setId(1L);
        book.setReservedNr(0);
        Mockito.when(bookClient.getBookById(1L)).thenReturn(RestResponse.ok(book));
        Mockito.when(costumerClient.getCostumer(1L)).thenReturn(RestResponse.ok());
        Mockito.when(bookClient.checkAvailability(1L)).thenReturn(RestResponse.ok(true));

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
        Book book = new Book();
        book.setId(1L);
        book.setReservedNr(0);
        Mockito.when(bookClient.getBookById(1L)).thenReturn(RestResponse.ok(book));
        Mockito.when(bookClient.updateBook(book)).thenReturn(RestResponse.ok(book));
        when()
                .delete("api/v1/reservations/" + reservationId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Create new reservation with invalid customer")
    @Order(5)
    void createReservationWithInvalidCustomer() {
        Mockito.when(costumerClient.getCostumer(1L)).thenReturn(RestResponse.notFound());
        given()
                .contentType("application/json")
                .body("{\"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create new reservation with invalid book")
    @Order(6)
    void createReservationWithInvalidBook() {
        Mockito.when(costumerClient.getCostumer(1L)).thenReturn(RestResponse.ok());
        Mockito.when(bookClient.checkAvailability(1L)).thenReturn(RestResponse.ok(false));
        given()
                .contentType("application/json")
                .body("{\"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
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
        Mockito.when(costumerClient.getCostumer(1L)).thenReturn(RestResponse.notFound());
        Mockito.when(bookClient.getBookById(1L)).thenReturn(RestResponse.noContent());
        given()
                .contentType("application/json")
                .body("{\"costumerId\": 1, \"bookId\": 1, \"durationInDays\": 7, \"createdDate\": \"2021-10-01\"}")
                .when()
                .post("api/v1/reservations")
                .then()
                .statusCode(400);
    }

}