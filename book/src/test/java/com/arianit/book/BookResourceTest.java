package com.arianit.book;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookResourceTest {

    private static Integer bookId;

    @Test
    @DisplayName("Get all books")
    @Order(0)
    void getAllBooks() {
        given()
                .when()
                .get("api/v1/books")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Create new book")
    @Order(1)
    void createBook() {
        bookId = Integer.valueOf(given()
                .contentType("application/json")
                .body("{\"title\": \"The Great Gatsby\", \"author\": \"F. Scott Fitzgerald\", \"stockNr\": 10, \"year\": 1925, \"reservedNr\": 0}")
                .when()
                .post("api/v1/books")
                .then()
                .statusCode(201)
                .extract().header("Location")
                .replace("http://localhost:8081/api/v1/books/", ""));
    }

    @Test
    @DisplayName("Get book by ID")
    @Order(2)
    void getBookById() {
        given()
                .when()
                .get("api/v1/books/" + bookId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Update book")
    @Order(3)
    void updateBook() {
        String bodyStr = String.format("{\"id\": %d, \"title\": \"The Great Gatsby\", \"author\": \"F. Scott Fitzgerald\", \"stockNr\": 10, \"year\": 1925, \"reservedNr\": 0}", bookId);
        given()
                .contentType("application/json")
                .body(bodyStr)
                .when()
                .put("api/v1/books")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check book availability")
    @Order(3)
    void checkBookAvailability() {
        given()
                .when()
                .get("api/v1/books/" + bookId + "/checkAvailability")
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

    @Test
    @DisplayName("Delete book")
    @Order(4)
    void deleteBook() {
        given()
                .when()
                .delete("api/v1/books/" + bookId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Get deleted book")
    @Order(5)
    void getDeletedBook() {
        given()
                .when()
                .get("api/v1/books/" + bookId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Get non-existing book")
    @Order(6)
    void getNonExistingBook() {
        given()
                .when()
                .get("api/v1/books/100")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete non-existing book")
    @Order(7)
    void deleteNonExistingBook() {
        given()
                .when()
                .delete("api/v1/books/100")
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Update non-existing book")
    @Order(8)
    void updateNonExistingBook() {

        given()
                .contentType("application/json")
                .body("{\"id\": 999,\"title\": \"The Great Gatsby\", \"author\": \"F. Scott Fitzgerald\", \"stockNr\": 10, \"year\": 1925, \"reservedNr\": 0}")
                .when()
                .put("api/v1/books")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Check non-existing book availability")
    @Order(9)
    void checkNonExistingBookAvailability() {
        given()
                .when()
                .get("api/v1/books/200/checkAvailability")
                .then()
                .statusCode(200)
                .body(equalTo("false"));
    }

}