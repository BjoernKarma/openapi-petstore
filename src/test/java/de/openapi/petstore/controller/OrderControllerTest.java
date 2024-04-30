package de.openapi.petstore.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import de.openapi.petstore.auth.SessionHelper;
import de.openapi.petstore.model.Order;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testMongo")
class OrderControllerTest {

  private RequestSpecification spec;

  @Value("${local.server.port}")
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Test
  void shouldDeleteOrder() {
    var orderId = 1;
    given()
        .when()
        .delete("/api/petstore/v1/store/order/" + orderId)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .header("Content-Length", is("0"));
  }

  @Test
  void shouldReturnInventory() {
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/api/petstore/v1/store/inventory")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("approved", is(50))
        .body("placed", is(200))
        .body("delivered", is(50))
        .body(matchesJsonSchemaInClasspath("schemata/inventory-schema.json"));
  }

  @Test
  void shouldReturnOrder() {
    var orderId = 1;
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/api/petstore/v1/store/order/" + orderId)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("id", is(orderId))
        .body("petId", is(1))
        .body("quantity", is(100))
        .body("complete", is(true))
        .body("shipDate", notNullValue())
        .body("status", is(Order.StatusEnum.PLACED.getValue()))
        .body(matchesJsonSchemaInClasspath("schemata/order-schema.json"));
  }

  @Test
  void shouldPlaceOrder() {
    var order = new Order()
        .id(1L)
        .petId(2L)
        .quantity(100)
        .complete(true)
        .shipDate(new Date().toInstant().atOffset(ZoneOffset.UTC))
        .status(Order.StatusEnum.PLACED);

    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .header("X-Session-ID", SessionHelper.getSession("testuser1"))
        .body(order)
        .when()
        .post("/api/petstore/v1/store/order")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("petId", is(2))
        .body("quantity", is(100))
        .body("complete", is(true))
        .body("shipDate", notNullValue())
        .body("status", is(Order.StatusEnum.PLACED.getValue()))
        .body(matchesJsonSchemaInClasspath("schemata/order-schema.json"));
  }

}
