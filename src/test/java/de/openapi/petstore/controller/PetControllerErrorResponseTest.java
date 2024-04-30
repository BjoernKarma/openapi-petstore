package de.openapi.petstore.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import de.openapi.petstore.service.PetService;
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
class PetControllerErrorResponseTest {

  private static final String ERROR_MESSAGE = "This is a critical runtime error!";

  @MockBean
  private PetService petService;

  @Value("${local.server.port}")
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Test
  void getPet_RuntimeException_thenServerError() {
    var petId = 1;
    Mockito.when(petService.getPetById((long) petId))
        .thenThrow(new RuntimeException(ERROR_MESSAGE));
    given()
        .accept("application/json")
        .when()
        .get("/api/petstore/v1/pet/" + petId)
        .then()
        .log().all(true)
        .statusCode(500)
        .and()
        .body("message", equalTo(ERROR_MESSAGE));
  }


}
