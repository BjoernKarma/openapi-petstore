package de.openapi.petstore.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.List;

import de.openapi.petstore.db.PetRepository;
import de.openapi.petstore.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

  @Mock
  private PetRepository petRepository;
  private PetService petService;

  @BeforeEach
  public void setUp() {
    petService = new PetService(petRepository, "mock");
    Mockito.reset(petRepository);
  }

  @Test
  public void shouldReturnPetByStatus() {
    when(petRepository.findByStatusIn(any())).thenReturn(List.of(new Pet().id(1L)));

    var pets = petService.findPetsByStatus("available,sold");

    verify(petRepository, times(1)).findByStatusIn(List.of("AVAILABLE", "SOLD"));
    assertThat(pets.size(), equalTo(1));
    assertThat(pets.get(0).getId(), equalTo(1L));
  }

  @Test
  public void shouldReturnPet() {
    when(petRepository.findById(anyLong())).thenReturn(new Pet().id(1L));

    var pet = petService.getPetById(1L);

    verify(petRepository, times(1)).findById(1L);
    assertThat(pet.getId(), equalTo(1L));
  }

  @Test
  public void shouldUpdateNewPetWithForm() {
    when(petRepository.findById(anyLong())).thenReturn(null);

    var pet = petService.updatePetWithForm(1L, "pet 2", "available");

    verify(petRepository, times(1)).findById(1L);
    verify(petRepository, times(0)).save(any());
    assertThat(pet, is(nullValue()));
  }

  @Test
  public void shouldDeletePet() {
    petService.deletePet(1L);
    verify(petRepository, times(1)).deleteById(1L);
  }

  @Test
  public void shouldAddPet() {
    var newPet = new Pet().id(1L).name("pet 1").status(Pet.StatusEnum.PENDING);
    var savedPet = new Pet().id(2L).name("pet 1").status(Pet.StatusEnum.PENDING);
    when(petRepository.save(any())).thenReturn(savedPet);

    var pet = petService.addPet(newPet);

    assertThat(pet.getId(), equalTo(2L));
    assertThat(pet.getName(), equalTo("pet 1"));
    assertThat(pet.getStatus(), equalTo(Pet.StatusEnum.PENDING));
  }

  @Test
  public void shouldUpdateExistingPet() {
    var existingPet = new Pet().id(1L).name("pet 1").status(Pet.StatusEnum.PENDING);
    var newPet = new Pet().id(1L).name("pet 2").status(Pet.StatusEnum.AVAILABLE);
    when(petRepository.save(any())).thenReturn(existingPet);

    var pet = petService.updatePet(newPet);

    verify(petRepository, times(1)).save(newPet);
    assertThat(pet.getId(), equalTo(1L));
    assertThat(pet.getName(), equalTo("pet 1"));
    assertThat(pet.getStatus(), equalTo(Pet.StatusEnum.PENDING));
  }
}
