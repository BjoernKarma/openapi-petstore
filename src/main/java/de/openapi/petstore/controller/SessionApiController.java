package de.openapi.petstore.controller;

import de.openapi.petstore.api.SessionApi;
import de.openapi.petstore.model.Session;
import de.openapi.petstore.service.SessionService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petstore/v1")
@Observed(name = "de.openapi.petstore.SessionApi")
public class SessionApiController implements SessionApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionApiController.class);

  private final SessionService sessionService;

  @Autowired
  public SessionApiController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  public ResponseEntity<Session> createSession(
      @Parameter(name = "username", description = "The name that needs a session", required = true, schema = @Schema(description = "")) @PathVariable("username") String username) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#createSession",
        getClass().getCanonicalName());

    Session session = new Session().username(username);
    return ResponseEntity.ok().body(this.sessionService.createSession(session));
  }

  public ResponseEntity<Session> getSession(
      @Parameter(name = "id", description = "The session id to retrieve", required = true, schema = @Schema(description = "")) @PathVariable("id") String id) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#getSession",
        getClass().getCanonicalName());

    var session = this.sessionService.getSessionById(id);
    return session != null ? ResponseEntity.ok().body(session) : ResponseEntity.notFound().build();
  }

  public ResponseEntity<Void> deleteSession(
      @Parameter(name = "id", description = "The session id to delete", required = true, schema = @Schema(description = "")) @PathVariable("id") String id) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#deleteSession",
        getClass().getCanonicalName());

    this.sessionService.deleteSessionById(id);
    return ResponseEntity.ok().build();
  }
}
