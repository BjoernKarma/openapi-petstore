package de.openapi.petstore.db;

import de.openapi.petstore.model.Pet;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PetRepository extends MongoRepository<Pet, String> {
    Pet findById(Long id);
    void deleteById(Long id);
    List<Pet> findByStatusIn(List<String> status);
}
