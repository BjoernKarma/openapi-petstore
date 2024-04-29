package de.openapi.petstore.service;

import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;

import de.openapi.petstore.config.EnableTestObservation;
import de.openapi.petstore.db.UserRepository;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.test.simple.SimpleTracer;
import io.micrometer.tracing.test.simple.TracingAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserService.class)
@EnableAutoConfiguration
@EnableTestObservation
@TestPropertySource(properties = {
    "spring.profiles.active=local",
    "spring.application.name=openapi-petstore"})
class UserServiceObservationTest {

  @Autowired
  UserService userService;
  @MockBean
  UserRepository userRepository;
  @Autowired
  TestObservationRegistry registry;
  @Autowired
  SimpleTracer tracer;

  @Test
  void testObservation() {
    userService.findUserByName("testUser1");
    assertThat(registry)
        .hasObservationWithNameEqualTo("de.openapi.petstore.UserService")
        .that()
        .hasBeenStarted()
        .hasBeenStopped();

    TracingAssertions.assertThat(tracer)
        .onlySpan()
        .hasNameEqualTo("user-service#find-user-by-name")
        .hasTag("peer.service", "openapi-petstore")
        .hasTag("environment.info", "dev")
        .isEnded();
  }

}
