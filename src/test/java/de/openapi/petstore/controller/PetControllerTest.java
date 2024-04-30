package de.openapi.petstore.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import de.openapi.petstore.auth.SessionHelper;
import de.openapi.petstore.model.Category;
import de.openapi.petstore.model.Pet;
import de.openapi.petstore.model.Tag;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.List;
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
class PetControllerTest {

  private RequestSpecification spec;

  @Value("${local.server.port}")
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @Test
  void shouldAddPet() {
    var category = new Category();
    category.setId(5L);
    category.name("Elephants");

    var pet = new Pet()
        .id(11L)
        .name("elephant")
        .category(category)
        .photoUrls(List.of("url1", "url2"))
        .tags(List.of(new Tag().id(1L).name("tag 1")));

    pet.setStatus(Pet.StatusEnum.AVAILABLE);

    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(pet)
        .when()
        .post("/api/petstore/v1/pet")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("name", is("elephant"))
        .body("category.id", is(5))
        .body("category.name", is("Elephants"))
        .body("photoUrls", hasItems("url1", "url2"))
        .body("tags.id", is(List.of(1)))
        .body("status", is(Pet.StatusEnum.AVAILABLE.getValue()))
        .body(matchesJsonSchemaInClasspath("schemata/pet-schema.json"));
  }

  @Test
  void shouldDeletePet() {
    var petId = 3;
    given()
        .when()
        .delete("/api/petstore/v1/pet/" + petId)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .header("Content-Length", is("0"));
  }

  @Test
  void shouldReturnPetByStatus() {
    given()
        .accept(ContentType.JSON)
        .param("status", "sold")
        .when()
        .get("/api/petstore/v1/pet/findByStatus")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("id", is(List.of(5)))
        .body(matchesJsonSchemaInClasspath("schemata/pets-schema.json"));
  }

  @Test
  void shouldReturnPetById() {
    var petId = 1;
    given()
        .accept(ContentType.JSON)
        .when()
        .get("/api/petstore/v1/pet/" + petId)
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("id", is(petId))
        .body("name", is("Cat 1"))
        .body("category.id", is(2))
        .body("category.name", is("Cats"))
        .body("photoUrls", hasItems("cat1.jpg"))
        .body("tags.id", is(List.of(1, 2)))
        .body("tags.name", is(List.of("tag1", "tag2")))
        .body("status", is(Pet.StatusEnum.AVAILABLE.getValue()))
        .body(matchesJsonSchemaInClasspath("schemata/pet-schema.json"));
  }

  @Test
  void shouldUpdatePet() {
    var category = new Category();
    category.setId(5L);
    category.name("Elephants");

    var pet = new Pet()
        .id(5L)
        .name("elephant")
        .category(category)
        .photoUrls(List.of("url1", "url2"))
        .tags(List.of(new Tag().id(1L).name("tag 1")));

    pet.setStatus(Pet.StatusEnum.SOLD);

    given()
        .accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .header("X-Session-ID", SessionHelper.getSession("testuser1"))
        .body(pet)
        .when()
        .put("/api/petstore/v1/pet")
        .then()
        .log().all(true)
        .statusCode(HttpStatus.OK.value())
        .and()
        .body("id", is(5))
        .body("name", is("elephant"))
        .body("category.id", is(5))
        .body("category.name", is("Elephants"))
        .body("photoUrls", hasItems("url1", "url2"))
        .body("tags.id", is(List.of(1)))
        .body("status", is(Pet.StatusEnum.SOLD.getValue()))
        .body(matchesJsonSchemaInClasspath("schemata/pet-schema.json"));
  }
}
