package de.openapi.petstore.controller;

import de.openapi.petstore.api.UserApi;
import de.openapi.petstore.model.Session;
import de.openapi.petstore.model.User;
import de.openapi.petstore.service.SessionService;
import de.openapi.petstore.service.UserService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petstore/v1")
@Observed(name = "de.openapi.petstore.UserApi")
public class UserApiController implements UserApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserApiController.class);

  private final UserService userService;
  private final SessionService sessionService;

  @Autowired
  public UserApiController(UserService userService, SessionService sessionService) {
    this.userService = userService;
    this.sessionService = sessionService;
  }

  public ResponseEntity<User> createUser(
      @Parameter(name = "User", description = "Created user object", schema = @Schema(description = "")) @Valid @RequestBody(required = false) User user) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#createUser",
        getClass().getCanonicalName());

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    var addedUser = userService.addUser(user);
    return ResponseEntity.ok().body(addedUser);
  }

  public ResponseEntity<Void> deleteUser(
      @NotNull @Parameter(name = "username", description = "The name that needs to be deleted", required = true, schema = @Schema(description = "")) @PathVariable("username") String username) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#deleteUser",
        getClass().getCanonicalName());

    if (username == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    userService.deleteUser(username);
    var user = userService.findUserByName(username);

    if (user == null) {
      return ResponseEntity.ok().body(null);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(null);
    }
  }

  public ResponseEntity<User> getUserByName(
      @NotNull @Parameter(name = "username", description = "The name that needs to be fetched. Use user1 for testing. ", required = true, schema = @Schema(description = "")) @PathVariable("username") String username) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#getUserByName",
        getClass().getCanonicalName());

    if (username == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    var user = userService.findUserByName(username);
    if (user != null) {
      return ResponseEntity.ok().body(user);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  public ResponseEntity<String> loginUser(
      @Parameter(name = "username", description = "The user name for login", schema = @Schema(description = "")) @Valid @RequestParam(value = "username", required = false) String username,
      @Parameter(name = "password", description = "The password for login in clear text", schema = @Schema(description = "")) @Valid @RequestParam(value = "password", required = false) String password) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#loginUser",
        getClass().getCanonicalName());

    if (username == null || password == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    var session = userService.loginUser(username, password);
    if (session != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(session);
    }
    var date = new Date(System.currentTimeMillis() + 3600000);
    return ResponseEntity.ok()
        .header("X-Rate-Limit", String.valueOf(5000))
        .header("X-Expires-After", date.toString())
        .body(this.sessionService.createSession(new Session().username(username)).getId());
  }

  public ResponseEntity<Void> logoutUser(
      @Parameter(name = "X-Session-ID", description = "", schema = @Schema(description = "")) @RequestHeader(value = "X-Session-ID", required = false) String xSessionID) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#logoutUser",
        getClass().getCanonicalName());

    var session = sessionService.getSessionById(xSessionID);
    if (session != null) {
      sessionService.deleteSessionById(xSessionID);
    }
    userService.logoutUser();
    return ResponseEntity.ok().body(null);
  }

  public ResponseEntity<Void> updateUser(
      @NotNull @Parameter(name = "username", description = "name that need to be deleted", required = true, schema = @Schema(description = "")) @PathVariable("username") String username,
      @Parameter(name = "User", description = "Update an existent user in the store", schema = @Schema(description = "")) @Valid @RequestBody(required = false) User user) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#updateUser",
        getClass().getCanonicalName());

    if (username == null || user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    var updatedUser = userService.updateUser(username, user);
    if (updatedUser != null) {
      return ResponseEntity.ok().body(null);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }
}
