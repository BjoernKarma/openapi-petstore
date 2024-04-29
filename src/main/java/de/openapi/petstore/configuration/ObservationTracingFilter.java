package de.openapi.petstore.configuration;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationFilter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class ObservationTracingFilter implements ObservationFilter {

  @Value("${ENVIRONMENT_NAME}")
  private String environmentInfo;
  @Value("${spring.application.name}")
  private String serviceName;

  @Override
  public Context map(Context context) {
    context.addLowCardinalityKeyValue(
        KeyValue.of("peer.service", Optional.ofNullable(serviceName).orElse("???")));
    context.addLowCardinalityKeyValue(
        KeyValue.of("environment.info", Optional.ofNullable(environmentInfo).orElse("???")));
    return context;
  }

}