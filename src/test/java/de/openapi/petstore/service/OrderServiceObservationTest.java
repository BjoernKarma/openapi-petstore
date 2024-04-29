package de.openapi.petstore.service;

import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;

import de.openapi.petstore.config.EnableTestObservation;
import de.openapi.petstore.db.OrderRepository;
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
@SpringBootTest(classes = OrderService.class)
@EnableAutoConfiguration
@EnableTestObservation
@TestPropertySource(properties = {
    "spring.profiles.active=local",
    "spring.application.name=openapi-petstore"})
class OrderServiceObservationTest {

  @Autowired
  OrderService orderService;
  @MockBean
  OrderRepository orderRepository;
  @Autowired
  TestObservationRegistry registry;
  @Autowired
  SimpleTracer tracer;

  @Test
  void testObservation() {
    orderService.getOrderById(1L);
    assertThat(registry)
        .hasObservationWithNameEqualTo("de.openapi.petstore.OrderService")
        .that()
        .hasBeenStarted()
        .hasBeenStopped();

    TracingAssertions.assertThat(tracer)
        .onlySpan()
        .hasNameEqualTo("order-service#get-order-by-id")
        .hasTag("peer.service", "openapi-petstore")
        .hasTag("environment.info", "dev")
        .isEnded();
  }

}
