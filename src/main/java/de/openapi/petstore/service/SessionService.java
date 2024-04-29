package de.openapi.petstore.service;

import de.openapi.petstore.db.SessionRepository;
import de.openapi.petstore.model.Session;
import io.micrometer.observation.annotation.Observed;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

// tag::javadoc[]

/**
 * A service that provides sessions.
 */
// end::javadoc[]
@Component
@Observed(name = "de.openapi.petstore.SessionService")
public class SessionService {

  private static String generateRandomPassword(int len) {
    // ASCII range – alphanumeric (0-9, a-z, A-Z)
    final var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    var random = new SecureRandom();
    var sb = new StringBuilder();

    // each iteration of the loop randomly chooses a character from the given
    // ASCII range and appends it to the `StringBuilder` instance

    for (var i = 0; i < len; i++) {
      var randomIndex = random.nextInt(chars.length());
      sb.append(chars.charAt(randomIndex));
    }

    return sb.toString();
  }


  private final SessionRepository sessionRepository;

  public SessionService(@Autowired SessionRepository sessionRepository,
      @Autowired MongoTemplate mongoTemplate) {
    this.sessionRepository = sessionRepository;
    mongoTemplate.indexOps(Session.class)
        .ensureIndex(new Index().on("expireAt", Sort.Direction.ASC).expire(0));
  }

  public Session getSessionById(String sessionId) {
    return this.sessionRepository.findSessionById(sessionId);
  }

  public void deleteSessionById(String sessionId) {
    this.sessionRepository.deleteById(sessionId);
  }

  public Session createSession(Session session) {
    session.setExpireAt(OffsetDateTime.now().plusMinutes(30));
    session.setId(generateRandomPassword(32));
    return this.sessionRepository.save(session);
  }
}
