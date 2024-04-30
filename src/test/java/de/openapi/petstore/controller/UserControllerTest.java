package de.openapi.petstore.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import de.openapi.petstore.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
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
class UserControllerTest {

  private RequestSpecification spec;

  @Value("${local.server.port}")
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Test
  void shouldCreateUser() {
    var user = new User()
        .id(12L)
        .username("user12")
        .firstName("first name 12")
        .lastName("last name 12")
        .password("XXXX")
        .email("email12@test.com")
        .phone("123-456-7890")
        .userStatus(4);

    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(user)
        .when()
        .post("/api/petstore/v1/user")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("username", is("user12"))
        .body("firstName", is("first name 12"))
        .body("lastName", is("last name 12"))
        .body("email", is("email12@test.com"))
        .body("password", not(emptyOrNullString()))
        .body("phone", is("123-456-7890"))
        .body("userStatus", is(4))
        .body(matchesJsonSchemaInClasspath("schemata/user-schema.json"));
  }

  @Test
  void shouldDeleteUser() {
    var userName = "user2";
    given()
        .when()
        .delete("/api/petstore/v1/user/" + userName)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .header("Content-Length", is("0"));
  }

  @Test
  void shouldReturnUser() {
    var username = "testuser1";
    given()
        .accept("application/json")
        .when()
        .get("/api/petstore/v1/user/" + username)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("id", is(1))
        .body("username", is(username))
        .body("firstName", is("Test"))
        .body("lastName", is("User 1"))
        .body("email", is("testuser1@github.com"))
        .body("password", not(emptyOrNullString()))
        .body("phone", is("0228/1813450"))
        .body("userStatus", is(1))
        .body(matchesJsonSchemaInClasspath("schemata/user-schema.json"));
  }

  @Test
  void shouldLoginUser() {
    var userName = "testuser3";
    var password = "pass3";

    given()
        .accept(ContentType.JSON)
        .queryParam("username", userName)
        .queryParam("password", password)
        .when()
        .get("/api/petstore/v1/user/login")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  void shouldLogoutUser() {
    given()
        .when()
        .get("/api/petstore/v1/user/logout")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .header("Content-Length", is("0"));
  }

  @Test
  void shouldUpdateUser() {
    var userName = "testuser4";
    var user = new User()
        .id(15L)
        .username(userName)
        .firstName("first name 15")
        .lastName("last name 15")
        .password("XXXX")
        .email("email15@test.com")
        .phone("123-456-7890")
        .userStatus(4);

    given()
        .contentType(ContentType.JSON)
        .body(user)
        .when()
        .put("/api/petstore/v1/user/" + userName)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .header("Content-Length", is("0"));
  }
}
