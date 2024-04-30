package de.openapi.petstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.profiles.active=testMongo"})
class PetstoreServiceTests {

  @Test
  void contextLoads() {
  }

}
