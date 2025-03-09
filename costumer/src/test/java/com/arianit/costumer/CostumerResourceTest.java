package com.arianit.costumer;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CostumerResourceTest {

    private static Integer costumerId;

    @Test
    @DisplayName("Get all costumers")
    @Order(0)
    void getAllCostumer() {
        given()
                .when()
                .get("api/v1/costumers")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Create new costumer")
    @Order(1)
    void createCostumer() {
        costumerId = Integer.valueOf(given()
                .contentType("application/json")
                .body("{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"phone\": \"123456789\", \"address\": \"123 Main St\", \"age\": 30}")
                .when()
                .post("api/v1/costumers")
                .then()
                .statusCode(201)
                .extract().header("Location")
                .replaceAll("http://localhost:8081/api/v1/costumers/", ""));
    }

    @Test
    @DisplayName("Get costumer by ID")
    @Order(2)
    void getCostumerById() {
        given()
                .when()
                .get("api/v1/costumers/{id}", costumerId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Update existing costumer")
    @Order(3)
    void updateCostumer() {
        String bodyStr = String.format("{\"id\": %d, \"name\": \"Jane Doe\", \"email\": \"jane.doe@example.com\", \"phone\": \"987654321\", \"address\": \"456 Elm St\", \"age\": 25}", costumerId);
        given()
                .contentType("application/json")
                .body(bodyStr)
                .when()
                .put("api/v1/costumers")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Get updated costumer")
    @Order(4)
    void getUpdatedCostumer() {
        given()
                .when()
                .get("api/v1/costumers/{id}", costumerId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Jane Doe"));
    }

    @Test
    @DisplayName("Delete existing costumer")
    @Order(5)
    void deleteExistingCostumer() {
        given()
                .when()
                .delete("api/v1/costumers/{id}", costumerId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Get non-existing costumer")
    @Order(6)
    void getNonExistingCostumer() {
        given()
                .when()
                .get("api/v1/costumers/{id}", 100)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete non-existing costumer")
    @Order(7)
    void deleteNonExistingCostumer() {
        given()
                .when()
                .delete("api/v1/costumers/{id}", 100)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Update non-existing costumer")
    @Order(8)
    void updateNonExistingCostumer() {
        given()
                .contentType("application/json")
                .body("{\"id\": 999, \"name\": \"Jane Doe\", \"email\": \"jane.doe@example.com\", \"phone\": \"987654321\", \"address\": \"456 Elm St\", \"age\": 25}")
                .when()
                .put("api/v1/costumers")
                .then()
                .statusCode(404);
    }

}