package de.openapi.petstore.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import de.openapi.petstore.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testMongo")
class UserControllerErrorResponseTest {

  private static final String ERROR_MESSAGE = "This is a critical runtime error!";

  @MockBean
  private UserService userService;

  @Value("${local.server.port}")
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Test
  void getUser_RuntimeException_thenServerError() {
    var username = "user1";
    Mockito.when(userService.findUserByName(username))
        .thenThrow(new RuntimeException(ERROR_MESSAGE));
    given()
        .accept("application/json")
        .when()
        .get("/api/petstore/v1/user/" + username)
        .then()
        .log().all(true)
        .statusCode(500)
        .and()
        .body("message", equalTo(ERROR_MESSAGE));
  }


}
