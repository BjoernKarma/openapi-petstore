package de.openapi.petstore.config;

import de.openapi.petstore.configuration.ObservedAspectConfiguration;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.test.simple.SimpleTracer;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AutoConfigureObservability
@Import({
    ObservedAspectConfiguration.class,
    EnableTestObservation.ObservationTestConfiguration.class
})
public @interface EnableTestObservation {

  @TestConfiguration
  class ObservationTestConfiguration {

    @Bean
    TestObservationRegistry observationRegistry() {
      return TestObservationRegistry.create();
    }

    @Bean
    SimpleTracer simpleTracer() {
      return new SimpleTracer();
    }

  }

}
