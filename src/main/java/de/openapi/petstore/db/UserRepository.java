package de.openapi.petstore.db;

import de.openapi.petstore.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    void deleteUserByUsername(String username);
    User findUserByUsername(String username);
}
