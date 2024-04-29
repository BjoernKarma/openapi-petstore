package de.openapi.petstore.db;

import de.openapi.petstore.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {

  void deleteById(String id);

  Session findSessionById(String id);
}
