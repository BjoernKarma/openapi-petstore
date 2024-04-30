package de.openapi.petstore.service;

import de.openapi.petstore.db.PetRepository;
import de.openapi.petstore.model.Category;
import de.openapi.petstore.model.Pet;
import de.openapi.petstore.model.Pet.StatusEnum;
import de.openapi.petstore.model.Tag;
import io.micrometer.observation.annotation.Observed;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// tag::javadoc[]

/**
 * A service that provides pets.
 */
// end::javadoc[]
@Component
@Observed(name = "de.openapi.petstore.PetService")
public class PetService {

  private final PetRepository petRepository;

  public PetService(@Autowired PetRepository petRepository, @Value("${openapi.pet.enabled}") String enabled) {
    this.petRepository = petRepository;
    if (enabled.equals("mock") && petRepository.findAll().isEmpty()) {
      // initialization of db
      List<Category> categories = new ArrayList<>();
      List<Pet> pets = new ArrayList<>();
      categories.add(createCategory(1, "Dogs"));
      categories.add(createCategory(2, "Cats"));
      categories.add(createCategory(3, "Rabbits"));
      categories.add(createCategory(4, "Lions"));

      pets.add(createPet(1, categories.get(1), "Cat 1", new String[]{"cat1.jpg"},
          new String[]{"tag1", "tag2"}, StatusEnum.AVAILABLE));
      pets.add(createPet(2, categories.get(1), "Cat 2", new String[]{"cat2.jpg"},
          new String[]{"tag2", "tag3"}, StatusEnum.AVAILABLE));
      pets.add(createPet(3, categories.get(1), "Cat 3", new String[]{"cat3.jpg"},
          new String[]{"tag3", "tag4"}, StatusEnum.PENDING));

      pets.add(createPet(4, categories.get(0), "Dog 1", new String[]{"dog1.jpg"},
          new String[]{"tag1", "tag2"},
          StatusEnum.AVAILABLE));
      pets.add(createPet(5, categories.get(0), "Dog 2", new String[]{"dog2.jpg"},
          new String[]{"tag2", "tag3"}, StatusEnum.SOLD));
      pets.add(createPet(6, categories.get(0), "Dog 3", new String[]{"dog3.jpg"},
          new String[]{"tag3", "tag4"}, StatusEnum.PENDING));

      pets.add(createPet(7, categories.get(3), "Lion 1", new String[]{"lion1.jpg"},
          new String[]{"tag1", "tag2"}, StatusEnum.AVAILABLE));
      pets.add(createPet(8, categories.get(3), "Lion 2", new String[]{"lion2.jpg"},
          new String[]{"tag2", "tag3"}, StatusEnum.AVAILABLE));
      pets.add(createPet(9, categories.get(3), "Lion 3", new String[]{"lion3.jpg"},
          new String[]{"tag3", "tag4"}, StatusEnum.AVAILABLE));

      pets.add(createPet(10, categories.get(2), "Rabbit 1", new String[]{"rabbit.jpg"},
          new String[]{"tag3", "tag4"}, StatusEnum.AVAILABLE));
      this.petRepository.saveAll(pets);
    }
  }

  private static Pet createPet(final long id, final Category cat, final String name,
      final String[] urls,
      final String[] tags, final StatusEnum status) {
    final var pet = new Pet();
    pet.setId(id);
    pet.setCategory(cat);
    pet.setName(name);
    if (null != urls) {
      final List<String> urlObjs = new ArrayList<>(Arrays.asList(urls));
      pet.setPhotoUrls(urlObjs);
    }
    final List<Tag> tagObjs = new ArrayList<>();
    var i = 0;
    if (null != tags) {
      for (final String tagString : tags) {
        i = i + 1;
        final var tag = new Tag();
        tag.setId((long) i);
        tag.setName(tagString);
        tagObjs.add(tag);
      }
    }
    pet.setTags(tagObjs);
    pet.setStatus(status);
    return pet;
  }

  private static Category createCategory(final long id, final String name) {
    final var category = new Category();
    category.setId(id);
    category.setName(name);
    return category;
  }

  public List<Pet> findPetsByStatus(final String status) {
    return petRepository.findByStatusIn(
        Arrays.stream(status.split(","))
            .map(String::toUpperCase)
            .map(String::trim)
            .collect(Collectors.toList())
    );
  }

  public Pet getPetById(final Long petId) {
    return petRepository.findById(petId);
  }

  public Pet updatePetWithForm(final Long petId,
      final String name, final String status) {
    final var existingPet = petRepository.findById(petId);

    if (existingPet == null) {
      return null;
    }

    existingPet.setName(name);
    existingPet.setStatus(StatusEnum.fromValue(status));
    return petRepository.save(existingPet);
  }

  public void deletePet(final Long petId) {
    petRepository.deleteById(petId);
  }

  public Pet uploadFile(final Long petId, final File file) {
    final var existingPet = petRepository.findById(petId);
    if (existingPet == null) {
      return null;
    }

    existingPet.getPhotoUrls().add(file.getAbsolutePath());

    return petRepository.save(existingPet);
  }

  public Pet addPet(final Pet pet) {
    pet.setId(new SecureRandom().nextLong());
    return petRepository.save(pet);
  }

  public Pet updatePet(final Pet pet) {
    return petRepository.save(pet);
  }

  public List<Pet> findPetsByTags(final List<String> tags) {
    return petRepository.findAll()
        .stream()
        .filter(p -> p.getTags().stream().anyMatch(t -> tags.contains(t.getName())))
        .collect(Collectors.toList());
  }

}
