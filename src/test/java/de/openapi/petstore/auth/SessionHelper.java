package de.openapi.petstore.auth;

import static io.restassured.RestAssured.given;

import de.openapi.petstore.model.Session;
import io.restassured.http.ContentType;

public class SessionHelper {
    public static String getSession(String username) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/petstore/v1/session/user/"+username)
                .as(Session.class)
                .getId();
    }
}
